package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;

import java.awt.event.FocusEvent;
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

	private void run() {
		// Load native library
		loadNativeLibrary();

		boolean pause = false;
		boolean showParticles = true;
		boolean showGrid = true;

		DisplayManager.createDisplay();
		Volume volume = new FixedVolume();
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("haircolorSlow")));

		TexturedModel cubeModel = new TexturedModel(OBJLoader.loadObjModel("cube", loader), new ModelTexture(loader.loadTexture("green")));
		Entity cube = new Entity(cubeModel, new Vector3f(0,0,0), new Vector3f(0,0,0), 4);
		
		Particle particle = new Particle(texturedModel, new Vector3f(0, 20, 0), false);
		
		
		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(0, 0, 50));

		float deltaT = 1.0f / 20.0f;
		int j = 0;
		float fps_avg = 0;
		
		Vector3f force = new Vector3f(0, -9.81f, 0);
		
		while (!Display.isCloseRequested()) {

			// start time
			long startTime = Sys.getTime();

			camera.move();

			if (!pause) {

				// /////////////////////////
				// Start simulation loop //
				// /////////////////////////
				
				particle.setPredictedPosition(new Vector3f(
						particle.getPosition().x + deltaT * particle.getVelocity().x  + deltaT * deltaT * force.x,
						particle.getPosition().y + deltaT * particle.getVelocity().y  + deltaT * deltaT * force.y,
						particle.getPosition().z + deltaT * particle.getVelocity().z  + deltaT * deltaT * force.z));

				particle.setVelocity(VectorMath.Divide(VectorMath.Subtract(particle.getPredictedPosition(), particle.getPosition()), deltaT));

				particle.setPosition(particle.getPredictedPosition());
				
				// ///////////////////////
				// End simulation loop //
				// ///////////////////////
			}

			// Draw head model
			renderer.processEntity(particle);
			renderer.processEntity(cube);

			renderer.render(light, camera);
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
