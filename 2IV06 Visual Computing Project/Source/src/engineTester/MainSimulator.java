package engineTester;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;

import models.RawModel;
import models.TexturedModel;
import objects.Hair;
import objects.HairDescription;
import objects.HairFactory;
import objects.Hairs;
import objects.Particle;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.Util;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Volume.FixedVolume;
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
import toolbox.UtilCL;
import toolbox.VectorMath;

public class MainSimulator {

	public static void displayInfo() {

		for (int platformIndex = 0; platformIndex < 1; platformIndex++) {
			CLPlatform platform = CLPlatform.getPlatforms().get(platformIndex);
			System.out.println("Platform #" + platformIndex + ":" + platform.getInfoString(CL10.CL_PLATFORM_NAME));
			List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
			for (int deviceIndex = 0; deviceIndex < devices.size(); deviceIndex++) {
				CLDevice device = devices.get(deviceIndex);
				System.out.printf(Locale.ENGLISH, "Device #%d(%s):%s\n", deviceIndex, UtilCL.getDeviceType(device.getInfoInt(CL10.CL_DEVICE_TYPE)), device.getInfoString(CL10.CL_DEVICE_NAME));
				System.out.printf(Locale.ENGLISH, "\tCompute Units: %d @ %d mghtz\n", device.getInfoInt(CL10.CL_DEVICE_MAX_COMPUTE_UNITS), device.getInfoInt(CL10.CL_DEVICE_MAX_CLOCK_FREQUENCY));
				System.out.printf(Locale.ENGLISH, "\tLocal memory: %s\n", UtilCL.formatMemory(device.getInfoLong(CL10.CL_DEVICE_LOCAL_MEM_SIZE)));
				System.out.printf(Locale.ENGLISH, "\tGlobal memory: %s\n", UtilCL.formatMemory(device.getInfoLong(CL10.CL_DEVICE_GLOBAL_MEM_SIZE)));
				System.out.println();
			}
		}
	}

	// Data buffers to store the input and result data in
	static IntBuffer buf_size;
	static IntBuffer buf_startindex;
	static IntBuffer buf_endindex;

	static FloatBuffer buf_force;
	static FloatBuffer buf_deltaT;

	static FloatBuffer buf_pos;
	static FloatBuffer buf_vel;
	static FloatBuffer buf_pred_pos;

	static FloatBuffer buf_spacing;
	static FloatBuffer buf_grid_weight;
	static FloatBuffer buf_grid_vel;
	static FloatBuffer buf_grid_grad;

	static CLContext context;
	static CLCommandQueue queue;
	static CLProgram program;
	static CLKernel kernel;
	static CLKernel kernel2;
	static CLKernel kernel3;

	static CLMem startindexMem;
	static CLMem endindexMem;
	static CLMem predPosMem;
	static CLMem forceMem;
	static CLMem deltaTMem;
	static CLMem posMem;
	static CLMem velMem;

	static CLMem spacingMem;
	static CLMem gridWeightMem;
	static CLMem gridVelMem;
	static CLMem gridGradMem;

