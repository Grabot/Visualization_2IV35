package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import Volume.FixedVolume;
import Volume.Volume;
import renderEngine.DisplayManager;
import renderEngine.HairLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import toolbox.OperatingSystem;
import toolbox.VectorMath;

public class MainSimulator {

	private ArrayList<Hair> hairs = new ArrayList<Hair>();
	private void run() {
		// Load native library
		loadNativeLibrary();

		boolean pause = false;
		boolean showParticles = true;
		boolean showGrid = true;
		boolean showGridCollision = true;

		float friction = 0.5f;
		float repulsion = -0.05f;

		DisplayManager.createDisplay();
		//Volume volume = new FixedVolume();
		//Volume volume_collision = new FixedVolume(1);
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

		// Guis
		List<GuiTexture> guis = new ArrayList<GuiTexture>();

		GuiTexture Button1Off = new GuiTexture(loader.loadTexture("buttonNotPressed"), new Vector2f(-0.8f, 0.9f), new Vector2f(0.2f, 0.1f));
		guis.add(Button1Off);
		GuiTexture Button2Off = new GuiTexture(loader.loadTexture("buttonNotPressed"), new Vector2f(-0.8f, 0.7f), new Vector2f(0.2f, 0.1f));
		guis.add(Button2Off);
		GuiTexture Button3Off = new GuiTexture(loader.loadTexture("buttonNotPressed"), new Vector2f(-0.8f, 0.5f), new Vector2f(0.2f, 0.1f));
		guis.add(Button3Off);
		GuiTexture Button4Off = new GuiTexture(loader.loadTexture("buttonNotPressed"), new Vector2f(-0.8f, 0.3f), new Vector2f(0.2f, 0.1f));
		guis.add(Button4Off);
		
		GuiTexture Button1On = new GuiTexture(loader.loadTexture("buttonPressed"), new Vector2f(-0.8f, 0.9f), new Vector2f(0.2f, 0.1f));
		guis.add(Button1On);
		GuiTexture Button2On = new GuiTexture(loader.loadTexture("buttonPressed"), new Vector2f(-0.8f, 0.7f), new Vector2f(0.2f, 0.1f));
		guis.add(Button2On);
		GuiTexture Button3On = new GuiTexture(loader.loadTexture("buttonPressed"), new Vector2f(-0.8f, 0.5f), new Vector2f(0.2f, 0.1f));
		guis.add(Button3On);
		GuiTexture Button4On = new GuiTexture(loader.loadTexture("buttonPressed"), new Vector2f(-0.8f, 0.3f), new Vector2f(0.2f, 0.1f));
		guis.add(Button4On);

		GuiTexture gui = new GuiTexture(loader.loadTexture("windowTexture"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		
		GuiTexture hairText = new GuiTexture(loader.loadTexture("HairWhite2"), new Vector2f(-0.889f, 0.9f), new Vector2f(0.05f, 0.02f));
		guis.add(hairText);
		GuiTexture particlesText = new GuiTexture(loader.loadTexture("particlesWhite"), new Vector2f(-0.86f, 0.8f), new Vector2f(0.08f, 0.02f));
		guis.add(particlesText);
		GuiTexture fpsText = new GuiTexture(loader.loadTexture("fpsWhite"), new Vector2f(-0.91f, 0.7f), new Vector2f(0.03f, 0.02f));
		guis.add(fpsText);
		GuiTexture fpsdot = new GuiTexture(loader.loadTexture("dot"), new Vector2f(-0.815f, 0.68f), new Vector2f(0.004f, 0.005f));
		guis.add(fpsdot);

		
		for( int i = 0; i < 5; i++ )
		{
			for( int j = 0; j < 10; j++ )
			{
				GuiTexture HairNumbers = new GuiTexture(loader.loadTexture("white" + j), new Vector2f(-(0.81f - (0.02f * i)), 0.9f), new Vector2f(0.01f, 0.02f));
				guis.add(HairNumbers);
			}
		}
		
		for( int i = 0; i < 6; i++ )
		{
			for( int j = 0; j < 10; j++ )
			{
				GuiTexture ParticleNumbers = new GuiTexture(loader.loadTexture("white" + j), new Vector2f(-(0.75f - (0.02f * i)), 0.8f), new Vector2f(0.01f, 0.02f));
				guis.add(ParticleNumbers);
			}
		}

		for( int i = 0; i < 10; i++ )
		{
			GuiTexture fpsNumber1 = new GuiTexture(loader.loadTexture("white" + i), new Vector2f(-0.85f, 0.7f), new Vector2f(0.01f, 0.02f));
			guis.add(fpsNumber1);
		}
		for( int i = 0; i < 10; i++ )
		{
			GuiTexture fpsNumber2 = new GuiTexture(loader.loadTexture("white" + i), new Vector2f(-(0.85f - 0.02f), 0.7f), new Vector2f(0.01f, 0.02f));
			guis.add(fpsNumber2);
		}
		for( int i = 0; i < 10; i++ )
		{
			GuiTexture fpsNumber3 = new GuiTexture(loader.loadTexture("white" + i), new Vector2f(-(0.85f - 0.05f), 0.7f), new Vector2f(0.01f, 0.02f));
			guis.add(fpsNumber3);
		}
		
		
		

		
		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("haircolorSlow")));

		RawModel cellModel = OBJLoader.loadObjModel("cube", loader);
		TexturedModel cellTexturedModel = new TexturedModel(cellModel, new ModelTexture(loader.loadTexture("green")));
		TexturedModel cellTexturedModel_red = new TexturedModel(cellModel, new ModelTexture(loader.loadTexture("red")));

		// Head obj
		TexturedModel texturedHairyModel = new TexturedModel(OBJLoader.loadObjModel("head", loader), new ModelTexture(loader.loadTexture("white")));

		// Wig obj
		RawModel wigModel = OBJLoader.loadObjModel("wigd2", loader);

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(80, 100, 220));

