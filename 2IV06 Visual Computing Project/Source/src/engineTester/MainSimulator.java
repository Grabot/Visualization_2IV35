package engineTester;

import java.util.ArrayList;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import textures.ModelTexture;

public class MainSimulator {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel texturedModel = new TexturedModel(model,
				new ModelTexture(loader.loadTexture("white")));

		// Obj to put hair on
		TexturedModel texturedHairyModel = new TexturedModel(
				OBJLoader.loadObjModel("sphere", loader), new ModelTexture(
						loader.loadTexture("grass")));

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(0, 0, 200));

		float scale = 20;
		Entity head = new Entity(texturedHairyModel, new Vector3f(0, 0, 0),
				new Vector3f(0, 0, 0), scale);

		ArrayList<Hair> hairs = new ArrayList<Hair>();
		for (int i = 0; i < model.getNormals().length; i += 3) {
			Vector3f loc = new Vector3f(model.getNormals()[i],
					model.getNormals()[i + 1], model.getNormals()[i + 2]);

			//if (loc.y > 0)
				//hairs.add(new Hair(texturedModel, VectorMath.Product(loc, scale), 30, 2));
		}

		hairs.add(new Hair(texturedModel, new Vector3f(0, 0, 0), 30, 2));

		float deltaT = 1.0f / 20.0f;

		MasterRenderer renderer = new MasterRenderer();

		while (!Display.isCloseRequested()) {

			// start time
			long startTime = System.nanoTime();

			camera.move();

			// /////////////////////////
			// Start simulation loop //
			// /////////////////////////

			Vector3f externalForce = new Vector3f(0, (float) -9.81f, 0);

			// Calculate gravity on particle
			for (Hair hair : hairs) {

				// Calculate all predicted positions of hair particles
				Equations.CalculatePredictedPositions(hair, externalForce, deltaT);

				// Solve constraints
				Equations.FixedDistanceContraint(hair);

				Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

				Equations.UpdateParticlePositions(hair);
			}

			// ///////////////////////
			// End simulation loop //
			// ///////////////////////

			renderer.render(light, camera);

			// draw all hair particles
			for (Hair hair : hairs) {
				for (Particle particle : hair.getParticles()) {
					renderer.processEntity(particle);
				}
			}

			// Draw head model
			// renderer.processEntity(head);

			// 1rst attribute buffer : vertices
			/*
			for (Hair hair : hairs) {
				float[] positions = new float[hair.getParticles().size() * 3];

				for (int i = 0; i < hair.getParticles().size(); i++) {
					Particle particle = hair.getParticles().get(i);

					positions[i * 3] = particle.getPosition().x;
					positions[i * 3 + 1] = particle.getPosition().x;
					positions[i * 3 + 2] = particle.getPosition().x;
				}
			}
			*/

			// renderer.render(light, camera);
			DisplayManager.updateDisplay();

			// end time
			long endTime = System.nanoTime();
			deltaT = (endTime - startTime) / 360000000f;
		}

		// renderer.Dispose();
		loader.Dispose();
		DisplayManager.closeDisplay();
	}
}