	public static void prepareGPU() throws LWJGLException, IOException {

		CLPlatform platform = CLPlatform.getPlatforms().get(0);
		List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
		context = CLContext.create(platform, devices, null, null, null);
		queue = CL10.clCreateCommandQueue(context, devices.get(0), CL10.CL_QUEUE_PROFILING_ENABLE, null);

		// Load the source from a resource file
		String source = UtilCL.getResourceAsString("cl/Calculations.c");

		// Create our program and kernel
		program = CL10.clCreateProgramWithSource(context, source, null);
		Util.checkCLError(CL10.clBuildProgram(program, devices.get(0), "", null));

		kernel = CL10.clCreateKernel(program, "HairCalculations", null);
		kernel2 = CL10.clCreateKernel(program, "GridCalculations", null);
		kernel3 = CL10.clCreateKernel(program, "ParticleCalculations", null);

		startindexMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_startindex, null);
		CL10.clEnqueueWriteBuffer(queue, startindexMem, 1, 0, buf_startindex, null, null);
		endindexMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_endindex, null);
		CL10.clEnqueueWriteBuffer(queue, endindexMem, 1, 0, buf_endindex, null, null);
		predPosMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_pred_pos, null);

		forceMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_force, null);
		CL10.clEnqueueWriteBuffer(queue, forceMem, 1, 0, buf_force, null, null);
		deltaTMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_deltaT, null);
		CL10.clEnqueueWriteBuffer(queue, deltaTMem, 1, 0, buf_deltaT, null, null);

		posMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_pos, null);
		CL10.clEnqueueWriteBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		velMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_vel, null);
		CL10.clEnqueueWriteBuffer(queue, velMem, 1, 0, buf_vel, null, null);

		// grid
		//spacingMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_spacing, null);
		//CL10.clEnqueueWriteBuffer(queue, spacingMem, 1, 0, buf_spacing, null, null);
		//gridWeightMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_weight, null);
		//CL10.clEnqueueWriteBuffer(queue, gridWeightMem, 1, 0, buf_grid_weight, null, null);
		//gridVelMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_vel, null);
		//CL10.clEnqueueWriteBuffer(queue, gridVelMem, 1, 0, buf_grid_vel, null, null);
		//gridGradMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_grad, null);
		//CL10.clEnqueueWriteBuffer(queue, gridGradMem, 1, 0, buf_grid_grad, null, null);

		CL10.clFinish(queue);
	}

	public static void OpenCLTest() {

		// CL10.clEnqueueWriteBuffer(queue, predPosMem, 1, 0, buf_pred_pos,
		// null, null);

		// forceMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY |
		// CL10.CL_MEM_COPY_HOST_PTR, buf_force, null);
		CL10.clEnqueueWriteBuffer(queue, forceMem, 1, 0, buf_force, null, null);
		// deltaTMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY |
		// CL10.CL_MEM_COPY_HOST_PTR, buf_deltaT, null);
		CL10.clEnqueueWriteBuffer(queue, deltaTMem, 1, 0, buf_deltaT, null, null);
		// posMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE |
		// CL10.CL_MEM_COPY_HOST_PTR, buf_pos, null);
		CL10.clEnqueueWriteBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		// velMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE |
		// CL10.CL_MEM_COPY_HOST_PTR, buf_vel, null);
		// CL10.clEnqueueWriteBuffer(queue, velMem, 1, 0, buf_vel, null, null);
		// // sum has to match a kernel method name in the OpenCL source
		CL10.clFinish(queue);

		// Execution our kernel
		PointerBuffer kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
		kernel1DGlobalWorkSize.put(0, buf_startindex.capacity());

		kernel.setArg(0, startindexMem);
		kernel.setArg(1, endindexMem);

		kernel.setArg(2, forceMem);
		kernel.setArg(3, deltaTMem);

		kernel.setArg(4, posMem);
		kernel.setArg(5, velMem);
		kernel.setArg(6, predPosMem);

		kernel.setArg(7, spacingMem);
		kernel.setArg(8, gridWeightMem);
		kernel.setArg(9, gridVelMem);
		CL10.clEnqueueNDRangeKernel(queue, kernel, 1, null, kernel1DGlobalWorkSize, null, null, null);
		
		// Execution our kernel
		PointerBuffer kernel2DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
		kernel2DGlobalWorkSize.put(0, buf_grid_weight.capacity());

		kernel2.setArg(0, spacingMem);
		kernel2.setArg(1, gridWeightMem);
		kernel2.setArg(2, gridVelMem);
		kernel2.setArg(3, gridGradMem);

		CL10.clEnqueueNDRangeKernel(queue, kernel2, 1, null, kernel2DGlobalWorkSize, null, null, null);

		// Execution our kernel
		
		PointerBuffer kernel3DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
		kernel3DGlobalWorkSize.put(0, buf_pos.capacity() / 4);
		
		kernel3.setArg(0, spacingMem);
		kernel3.setArg(1, deltaTMem);
		
		kernel3.setArg(2, posMem);
		kernel3.setArg(3, velMem);
		
		kernel3.setArg(4, gridWeightMem);
		kernel3.setArg(5, gridVelMem);
		kernel3.setArg(6, gridGradMem);
		
		CL10.clEnqueueNDRangeKernel(queue, kernel3, 1, null, kernel3DGlobalWorkSize, null, null, null);
		
		// Read the results memory back into our result buffer
		CL10.clEnqueueReadBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		CL10.clEnqueueReadBuffer(queue, predPosMem, 1, 0, buf_pred_pos, null, null);
		CL10.clEnqueueReadBuffer(queue, velMem, 1, 0, buf_vel, null, null);

		//CL10.clEnqueueReadBuffer(queue, gridWeightMem, 1, 0, buf_grid_weight, null, null);
		//CL10.clEnqueueReadBuffer(queue, gridVelMem, 1, 0, buf_grid_vel, null, null);
		//CL10.clEnqueueReadBuffer(queue, gridGradMem, 1, 0, buf_grid_grad, null, null);
		CL10.clFinish(queue);

		/*
		 * kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
		 * kernel1DGlobalWorkSize.put(0, buf_pos.capacity());
		 * 
		 * kernel.setArg(1, posMem); kernel.setArg(2, velMem); kernel.setArg(3,
		 * predPosMem); CL10.clEnqueueNDRangeKernel(queue, kernel2, 1, null,
		 * kernel1DGlobalWorkSize, null, null, null);
		 * 
		 * // Read the results memory back into our result buffer
		 * 
		 * CL10.clEnqueueReadBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		 * CL10.clEnqueueReadBuffer(queue, velMem, 1, 0, buf_vel, null, null);
		 * CL10.clFinish(queue);
		 */

		// CL10.clReleaseMemObject(posMem);
		// Print the result memory
		// UtilCL.print(buf_grid_weight);
		// UtilCL.print(buf_pos);

		// buf_pos = buf_pred_pos;

		// Clean up OpenCL resources CL10.clReleaseKernel(kernel);
		// CL10.clReleaseProgram(program); CL10.clReleaseMemObject(posMem);
		// CL10.clReleaseMemObject(velMem); CL10.clReleaseMemObject(forceMem);
		// /CL10.clReleaseMemObject(deltaTMem);
		// CL10.clReleaseMemObject(newPosMem);

		// CL.destroy();

	}

	public static void main(String[] args) {

		// Load native library
		loadNativeLibrary();

		// Print openCl information
		try {
			CL.create();
			displayInfo();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}

		boolean gpu = true;
		boolean pause = false;
		boolean showParticles = true;
		boolean showGrid = true;

		
		DisplayManager.createDisplay();
		FixedVolume volume = new FixedVolume();
		Loader loader = new Loader();
		HairLoader hairLoader = new HairLoader();
		MasterRenderer renderer = new MasterRenderer();

		RawModel model = OBJLoader.loadObjModel("sphere", loader);
		TexturedModel particleModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("haircolor")));

		// Head obj
		TexturedModel texturedHairyModel = new TexturedModel(OBJLoader.loadObjModel("head", loader), new ModelTexture(loader.loadTexture("white")));

		// Wig obj
		RawModel wigModel = OBJLoader.loadObjModel("wigd4", loader);

		Light light = new Light(new Vector3f(0, 0, 20), new Vector3f(1, 1, 1));

		Camera camera = new Camera();
		camera.setPosition(new Vector3f(80, 100, 220));

		float scale = 1;
		Entity head = new Entity(texturedHairyModel, new Vector3f(90, 70, 80), new Vector3f(0, 0, 0), scale);

		HairFactory hairFactory = new HairFactory(particleModel, 1.0f);

		for (Vector3f vec : wigModel.getVertices()) {
			hairFactory.addHairDescription(new HairDescription(VectorMath.Sum(vec, head.getPosition()), 30));
		}

		final Hairs hairs = hairFactory.Build();

		buf_force = UtilCL.toFloatBuffer(new float[] { 0, -9.8f, 0, 0 });
		buf_deltaT = UtilCL.toFloatBuffer(new float[] { 0.25f });

		buf_startindex = hairs.getStartIndexesBuffer();
		buf_endindex = hairs.getEndIndexesBuffer();
		buf_pos = hairs.getPositionsBuffer();
		buf_vel = hairs.getVelocityBuffer();
		buf_pred_pos = hairs.getPredictedPositionsBuffer();

		buf_spacing = UtilCL.toFloatBuffer(new float[] { volume.getSpacing() });
		buf_grid_weight = UtilCL.toFloatBuffer(volume.getWeight());
		buf_grid_vel = UtilCL.toFloatBuffer(volume.getVelocity());
		buf_grid_grad = UtilCL.toFloatBuffer(volume.getGradients());

		System.out.println("Hairs: " + hairs.size());
		System.out.println("Particles: " + hairs.size() * hairs.get(0).getParticles().size());

		float deltaT = 1.0f / 20.0f;
		float friction = 0.5f;
		float repulsion = -0.2f;

		try {
			prepareGPU();
		} catch (LWJGLException | IOException e1) {
			e1.printStackTrace();
		}

		int j = 0;
		float fps_avg = 0;
		float time_before = System.nanoTime();

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

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				gpu = !gpu;
				if (gpu) {
					System.out.println("GPU Computation Enabled");
				} else {
					System.out.println("GPU Computation Disabled");
				}
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

			if (!pause) {

				Vector3f externalForce;
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					externalForce = new Vector3f(-80, (float) -9.81f, 0);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
					externalForce = new Vector3f(80, (float) -9.81f, 0);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					externalForce = new Vector3f(0, (float) -9.81f, 8);
				} else {
					externalForce = new Vector3f(0, (float) -9.81f, 0);
				}

				// /////////////////////////
				// Start simulation loop //
				// /////////////////////////

				// Calculate gravity on particle
				volume.Clear();

				if (gpu) {
					buf_force.put(0, externalForce.x);
					buf_force.put(1, externalForce.y);
					buf_force.put(2, externalForce.z);
					OpenCLTest();
				}

				if (!gpu) {
					for (Hair hair : hairs) {

						// Calculate all predicted positions of hair particles
						Equations.CalculatePredictedPositions(hair, externalForce, deltaT);

						// Solve constraints
						Equations.FixedDistanceContraint(hair, hair.getParticleDistance());
						Equations.CalculateParticleVelocities(hair, deltaT, 0.9f);

						Equations.UpdateParticlePositions(hair);
					}

					// Add particle weight to grid
					for (Hair hair : hairs) {
						for (Particle particle : hair.getParticles()) {
							volume.addValues(particle.getPosition(), 1.0f, particle.getVelocity());
						}
					}

					// Apply friction and repulsion
					volume.calculateAverageVelocityAndGradients();

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

			// draw all hair particles
			int i = 0;
			if (showParticles) {
				for (Hair hair : hairs) {
					if ((i % 5) == 0) {

						for (Particle particle : hair.getParticles()) {
							renderer.processEntity(particle);
							i++;
						}
					}
					i++;
				}
			}

			// hairLoader.updateDataInAttributeList(hair.getRawModel().getPositionsVboID(),
			// 0, 3, hair.getVertices());
			// renderer.processEntity(hair);
			// }

			// Draw head model
			renderer.processEntity(head);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();

			// end time
			long endTime = Sys.getTime();

			deltaT = (endTime - startTime) / 300f;
			buf_deltaT.put(0, deltaT);
			float fps = 1f / ((endTime - startTime) / 1000f);

			// Average FPS over the last 10 frames
			fps_avg += (fps);
			j++;
			if (j == 10) {
				// System.out.println("AVG FPS ----> " + fps_avg/10);
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

}
