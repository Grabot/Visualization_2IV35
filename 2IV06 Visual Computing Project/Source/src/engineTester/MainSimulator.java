package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;

import java.io.File;
import java.io.IOException;

import models.RawModel;
import models.TexturedModel;
import objects.Hair;
import objects.HairDescription;
import objects.HairFactory;
import objects.Hairs;
import objects.Particle;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opencl.CL;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Volume.FixedVolume;
import renderEngine.DisplayManager;
import renderEngine.HairLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import toolbox.OperatingSystem;
import toolbox.UtilCL;
import toolbox.VectorMath;

public class MainSimulator {

	private void run() {
		// Load native library
		loadNativeLibrary();

		// Print openCl information
		try {
			CL.create();
			OpenCL.displayInfo();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}

		boolean gpu = true;
		boolean pause = false;
		boolean showParticles = false;
		boolean rendering = true;

		float deltaT = 1.0f / 4.0f;
		float friction = 0.5f;
		float repulsion = -0.4f;
		Vector3f externalForce = new Vector3f();

		DisplayManager.createDisplay();
		FixedVolume volume = new FixedVolume();
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel particleModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("haircolorSlow")));

		TexturedModel texturedHairyModel = new TexturedModel(OBJLoader.loadObjModel("head", loader), new ModelTexture(loader.loadTexture("white")));

		Light light = new Light(new Vector3f(90, 70, 150), new Vector3f(1f, 1f, 1f));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(80, 100, 220));

		float scale = 1;
		Entity head = new Entity(texturedHairyModel, new Vector3f(90, 70, 80), new Vector3f(0, 0, 0), scale);

		// Wig obj
		TexturedModel wigModel = new TexturedModel(OBJLoader.loadObjModel("wigd4", loader), new ModelTexture(loader.loadTexture("haircolor")));
		Entity wig = new Entity(wigModel, VectorMath.Sum(head.getPosition(), new Vector3f(0, 2, 0)), head.getRotation(), scale);

		HairFactory hairFactory = new HairFactory(particleModel, 0.5f);

		for (Vector3f vec : wig.getModel().getRawModel().getVertices()) {
			hairFactory.addHairDescription(new HairDescription(VectorMath.Sum(vec, head.getPosition()), 40));
		}

		final Hairs hairs = hairFactory.Build(hairLoader);

		// Copy array to buffers
		OpenCL.buf_force = UtilCL.toFloatBuffer(new float[] { 0, -9.8f, 0, 0 });
		OpenCL.buf_deltaT = UtilCL.toFloatBuffer(new float[] { deltaT });

		OpenCL.buf_startindex = hairs.getStartIndexesBuffer();
		OpenCL.buf_endindex = hairs.getEndIndexesBuffer();
		OpenCL.buf_pos = hairs.getPositionsBuffer();
		OpenCL.buf_vel = hairs.getVelocityBuffer();
		OpenCL.buf_pred_pos = hairs.getPredictedPositionsBuffer();

		OpenCL.buf_spacing = UtilCL.toFloatBuffer(new float[] { volume.getSpacing() });
		OpenCL.buf_grid_weight = volume.getWeight();
		OpenCL.buf_grid_vel = volume.getVelocity();
		OpenCL.buf_grid_grad = volume.getGradients();

		// Prepare gpu for computations
		try {
			OpenCL.prepareGPU();
		} catch (LWJGLException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Show number of hairs and particles
		System.out.println("Hairs: " + hairs.size());
		System.out.println("Particles: " + hairs.size() * hairs.get(0).getParticles().size());

		int j = 0;
		float fps_avg = 0;

		while (!Display.isCloseRequested()) {

			// start time
			long startTime = Sys.getTime();

			camera.move();

			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				pause = !pause;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				gpu = !gpu;
				if (gpu) {
					System.out.println("GPU Computation Enabled");
				} else {
					System.out.println("GPU Computation Disabled");
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
				rendering = !rendering;
				if (rendering) {
					System.out.println("Rendering Enabled");
				} else {
					System.out.println("Rendering Disabled");
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				showParticles = !showParticles;
			}

			if (!pause) {

				externalForce.x = 0;
				externalForce.y = -9.81f;
				externalForce.z = 0;
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					externalForce.x += -8;
				} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					externalForce.x += 8;
				} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					externalForce.z += 8;
				} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
					externalForce.z += -8;
				} else if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					externalForce.y += 30;
				}

				// /////////////////////////
				// Start simulation loop //
				// /////////////////////////
				if (!gpu) {
					volume.Clear();
				}

				if (gpu) {
					OpenCL.buf_force.put(0, externalForce.x);
					OpenCL.buf_force.put(1, externalForce.y);
					OpenCL.buf_force.put(2, externalForce.z);

					OpenCL.OpenCLTest();
				}

				for (Hair hair : hairs) {
					if (!gpu) {
						// Calculate all predicted positions of hair particles
						Equations.CalculatePredictedPositions(hair, externalForce, deltaT);

						Equations.FixedDistanceContraint(hair);

						Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

						Equations.UpdateParticlePositions(hair);

						// Add particle weight to grid
						for (Particle particle : hair.getParticles()) {
							volume.addValues(particle.getPosition(), 1.0f, particle.getVelocity());
						}
					}

				}

				// Apply friction and repulsion
				if (!gpu) {
					volume.calculateAverageVelocityAndGradients();
				}

				if (!gpu) {
					for (Hair hair : hairs) {
						for (Particle particle : hair.getParticles()) {
							Node nodeValue = volume.getNodeValue(particle.getPosition());
							particle.setVelocity(VectorMath.Sum(VectorMath.Product(particle.getVelocity(), (1 - friction)), VectorMath.Product(nodeValue.Velocity, friction)));
							particle.setVelocity(VectorMath.Sum(particle.getVelocity(), VectorMath.Divide(VectorMath.Product(nodeValue.Gradient, repulsion), deltaT)));
						}
					}
				}

				// ///////////////////////
				// End simulation loop //
				// ///////////////////////
			}

			if (rendering) {
				// draw all hair particles
				for (Hair hair : hairs) {
					if (showParticles) {
						for (Particle particle : hair.getParticles()) {
							renderer.processEntity(particle);
						}
					}

					hairLoader.updateDataInAttributeList(hair.getRawModel().getPositionsVboID(), 0, 3, hair.getVertices());
					hairLoader.updateDataInAttributeList(hair.getRawModel().getNormalsVboID(), 1, 3, hair.getNormals());
					renderer.processEntity(hair);
				}

				// Draw head model
				renderer.processEntity(head);

				renderer.render(light, camera);
			}

				DisplayManager.updateDisplay();

			// end time
			long endTime = Sys.getTime();
			// deltaT = (endTime - startTime) / 300f;
			float fps = 1f / ((endTime - startTime) / 1000f);

			// Average FPS over the last 10 frames
			fps_avg += (fps);
			j++;
			if (j == 10) {
				Display.setTitle("fps: " + fps_avg / 10);
				j = 0;
				fps_avg = 0;
			}

		}

		renderer.Dispose();
		loader.Dispose();
		hairLoader.Dispose();
		DisplayManager.closeDisplay();
	}

	public static void loadNativeLibrary() {
		String fileNatives = OperatingSystem.getOSforLWJGLNatives();
		System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + File.separator + "lib" + File.separator + "lwjgl-2.9.3" + File.separator + "native" + File.separator + fileNatives);
	}

	public static void main(String[] args) {

		MainSimulator main = new MainSimulator();
		main.run();
	}
}
