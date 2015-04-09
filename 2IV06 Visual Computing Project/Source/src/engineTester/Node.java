package engineTester;

import org.lwjgl.util.vector.Vector3f;

public class Node {
	public float Weight = 0;
	public Vector3f Velocity = new Vector3f(0,0,0);
	private Vector3f position = new Vector3f(0,0,0);
	public Vector3f Gradient = new Vector3f(0,0,0);
	public boolean inside = false;
	
	public Vector3f getPosition() {
		return position;
	}

	public Node(Vector3f position) {
		this.position = position;
	}
	
	public Node() {
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
