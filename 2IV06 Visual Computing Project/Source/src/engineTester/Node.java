package engineTester;

import java.util.ArrayList;

import objects.Particle;

import org.lwjgl.util.vector.Vector3f;

public class Node {
	public float Weight = 0;
	public boolean containsObject = false;
	public Vector3f Velocity = new Vector3f(0,0,0);
	private Vector3f position = new Vector3f(0,0,0);
	private Vector3f gradient = new Vector3f(0,0,0);
	
	public void Clear() {
		Weight = 0;
		Velocity = new Vector3f(0,0,0);
		gradient = new Vector3f(0,0,0);
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
