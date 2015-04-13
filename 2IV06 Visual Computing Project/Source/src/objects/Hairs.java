package objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Hairs extends ArrayList<Hair> {

	private IntBuffer buf_startindexes;
	private IntBuffer buf_endindexes;

	private FloatBuffer buf_positions;
	private FloatBuffer buf_velocities;
	private FloatBuffer buf_preditedpositions;

	public Hairs(IntBuffer startindexes, IntBuffer endindexes, FloatBuffer positions, FloatBuffer velocities, FloatBuffer predictedpositions) {
		this.buf_startindexes = startindexes;
		this.buf_endindexes = endindexes;
		this.buf_positions = positions;
		this.buf_velocities = velocities;
		this.buf_preditedpositions = predictedpositions;
	}

	public IntBuffer getStartIndexesBuffer() {
		return buf_startindexes;
	}

	public IntBuffer getEndIndexesBuffer() {
		return buf_endindexes;
	}

	public FloatBuffer getPositionsBuffer() {
		return buf_positions;
	}

	public FloatBuffer getVelocityBuffer() {
		return buf_velocities;
	}

	public FloatBuffer getPredictedPositionsBuffer() {
		return buf_preditedpositions;
	}
}