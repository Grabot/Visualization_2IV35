package engineTester;

import java.util.ArrayList;
import java.util.List;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Particle extends Entity {

	private Vector3f force = new Vector3f(0, 0, 0);
	private Vector3f predictedPosition = new Vector3f(0, 0, 0);
	private List<Particle> neighbourParticles = new ArrayList<Particle>();

	public Particle(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		super(model, position, rotation, scale);
	}

	public void increaseForce(float dfx, float dfy, float dfz) {
		this.force.x += dfx;
		this.force.y += dfy;
		this.force.z += dfz;
	}

	public Vector3f getForce() {
		return force;
	}

	public void setPredictedPosition(Vector3f position) {
		predictedPosition = position;
	}

	public void addNeighbourParticle(Particle particle)
	{
		neighbourParticles.add(particle);
	}
	
	public void clearNeighbourList()
	{
		neighbourParticles.clear();
	}
}
