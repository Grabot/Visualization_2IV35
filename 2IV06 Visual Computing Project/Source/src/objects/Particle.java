package objects;

import java.nio.FloatBuffer;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Particle extends Entity implements IParticle {

	private int index;
	private FloatBuffer buf_positions;
	private FloatBuffer buf_velocities;
	private Vector3f position = new Vector3f(0,0,0);
	private Vector3f predictedPosition = new Vector3f(0,0,0);
	private Vector3f FTLCorrectionVector = new Vector3f(0,0,0);

	public Particle(TexturedModel model, int index, FloatBuffer positions, FloatBuffer velocities) {
		super(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0.25f);
		this.index = index;
		this.buf_positions = positions;
		this.position = getPosition();
		this.buf_velocities = velocities;
	}

	@Override
	public Vector3f getPosition() {
		float x = buf_positions.get(index * 4);
		float y = buf_positions.get(index * 4 + 1);
		float z = buf_positions.get(index * 4 + 2);

		return new Vector3f(x, y, z);
	}

	@Override
	public void setPosition(Vector3f position) {
		buf_positions.put(index * 4, position.x);
		buf_positions.put(index * 4 + 1, position.y);
		buf_positions.put(index * 4 + 2, position.z);
	}

	@Override
	public Vector3f getVelocity() {
		float x = buf_velocities.get(index * 4);
		float y = buf_velocities.get(index * 4 + 1);
		float z = buf_velocities.get(index * 4 + 2);

		return new Vector3f(x, y, z);
	}

	@Override
	public void setVelocity(Vector3f velocity) {
		buf_velocities.put(index * 4, velocity.x);
		buf_velocities.put(index * 4 + 1, velocity.y);
		buf_velocities.put(index * 4 + 2, velocity.z);
	}

	public Vector3f getPredictedPosition() {
		return predictedPosition;
	}

	public void setPredictedPosition(Vector3f predictedPosition) {
		this.predictedPosition = predictedPosition;
	}

	public Vector3f getFTLCorrectionVector() {
		return FTLCorrectionVector;
	}

	public void setFTLCorrectionVector(Vector3f fTLCorrectionVector) {
		FTLCorrectionVector = fTLCorrectionVector;
	}
}