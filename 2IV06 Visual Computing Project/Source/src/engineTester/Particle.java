package engineTester;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Particle extends Entity {

	private Vector3f velocity = new Vector3f(0, 0, 0);
	private boolean isRoot = false;
	private Vector3f predictedPosition;

	public Particle(TexturedModel model, Vector3f position) {
		super(model, position, new Vector3f(0,0,0), 1);
	}
	
	public Particle(TexturedModel model, Vector3f position, boolean isRoot) {
		super(model, position, new Vector3f(0,0,0), 1);
		this.isRoot = isRoot;
	}

	public void setVelocity(Vector3f value){
		this.velocity = value;
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
		this.predictedPosition = predictedPosition;
	}
}
