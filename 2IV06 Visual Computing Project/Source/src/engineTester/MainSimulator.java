package engineTester;

import java.io.File;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import renderEngine.DisplayManager;
import renderEngine.HairLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import toolbox.OperatingSystem;
import toolbox.VectorMath;

public class MainSimulator {

	public static void main(String[] args) {

		// Load native library
		loadNativeLibrary();

		DisplayManager.createDisplay();
		Volume volume = new Volume();
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel texturedModel = new TexturedModel(model,
				new ModelTexture(loader.loadTexture("haircolor")));

		RawModel cellModel = OBJLoader.loadObjModel("cube", loader);
		TexturedModel cellTexturedModel = new TexturedModel(cellModel,
				new ModelTexture(loader.loadTexture("green")));

		// Head obj
		TexturedModel texturedHairyModel = new TexturedModel(
				OBJLoader.loadObjModel("head", loader), new ModelTexture(
						loader.loadTexture("white")));

		// Wig obj
		RawModel wigModel = OBJLoader.loadObjModel("wigd2", loader);

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(0, 20, 200));

		float scale = 1;
		Entity head = new Entity(texturedHairyModel, new Vector3f(0, 0, 0),
				new Vector3f(0, 0, 0), scale);

		ArrayList<Hair> hairs = new ArrayList<Hair>();

		for (Vector3f vec : wigModel.getVertices()) {
			hairs.add(new Hair(texturedModel, vec, 10, 5));
		}

		for (Hair hair : hairs) {
			RawModel hairModel = hairLoader.loadToVao(hair.getVertices(),
					hair.getIndices());
			hair.setRawModel(hairModel);
		}

		System.out.println("Hairs: " + hairs.size());
		System.out.println("Particles: " + hairs.size() * 15);

		float deltaT = 1.0f / 20.0f;
		boolean pause = false;
		boolean showParticles = true;

		while (!Display.isCloseRequested()) {

			// start time
			long startTime = System.nanoTime();

			camera.move();

			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				pause = !pause;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
				showParticles = !showParticles;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
				volume.Clear();
				for (Hair hair : hairs) {

					// Calculate all predicted positions of hair particles
					Equations.CalculatePredictedPositions(hair, externalForce,
							deltaT);

					// Solve constraints
					Equations.FixedDistanceContraint(hair);
					Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

					// Add particle weight to grid
					for (Particle particle : hair.getParticles()) {
						volume.addValues(particle.getPredictedPosition(), 1.0f,
								particle.getVelocity());
					}
				}

				// Apply friction and repulsion
				volume.calculateAverageVelocityAndGradients();
				for (Hair hair : hairs) {

					float friction = 0.02f;
					float repulsion = -0.05f;
					for (Particle particle : hair.getParticles()) {
						Node nodeValue = volume.getNodeValue(particle
								.getPredictedPosition());
						particle.setVelocity(VectorMath.Sum(VectorMath.Product(
								particle.getVelocity(), (1 - friction)),
								VectorMath
										.Product(nodeValue.Velocity, friction)));
						particle.setVelocity(VectorMath.Sum(particle
								.getVelocity(), VectorMath.Divide(VectorMath
								.Product(nodeValue.getGradient(), repulsion),
								deltaT)));
					}

					Equations.UpdateParticlePositions(hair);
				}

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

				hairLoader.updateDataInAttributeList(hair.getRawModel()
						.getPositionsVboID(), 0, 3, hair.getVertices());
				renderer.processEntity(hair);
			}

			// draw all grid cells
			ArrayList<Node> nodes = volume.getGridCells();
			for (Node node : nodes) {
				Entity entity = new Entity(cellTexturedModel, VectorMath.Sum(
						node.getPosition(), (0.5f * volume.getSpacing())),
						new Vector3f(0, 0, 0), volume.getSpacing());

				entity.setWireFrame(true);
				renderer.processEntity(entity);
			}

			// Draw head model
			renderer.processEntity(head);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();

			// end time
			long endTime = System.nanoTime();
			deltaT = (endTime - startTime) / 360000000f;
		}

		// renderer.Dispose();
		loader.Dispose();
		hairLoader.Dispose();
		DisplayManager.closeDisplay();
	}

	public static void loadNativeLibrary() {
		String fileNatives = OperatingSystem.getOSforLWJGLNatives();
		System.setProperty("org.lwjgl.librarypath",
				System.getProperty("user.dir") + File.separator + "lib"
						+ File.separator + "lwjgl-2.9.3" + File.separator
						+ "native" + File.separator + fileNatives);
	}
}
