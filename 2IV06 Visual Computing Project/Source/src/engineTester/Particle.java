package engineTester;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Particle extends Entity {

	private boolean isRoot = false;
	private Vector3f predictedPosition;
	private Vector3f velocity = new Vector3f(0, 0, 0);
	private Vector3f FTLCorrectionVector = new Vector3f(0, 0, 0);

	public Particle(TexturedModel model, Vector3f position) {
		super(model, position, new Vector3f(0, 0, 0), 0.5f);
		this.predictedPosition = position;
	}

	public Particle(TexturedModel model, Vector3f position, boolean isRoot) {
		super(model, position, new Vector3f(0, 0, 0), 0.5f);
		this.predictedPosition = position;
		this.isRoot = isRoot;
	}

	public Vector3f getFTLCorrectionVector() {
		return FTLCorrectionVector;
	}

	public void setFTLCorrectionVector(Vector3f fTLCorrectionVector) {
		FTLCorrectionVector = fTLCorrectionVector;
	}
	
	public void setVelocity(Vector3f value) {
		if(!isRoot()) {
			this.velocity = value;
		}
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public Vector3f getPredictedPosition() {
		return predictedPosition;
	}

	public void setPredictedPosition(Vector3f predictedPosition) {
		if (!isRoot()) {
			this.predictedPosition = predictedPosition;
		}
	}
}
