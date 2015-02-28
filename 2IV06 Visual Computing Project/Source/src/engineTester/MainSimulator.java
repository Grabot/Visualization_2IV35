package engineTester;

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

public class MainSimulator {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Volume volume = new Volume(5);
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

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
		for (int x = -5; x < 5; x++) {
			for (int z = -5; z < 5; z++) {
				// hairs.add(new Hair(texturedModel, new Vector3f(x*2, 0, z*2),
				// 15, 4));
			}
		}
		hairs.add(new Hair(texturedModel, new Vector3f(0, 0, 0), 15, 4));

		for (Hair hair : hairs) {
			RawModel hairModel = hairLoader.loadToVao(hair.getVertices(), hair.getIndices());
			hair.setRawModel(hairModel);
		}

		float deltaT = 1.0f / 20.0f;
		boolean pause = false;

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
			
			if (!pause) {

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

					// Add particle weight to grid
					for(Particle particle : hair.getParticles()) {
						volume.addVoxelWeight(particle.getPredictedPosition(), 1.0f);
					}
					
					Equations.UpdateParticlePositions(hair);
				}

				// ///////////////////////
				// End simulation loop //
				// ///////////////////////
			}
	
			// draw all hair particles
			for (Hair hair : hairs) {
				for (Particle particle : hair.getParticles()) {
					renderer.processEntity(particle);
				}
				hairLoader.updateDataInAttributeList(hair.getRawModel().getPositionsVboID(), 0, 3, hair.getVertices());
				renderer.processEntity(hair);
			}

			// Draw head model
			// renderer.processEntity(head);

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
}
