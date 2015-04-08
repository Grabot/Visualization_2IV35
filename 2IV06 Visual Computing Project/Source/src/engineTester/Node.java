package engineTester;

import org.lwjgl.util.vector.Vector3f;

public class Node {
	public float Weight = 0;
	public boolean containsObject = false;
	public Vector3f Velocity = new Vector3f(0,0,0);
	public Vector3f Gradient = new Vector3f(0,0,0);
	
	public void Clear() {
		Weight = 0;
		Velocity = new Vector3f(0,0,0);
		Gradient = new Vector3f(0,0,0);
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
