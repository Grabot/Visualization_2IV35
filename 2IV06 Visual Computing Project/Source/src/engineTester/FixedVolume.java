package engineTester;

import org.lwjgl.util.vector.Vector3f;

public class FixedVolume {
	
	private float spacing = 5;
	private int width = 32;
	private int height = 32;
	private int depth = 32;

	private float[] weights;
	private float[] velocities;
	private float[] gradients;
	
	private Node[] nodes = new Node[width * height * depth];

	public FixedVolume() {
		weights = new float[width * height * depth];
		velocities = new float[width * height * depth * 4];
		gradients = new float[width * height * depth * 4];
	}

	public int getKey(Vector3f position) {
		int x = (int) Math.floor(position.x / spacing);
		int y = (int) Math.floor(position.y / spacing);
		int z = (int) Math.floor(position.z / spacing);

		if (x < width && y < height && z < depth && 
				x >= 0 && y >= 0 && z >= 0) {

			return x + width * (y + height * z);		
		}
		
		return -1;
	}
	
	public void Clear() {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
		for (int i = 0; i < velocities.length; i++) {
			velocities[i] = 0;
		}
		for (int i = 0; i < gradients.length; i++) {
			gradients[i] = 0;
		}
	}

}