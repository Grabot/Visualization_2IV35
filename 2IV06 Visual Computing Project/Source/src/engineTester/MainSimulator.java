package engineTester;

import java.util.ArrayList;
import java.util.List;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainSimulator {

	public static void main(String[] args) {

		DisplayManager.createDisplay();

		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);

		RawModel model = OBJLoader.loadObjModel("sphere", loader);

		// Vertices of a square
		float[] vertices = { -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, 0.5f, 0.5f, -0.5f,

				-0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
				0.5f, 0.5f,

				0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
				0.5f, 0.5f,

				-0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,

				-0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,
				0.5f, 0.5f,

				-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, 0.5f };

		float[] textureCoords = {

		0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
				0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1,
				1, 1, 0 };

		int[] indices = { 0, 1, 3, 3, 1, 2, 4, 5, 7, 7, 5, 6, 8, 9, 11, 11, 9,
				10, 12, 13, 15, 15, 13, 14, 16, 17, 19, 19, 17, 18, 20, 21, 23,
				23, 21, 22 };

		// RawModel model = loader.loadToVao(vertices, textureCoords, indices);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader
				.loadTexture("grass")));

		List<Entity> entities = new ArrayList<Entity>();
		for (int x = -5; x < 5; x++) {
			for (int y = -5; y < 5; y++) {
				for (int z = -5; z < 5; z++) {
					entities.add(new Entity(texturedModel, new Vector3f(x*2, y*2, z*2), new Vector3f(0, 0, 0), 0.5f));
				}
			}
		}

		Camera camera = new Camera();

		while (!Display.isCloseRequested()) {
			// entity.increaseRotation(1, 1, 0);

			camera.move();

			// Simulation loop
			renderer.prepare();

			shader.start();
			shader.loadViewMatrix(camera);

			// Render all entities
			for (Entity entity : entities) {
				entity.increaseRotation(1, 1, 0);
				renderer.render(entity, shader);
			}

			shader.stop();

			DisplayManager.updateDisplay();
		}

		shader.Dispose();
		loader.Dispose();
		DisplayManager.closeDisplay();
	}
}
