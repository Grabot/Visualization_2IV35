/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import gui.RaycastRendererPanel;
import gui.TransferFunctionEditor;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.media.opengl.GL2;
import util.PixelThreadPoolChangeListener;
import util.PixelThreadPoolExecutor;
import util.RendererChangeListener;
import util.TFChangeListener;
import util.VectorMath;
import volume.Volume;

/**
 *
 * @author michel
 */
public class RaycastRenderer extends Renderer implements TFChangeListener, PixelThreadPoolChangeListener {

    private Volume volume = null;
    private Volume levoyVolume = null;
    private BufferedImage image;
    private double[] viewMatrix = new double[4 * 4];

    RaycastRendererPanel panel;
    TransferFunction tFunc;
    TransferFunctionEditor tfEditor;

    ArrayList<RendererChangeListener> rendererChangeListeners = new ArrayList<RendererChangeListener>();
    RendererTypes type = RendererTypes.Slicer;

    PixelThreadPoolExecutor pixelThreadPoolExecutor = null;

    public RaycastRenderer() {
        panel = new RaycastRendererPanel(this);
        panel.setSpeedLabel("0");
    }

    public void setRendererType(RendererTypes type) {
        this.type = type;
        StartRenderer(viewMatrix);
    }

    public void setVolume(Volume vol) {
        volume = vol;

        // set up image for storing the resulting rendering
        // the image width and height are equal to the length of the volume diagonal
        int imageSize = (int) Math.floor(Math.sqrt(vol.getDimX() * vol.getDimX() + vol.getDimY() * vol.getDimY()
                + vol.getDimZ() * vol.getDimZ()));
        if (imageSize % 2 != 0) {
            imageSize = imageSize + 1;
        }
        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB_PRE);
        tFunc = new TransferFunction(volume.getMinimum(), volume.getMaximum(), volume);
        tFunc.addTFChangeListener(this);
        tfEditor = new TransferFunctionEditor(tFunc, volume.getHistogram());
        panel.setTransferFunctionEditor(tfEditor);

    }

    @Override
    public void changed() {
        StartRenderer(viewMatrix);
        for (TFChangeListener listener : listeners) {
            listener.changed();
        }
    }

    public RaycastRendererPanel getPanel() {
        return panel;
    }

    public TransferFunction tfunction()
    {
        return tFunc;
    }
    
    private short getVoxel(double[] coord) {
        return getVoxel(coord, volume);
    }

    // get a voxel from the volume data by nearest neighbor interpolation
    private short getVoxel(double[] coord, Volume volume) {

        //threadLock.lock();
        short value = 0;

        try {

            double x = coord[0];
            double y = coord[1];
            double z = coord[2];

            if ((x >= 0) && (x < volume.getDimX()) && (y >= 0) && (y < volume.getDimY())
                    && (z >= 0) && (z < volume.getDimZ())) {

                int x0 = (int) Math.floor(x);
                int y0 = (int) Math.floor(y);
                int z0 = (int) Math.floor(z);

                int x1 = (int) Math.ceil(x);
                int y1 = (int) Math.ceil(y);
                int z1 = (int) Math.ceil(z);

                int xd = (int) ((x - x0) / (x1 - x0));
                int yd = (int) ((y - y0) / (y1 - y0));
                int zd = (int) ((z - z0) / (z1 - z0));

                double c00 = volume.getVoxel(x0, y0, z0) * (1 - xd) + volume.getVoxel(x1, y0, z0) * xd;
                double c10 = volume.getVoxel(x0, y1, z0) * (1 - xd) + volume.getVoxel(x1, y1, z0) * xd;
                double c01 = volume.getVoxel(x0, y0, z1) * (1 - xd) + volume.getVoxel(x1, y0, z1) * xd;
                double c11 = volume.getVoxel(x0, y1, z1) * (1 - xd) + volume.getVoxel(x1, y1, z1) * xd;

                double c0 = c00 * (1 - yd) + c10 * yd;
                double c1 = c01 * (1 - yd) + c11 * yd;

                value = (short) (c0 * (1 - zd) + c1 * zd);
            }
        } catch (Exception e) {
        } finally {
            return value;
        }
    }

    public void StartRenderer(double[] viewMatrix) {

        if (volume == null) {
            return;
        }

        // Get orientation
        // Interupt all previous tasks
        if (pixelThreadPoolExecutor != null) {
            pixelThreadPoolExecutor.shutdownNow();

            // Await shutdown
            try {
                pixelThreadPoolExecutor.awaitTermination(200, TimeUnit.MILLISECONDS);
                pixelThreadPoolExecutor.purge();
            } catch (InterruptedException ex) {
                //Logger.getLogger(RaycastRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Create new pool for image
        pixelThreadPoolExecutor = new PixelThreadPoolExecutor(1, 1, 500, TimeUnit.MILLISECONDS);
        pixelThreadPoolExecutor.addListener(this);

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        final double[] viewVec = new double[3];
        final double[] uVec = new double[3];
        final double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);

        // image is square
        int imageCenter = image.getWidth() / 2;

        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);

        double max = volume.getMaximum();

        // ArrayList of subpixel tasks
        ArrayList<Runnable> subPixelQueue = new ArrayList<Runnable>();
        ArrayList<Runnable> subsubPixelQueue = new ArrayList<Runnable>();

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {

                Runnable runnable;

                if (type == RendererTypes.Compositing) {
                    if ((i % 4 == 0) && (j % 4 == 0)) {
                        runnable = new COMPThread(i, j, 4, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        runnable.run();
                    } else if ((i % 2 == 0) && (j % 2 == 0)) {
                        runnable = new COMPThread(i, j, 2, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        subPixelQueue.add(runnable);
                    } else {
                        runnable = new COMPThread(i, j, 1, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        subsubPixelQueue.add(runnable);
                    }
                }

                if (type == RendererTypes.MIP) {
                    if ((i % 4 == 0) && (j % 4 == 0)) {
                        runnable = new MIPThread(i, j, 4, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        runnable.run();
                    } else if ((i % 2 == 0) && (j % 2 == 0)) {
                        runnable = new MIPThread(i, j, 2, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        subPixelQueue.add(runnable);
                    } else {
                        runnable = new MIPThread(i, j, 1, imageCenter, pixelCoord, volumeCenter, viewVec, uVec, vVec);
                        subsubPixelQueue.add(runnable);
                    }
                }

                if (type == RendererTypes.Slicer) {
                    pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                            + volumeCenter[0];
                    pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                            + volumeCenter[1];
                    pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                            + volumeCenter[2];

                    int val = getVoxel(pixelCoord);
                    // Apply the transfer function to obtain a color
                    TFColor voxelColor = tFunc.getColor(val);

                    // BufferedImage expects a pixel color packed as ARGB in an int
                    int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                    int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                    int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                    int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                    int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                    image.setRGB(i, j, pixelColor);
                }
            }
        }

        // Add other tasks to threadque
        for (Runnable runnable : subPixelQueue) {
            pixelThreadPoolExecutor.execute(runnable);
        }
        
        for (Runnable runnable : subsubPixelQueue) {
            pixelThreadPoolExecutor.execute(runnable);
        }

        pixelThreadPoolExecutor.shutdown();

        RenderingCompleted();
    }

    private void drawBoundingBox(GL2 gl) {
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor4d(1.0, 1.0, 1.0, 1.0);
        gl.glLineWidth(1.5f);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
    }

    @Override
    public void visualize(GL2 gl) {

        if (volume == null) {
            return;
        }

        drawBoundingBox(gl);

        double[] oldViewMatrix = viewMatrix.clone();
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, viewMatrix, 0);

        if (!Arrays.equals(oldViewMatrix, viewMatrix)) {
            StartRenderer(viewMatrix);
        }

        //        long startTime = System.currentTimeMillis();
        //        long endTime = System.currentTimeMillis();
        //        double runningTime = (endTime - startTime);
        //        panel.setSpeedLabel(Double.toString(runningTime));
        Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // draw rendered image as a billboard texture
        texture.enable(gl);
        texture.bind(gl);
        double halfWidth = image.getWidth() / 2.0;
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-halfWidth, -halfWidth, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(-halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(halfWidth, -halfWidth, 0.0);
        gl.glEnd();
        texture.disable(gl);
        texture.destroy(gl);
        gl.glPopMatrix();

        gl.glPopAttrib();

        if (gl.glGetError() > 0) {
            System.out.println("some OpenGL error: " + gl.glGetError());
        }

    }

    @Override
    public void OnProgressUpdated() {
        RenderingCompleted();
    }

    class MIPThread implements Runnable {

        int i;
        int j;
        int res;
        int imageCenter;

        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];

        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];

        MIPThread(int i, int j, int res, int imageCenter, double[] pixelCoord, double[] volumeCenter, double[] viewVec, double[] uVec, double[] vVec) {
            this.i = i;
            this.j = j;
            this.res = res;
            this.imageCenter = imageCenter;
            this.pixelCoord = pixelCoord;
            this.volumeCenter = volumeCenter;
            this.viewVec = viewVec;
            this.uVec = uVec;
            this.vVec = vVec;
        }

        @Override
        public void run() {
            int maxVal = 0;
            double maxRange = Math.abs(viewVec[0]) > (Math.abs(viewVec[1]) > Math.abs(viewVec[2]) ? volume.getDimY() : volume.getDimZ()) ? volume.getDimX() : (Math.abs(viewVec[1]) > Math.abs(viewVec[2]) ? volume.getDimY() : volume.getDimZ());

            for (int n = 0; n < maxRange; n++) {
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                        + viewVec[0] * (n - (maxRange / 2)) + volumeCenter[0];
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                        + viewVec[1] * (n - (maxRange / 2)) + volumeCenter[1];
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                        + viewVec[2] * (n - (maxRange / 2)) + volumeCenter[2];

                int val = getVoxel(pixelCoord);

                if (val > maxVal) {
                    maxVal = val;
                }
            }

            // Apply the transfer function to obtain a color
            TFColor voxelColor = tFunc.getColor(maxVal);

            // BufferedImage expects a pixel color packed as ARGB in an int
            int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
            int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
            int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
            int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
            int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;

            // Set multiple pixels at lower resolution
            for (int ri = 0; ri < res; ri++) {
                for (int rj = 0; rj < res; rj++) {
                    if ((i + ri < image.getHeight()) && (j + rj < image.getWidth())) {
                        image.setRGB(i + ri, j + rj, pixelColor);
                    }
                }
            }
        }
    }

    class COMPThread implements Runnable {

        int i;
        int j;
        int res;
        int imageCenter;

        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];

        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];

        COMPThread(int i, int j, int res, int imageCenter, double[] pixelCoord, double[] volumeCenter, double[] viewVec, double[] uVec, double[] vVec) {
            this.i = i;
            this.j = j;
            this.res = res;
            this.imageCenter = imageCenter;
            this.pixelCoord = pixelCoord;
            this.volumeCenter = volumeCenter;
            this.viewVec = viewVec;
            this.uVec = uVec;
            this.vVec = vVec;
        }

        @Override
        public void run() {
            TFColor compColor = new TFColor(0, 0, 0, 1);
            double maxRange = Math.abs(viewVec[0]) > (Math.abs(viewVec[1]) > Math.abs(viewVec[2]) ? volume.getDimY() : volume.getDimZ()) ? volume.getDimX() : (Math.abs(viewVec[1]) > Math.abs(viewVec[2]) ? volume.getDimY() : volume.getDimZ());

            for (int n = 0; n < maxRange; n++) {
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                        + viewVec[0] * (n - (maxRange / 2)) + volumeCenter[0];
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                        + viewVec[1] * (n - (maxRange / 2)) + volumeCenter[1];
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                        + viewVec[2] * (n - (maxRange / 2)) + volumeCenter[2];

                int val = getVoxel(pixelCoord);

                // Apply the transfer function to obtain a color
                TFColor voxelColor = tFunc.getColor(val);

                compColor.r = voxelColor.r * voxelColor.a + (1 - voxelColor.a) * compColor.r;
                compColor.g = voxelColor.g * voxelColor.a + (1 - voxelColor.a) * compColor.g;
                compColor.b = voxelColor.b * voxelColor.a + (1 - voxelColor.a) * compColor.b;
            }

            // BufferedImage expects a pixel color packed as ARGB in an int;
            int c_alpha = compColor.a <= 1.0 ? (int) Math.floor(compColor.a * 255) : 255;
            int c_red = compColor.r <= 1.0 ? (int) Math.floor(compColor.r * 255) : 255;
            int c_green = compColor.g <= 1.0 ? (int) Math.floor(compColor.g * 255) : 255;
            int c_blue = compColor.b <= 1.0 ? (int) Math.floor(compColor.b * 255) : 255;

            int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;

            // Set multiple pixels at lower resolution
            for (int ri = 0; ri < res; ri++) {
                for (int rj = 0; rj < res; rj++) {
                    if ((i + ri < image.getHeight()) && (j + rj < image.getWidth())) {
                        image.setRGB(i + ri, j + rj, pixelColor);
                    }
                }
            }
        }
    } 
}
