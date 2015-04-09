package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GuiRenderer;

import java.io.File;
import java.util.ArrayList;

import models.RawModel;
import models.TexturedModel;
import objects.Hair;
import objects.HairDescription;
import objects.HairFactory;
import objects.Hairs;
import objects.Particle;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
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
	private Vector3f externalForce;

	private void run() {
		// Load native library
		loadNativeLibrary();

		boolean pause = false;

		boolean showParticles = true;
		boolean rendering = true;

		float friction = 0.5f;
		float repulsion = -0.2f;
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
		TexturedModel wigModel = new TexturedModel(OBJLoader.loadObjModel("wigd2", loader), new ModelTexture(loader.loadTexture("haircolor")));
		Entity wig = new Entity(wigModel, head.getPosition(), VectorMath.Sum(head.getRotation(), new Vector3f(0, 2, 0)), scale);

		HairFactory hairFactory = new HairFactory(particleModel, 1.0f);

		for (Vector3f vec : wig.getModel().getRawModel().getVertices()) {
			hairFactory.addHairDescription(new HairDescription(VectorMath.Sum(vec, head.getPosition()), 30));
		}

		final Hairs hairs = hairFactory.Build();

		System.out.println("Hairs: " + hairs.size());
		System.out.println("Particles: " + hairs.size() * hairs.get(0).getParticles().size());

		float deltaT = 1.0f / 4.0f;
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

			if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
				rendering = !rendering;
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

				// Calculate gravity on particle
				volume.Clear();

				for (Hair hair : hairs) {

					// Calculate all predicted positions of hair particles
					Equations.CalculatePredictedPositions(hair, externalForce, deltaT);

						Equations.FixedDistanceContraint(hair);

						Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

					// Add particle weight to grid
					for (Particle particle : hair.getParticles()) {
						volume.addValues(particle.getPredictedPosition(), 1.0f, particle.getVelocity());
					}

					Equations.UpdateParticlePositions(hair);
				}

				// Apply friction and repulsion
				volume.calculateAverageVelocityAndGradients();

				for (Hair hair : hairs) {
					for (Particle particle : hair.getParticles()) {
						Node nodeValue = volume.getNodeValue(particle.getPredictedPosition());
						particle.setVelocity(VectorMath.Sum(VectorMath.Product(particle.getVelocity(), (1 - friction)), VectorMath.Product(nodeValue.Velocity, friction)));
						particle.setVelocity(VectorMath.Sum(particle.getVelocity(), VectorMath.Divide(VectorMath.Product(nodeValue.Gradient, repulsion), deltaT)));
					}
				}

				// ///////////////////////
				// End simulation loop //
				// ///////////////////////
			}

			// draw all hair particles
			if (rendering) {
				for (Hair hair : hairs) {
					if (showParticles) {
						for (Particle particle : hair.getParticles()) {
							renderer.processEntity(particle);
						}
					}

					//hairLoader.updateDataInAttributeList(hair.getRawModel().getPositionsVboID(), 0, 3, hair.getVertices());
					//hairLoader.updateDataInAttributeList(hair.getRawModel().getNormalsVboID(), 1, 3, hair.getNormals());
					//renderer.processEntity(hair);
				}

			}

			// Draw head model
			renderer.processEntity(head);
			renderer.processEntity(wig);

			renderer.render(light, camera);

			DisplayManager.updateDisplay();

			// end time
			long endTime = Sys.getTime();

			// deltaT = (endTime - startTime) / 300f;
			float fps = 1f / ((endTime - startTime) / 1000f);

			// Average FPS over the last 10 frames
			fps_avg += (fps);
			j++;
			if (j == 10) {
				//guiRenderer.setFPS(fps_avg / 10);
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
