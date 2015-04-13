package engineTester;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.Util;

import toolbox.UtilCL;

public class OpenCL {

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
	static CLKernel kernel4;
	
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
		kernel4 = CL10.clCreateKernel(program, "ClearGrid", null);

		startindexMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_startindex, null);
		CL10.clEnqueueWriteBuffer(queue, startindexMem, 1, 0, buf_startindex, null, null);
		endindexMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_endindex, null);
		CL10.clEnqueueWriteBuffer(queue, endindexMem, 1, 0, buf_endindex, null, null);

		forceMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_force, null);
		CL10.clEnqueueWriteBuffer(queue, forceMem, 1, 0, buf_force, null, null);
		deltaTMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_deltaT, null);
		CL10.clEnqueueWriteBuffer(queue, deltaTMem, 1, 0, buf_deltaT, null, null);

		posMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_pos, null);
		CL10.clEnqueueWriteBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		predPosMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_pred_pos, null);
		CL10.clEnqueueWriteBuffer(queue, predPosMem, 1, 0, buf_pred_pos, null, null);
		velMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_vel, null);
		CL10.clEnqueueWriteBuffer(queue, velMem, 1, 0, buf_vel, null, null);

		// grid
		spacingMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buf_spacing, null);
		CL10.clEnqueueWriteBuffer(queue, spacingMem, 1, 0, buf_spacing, null, null);
		gridWeightMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_weight, null);
		CL10.clEnqueueWriteBuffer(queue, gridWeightMem, 1, 0, buf_grid_weight, null, null);
		gridVelMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_vel, null);
		CL10.clEnqueueWriteBuffer(queue, gridVelMem, 1, 0, buf_grid_vel, null, null);
		gridGradMem = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, buf_grid_grad, null);
		CL10.clEnqueueWriteBuffer(queue, gridGradMem, 1, 0, buf_grid_grad, null, null);

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
		CL10.clEnqueueWriteBuffer(queue, velMem, 1, 0, buf_vel, null, null);

		CL10.clEnqueueWriteBuffer(queue, gridGradMem, 1, 0, buf_grid_grad, null, null);
		CL10.clEnqueueWriteBuffer(queue, gridVelMem, 1, 0, buf_grid_vel, null, null);
		CL10.clEnqueueWriteBuffer(queue, gridWeightMem, 1, 0, buf_grid_weight, null, null);
		// // sum has to match a kernel method name in the OpenCL source
		CL10.clFinish(queue);

		// Execution our kernel
		PointerBuffer kernel4DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
		kernel4DGlobalWorkSize.put(0, buf_grid_weight.capacity());

		kernel4.setArg(0, gridWeightMem);
		kernel4.setArg(1, gridVelMem);
		kernel4.setArg(2, gridGradMem);

		CL10.clEnqueueNDRangeKernel(queue, kernel4, 1, null, kernel4DGlobalWorkSize, null, null, null);
		
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
		CL10.clEnqueueReadBuffer(queue, gridWeightMem, 1, 0, buf_grid_weight, null, null);
		CL10.clEnqueueReadBuffer(queue, gridVelMem, 1, 0, buf_grid_vel, null, null);
		CL10.clEnqueueReadBuffer(queue, gridGradMem, 1, 0, buf_grid_grad, null, null);

		CL10.clEnqueueReadBuffer(queue, posMem, 1, 0, buf_pos, null, null);
		CL10.clEnqueueReadBuffer(queue, velMem, 1, 0, buf_vel, null, null);
		CL10.clFinish(queue);

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

}