		float scale = 1;
		Entity head = new Entity(texturedHairyModel, new Vector3f(90, 70, 80), new Vector3f(0, 0, 0), scale);

		// Set grid cell to inside when it contains head
/*
		for ( Vector3f vec : head.getModel().getRawModel().getVertices()) {
			vec = VectorMath.Sum(vec, head.getPosition());
			volume_collision.getNode(vec).inside = true;
		}
		// Set grid cell to inside when it contains head
		for ( int z = 0; z < volume_collision.getGridSize(); z++ ) {
			for ( int y = 0; y < volume_collision.getGridSize(); y++ ) {
				for ( int i = 0; i < volume_collision.getGridSize(); i++) {
					if (volume_collision.getNode(new Vector3f(i * volume_collision.getSpacing(), y* volume_collision.getSpacing(), z* volume_collision.getSpacing())).inside) {
						for ( int j = volume_collision.getGridSize()-1; j > i; j--) {
							if(volume_collision.getNode(new Vector3f(j * volume_collision.getSpacing(), y* volume_collision.getSpacing(),z* volume_collision.getSpacing())).inside) {
								for ( int k = i; k < j; k++) {
									volume_collision.getNode(new Vector3f(k * volume_collision.getSpacing(), y* volume_collision.getSpacing(), z* volume_collision.getSpacing())).inside = true;
								}
							}
						}
					}
				}
			}
		}
		*/
		
		for (Vector3f vec : wigModel.getVertices()) {
			hairs.add(new Hair(texturedModel, VectorMath.Sum(vec, head.getPosition()), 5, 5));
		}

		for (Hair hair : hairs) {
			RawModel hairModel = hairLoader.loadToVao(hair.getVertices(), hair.getIndices());
			hair.setRawModel(hairModel);
		}

		GuiRenderer guiRenderer = new GuiRenderer(loader, hairs);

		System.out.println("Hairs: " + hairs.size());
		System.out.println("Particles: " + hairs.size() * hairs.get(0).getParticles().size());

		float deltaT = 1.0f / 20.0f;
		int j = 0;
		float fps_avg = 0;
		
