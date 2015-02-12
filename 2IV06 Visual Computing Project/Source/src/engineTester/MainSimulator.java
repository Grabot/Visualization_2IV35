package engineTester;

import java.util.ArrayList;
import java.util.List;

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
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainSimulator {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader
				.loadTexture("grass")));

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(0, -20, 100));

		ArrayList<Hair> hairs = new ArrayList<Hair>();
		hairs.add(new Hair(texturedModel, new Vector3f(0, 0, 0), 20, 2));
		float deltaT = 0.005f;

		MasterRenderer renderer = new MasterRenderer();
		while (!Display.isCloseRequested()) {

			camera.move();

			// /////////////////////////
			// Start simulation loop //
			// /////////////////////////

			Vector3f externalForce = new Vector3f(0, (float) -9.81, 0);

			// Calculate gravity on particle
			for (Hair hair : hairs) {
				for (int i = 0; i < hair.getParticles().size(); i++) {
					Particle particle = hair.getParticles().get(i);

					particle.setPredictedPosition(Equations.Equation1(particle.getPosition(), particle
							.getVelocity(), externalForce, deltaT));

					// TODO: Solve constraints
					if(i > 0){
					Equations.SatisfyConstraints(hair.getParticles().get(i - 1), hair.getParticles().get(i));
					}

					particle.setVelocity(Equations
							.Equation3(particle.getPredictedPosition(), particle
									.getPosition(), deltaT));

					if (!particle.isRoot()) {
						particle.setPosition(particle.getPredictedPosition());
					}

				}
			}

			// ///////////////////////
			// End simulation loop //
			// ///////////////////////

			// draw all hair particles
			for (Hair hair : hairs) {
				for (Particle particle : hair.getParticles()) {
					renderer.processEntity(particle);
				}
			}

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.Dispose();
		loader.Dispose();
		DisplayManager.closeDisplay();
	}
}
