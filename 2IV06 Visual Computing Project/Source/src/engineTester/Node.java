package engineTester;

import java.util.ArrayList;

import objects.Particle;

import org.lwjgl.util.vector.Vector3f;

public class Node {
	public float Weight = 0;
	public Vector3f Velocity = new Vector3f(0,0,0);
	private Vector3f position = new Vector3f(0,0,0);
	private Vector3f gradient = new Vector3f(0,0,0);
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	
	public Vector3f getGradient() {
		return gradient;
	}

	public void setGradient(Vector3f gradient) {
		this.gradient = gradient;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Node(Vector3f position) {
		this.position = position;
	}
	
	public float getWeight() {
		return Weight;
	}
	public void setWeight(float weight) {
		Weight = weight;
	}
	public Vector3f getVelocity() {
		return Velocity;
	}
	public void setVelocity(Vector3f velocity) {
		Velocity = velocity;
	}
}
