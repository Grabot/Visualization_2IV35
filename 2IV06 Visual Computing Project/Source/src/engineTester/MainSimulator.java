package engineTester;

import entities.Camera;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.OperatingSystem;

public class MainSimulator {


	
	private void run() {
		// Load native library
		loadNativeLibrary();

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();

		// Guis
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(80, 100, 300));

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));
		
		while (!Display.isCloseRequested()) {
			
			renderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();

		}

		guiRenderer.Dispose();
		renderer.Dispose();
		loader.Dispose();
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