		while (!Display.isCloseRequested()) {

			// start time
			long startTime = Sys.getTime();

			camera.move();

			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				pause = !pause;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				showParticles = !showParticles;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
				showGrid = !showGrid;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
				showGridCollision = !showGridCollision;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (!pause) {

				// /////////////////////////
				// Start simulation loop //
				// /////////////////////////

				Vector3f externalForce;
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					externalForce = new Vector3f(-8, (float) -9.81f, 0);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
					externalForce = new Vector3f(8, (float) -9.81f, 0);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					externalForce = new Vector3f(0, (float) -9.81f, 8);
				} else {
					externalForce = new Vector3f(0, (float) -9.81f, 0);
				}

				// Calculate gravity on particle
				//volume.Clear();

				for (Hair hair : hairs) {

					// Calculate all predicted positions of hair particles
					Equations.CalculatePredictedPositions(hair, externalForce, deltaT);
					
					boolean satisfied = false;
					
					while (!satisfied) {
						satisfied = true;
						// Solve constraints
						Equations.FixedDistanceContraint(hair);
						// collision 
						/*
						for ( Particle particle : hair.getParticles() ) {
							if (volume_collision.getNode(particle.getPredictedPosition()).inside && !particle.isRoot()) {
								particle.setPredictedPosition( particle.getPosition() );
								satisfied = true;
							}
						}
						*/
						
					}
					Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

					// Add particle weight to grid
					/*
					for (Particle particle : hair.getParticles()) {
						volume.addValues(particle.getPredictedPosition(), 1.0f, particle.getVelocity());
					}
					*/
					Equations.UpdateParticlePositions(hair);
				}

				// Apply friction and repulsion
				//volume.calculateAverageVelocityAndGradients();

				/*
				for (Hair hair : hairs) {
					for (Particle particle : hair.getParticles()) {
						Node nodeValue = volume.getNodeValue(particle.getPredictedPosition());
						particle.setVelocity(VectorMath.Sum(VectorMath.Product(particle.getVelocity(), (1 - friction)), VectorMath.Product(nodeValue.Velocity, friction)));
						particle.setVelocity(VectorMath.Sum(particle.getVelocity(), VectorMath.Divide(VectorMath.Product(nodeValue.getGradient(), repulsion), deltaT)));
					}
				}
				*/

				// ///////////////////////
				// End simulation loop //
				// ///////////////////////
			}

			// draw all hair particles
			for (Hair hair : hairs) {
				if (showParticles) {
					for (Particle particle : hair.getParticles()) {
						renderer.processEntity(particle);
					}
				}

				hairLoader.updateDataInAttributeList(hair.getRawModel().getPositionsVboID(), 0, 3, hair.getVertices());
				renderer.processEntity(hair);
			}

			// draw all grid cells
			/*
			if (!showGrid) {
				List<Node> nodes = volume.getGridCells();
				for (Node node : nodes) {
					if (node.Weight > 0) {	
						Entity entity;
						entity = new Entity(cellTexturedModel, VectorMath.Sum(node.getPosition(), (0.5f * volume.getSpacing())), new Vector3f(0, 0, 0), volume.getSpacing());
						entity.setWireFrame(true);
						renderer.processEntity(entity);	
					}
				}
			}
			*/

			/*
			// draw all collision cells
			if (!showGridCollision) {
				List<Node> nodes = volume_collision.getGridCells();
				for (Node node : nodes) {
					Entity entity;
					if ( node.inside) {
						entity = new Entity(cellTexturedModel_red, VectorMath.Sum(node.getPosition(), (0.5f * volume_collision.getSpacing())), new Vector3f(0, 0, 0), volume_collision.getSpacing());
						entity.setWireFrame(true);
						renderer.processEntity(entity);
					} 	
				}
			}
			*/

			// Draw head model
			renderer.processEntity(head);

			renderer.render(light, camera);
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();

			// end time
			long endTime = Sys.getTime();

			deltaT = (endTime - startTime) / 300f;
			// System.out.println(1/deltaT);
			float fps = 1f / ((endTime - startTime) / 1000f);
			// System.out.println("time: " + fps);

			// Average FPS over the last 10 frames
			fps_avg += (fps);
			j++;
			if (j == 10) {
				// System.out.println("AVG FPS ----> " + fps_avg/10);
				Display.setTitle("pfs: " + fps_avg / 10);
				guiRenderer.setFPS(fps_avg/10);
				j = 0;
				fps_avg = 0;
			}

		}

		guiRenderer.Dispose();
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
