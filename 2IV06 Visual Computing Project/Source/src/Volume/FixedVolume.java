package Volume;

import org.lwjgl.util.vector.Vector3f;

import toolbox.VectorMath;
import engineTester.Node;

public class FixedVolume extends Volume {

	private float[] grid_weight;
	private float[] grid_velocity;
	private float[] grid_gradient;

	private int size = 0;
	private int width = 32;
	private int height = 32;
	private int depth = 32;

	public FixedVolume(float spacing) {
		this.width = (int) (this.spacing / spacing * this.width);
		this.height = (int) (this.spacing / spacing * this.height);
		this.depth = (int) (this.spacing / spacing * this.depth);
		this.spacing = spacing;

		init();
	}

	public FixedVolume() {
		init();
	}

	private void init() {
		size = width * height * depth;
		grid_weight = new float[width * height * depth];
		grid_velocity = new float[width * height * depth * 4];
		grid_gradient = new float[width * height * depth * 4];
	}
	
	public int getGridSize() {
		return size;
	}
	
	public float[] getWeight() {
		return grid_weight;
	}

	public float[] getVelocity() {
		return grid_velocity;
	}

	public float[] getGradients() {
		return grid_gradient;
	}

	public int getKey(Vector3f position) {
		int x = (int) Math.floor(position.x / spacing);
		int y = (int) Math.floor(position.y / spacing);
		int z = (int) Math.floor(position.z / spacing);

		if (x < width && y < height && z < depth && x >= 0 && y >= 0 && z >= 0) {

			return x + width * (y + height * z);
		}

		return -1;
	}

	@Override
	public void Clear() {
		for (int i = 0; i < size; i++) {

			grid_weight[i] = 0;

			grid_velocity[i * 4] = 0;
			grid_velocity[i * 4 + 1] = 0;
			grid_velocity[i * 4 + 2] = 0;
			grid_velocity[i * 4 + 3] = 0;

			grid_gradient[i * 4] = 0;
			grid_gradient[i * 4 + 1] = 0;
			grid_gradient[i * 4 + 2] = 0;
			grid_gradient[i * 4 + 3] = 0;
		}
	}

	public void addValues(Vector3f position, float weight, Vector3f velocity) {
		Vector3f nodePosition = getRefrencePosition(position);
		
		float xd = (position.x - nodePosition.x) / (spacing);
		float yd = (position.y - nodePosition.y) / (spacing);
		float zd = (position.z - nodePosition.z) / (spacing);
		
		float w1 = weight * xd * yd * zd;
		float w2 = weight * (1 - xd) * yd * zd;
		float w3 = weight * xd * (1 - yd) * zd;
		float w4 = weight * (1 - xd) * (1 - yd) * zd;
		float w5 = weight * xd * yd * (1 - zd);
		float w6 = weight * (1 - xd) * yd * (1 - zd);
		float w7 = weight * xd * (1 - yd) * (1 - zd);
		float w8 = weight * (1 - xd) * (1 - yd) * (1 - zd);
		
		Vector3f x1 = nodePosition;
		Vector3f x2 = VectorMath.Sum(nodePosition, new Vector3f(spacing, 0, 0));
		Vector3f x3 = VectorMath.Sum(nodePosition, new Vector3f(0, spacing, 0));
		Vector3f x4 = VectorMath.Sum(nodePosition, new Vector3f(spacing, spacing, 0));
		Vector3f x5 = VectorMath.Sum(nodePosition, new Vector3f(0, 0, spacing));
		Vector3f x6 = VectorMath.Sum(nodePosition, new Vector3f(spacing, 0, spacing));
		Vector3f x7 = VectorMath.Sum(nodePosition, new Vector3f(0, spacing, spacing));
		Vector3f x8 = VectorMath.Sum(nodePosition, new Vector3f(spacing, spacing, spacing));
		
		grid_weight[getKey(x1)] += w1;
		grid_weight[getKey(x2)] += w2;
		grid_weight[getKey(x3)] += w3;
		grid_weight[getKey(x4)] += w4;
		grid_weight[getKey(x5)] += w5;
		grid_weight[getKey(x6)] += w6;
		grid_weight[getKey(x7)] += w7;
		grid_weight[getKey(x8)] += w8;
		
		grid_velocity[getKey(x1) * 4] += velocity.x * w1;
		grid_velocity[getKey(x1) * 4 + 1] += velocity.y * w1;
		grid_velocity[getKey(x1) * 4 + 2] += velocity.z * w1;
		
		grid_velocity[getKey(x2) * 4] += velocity.x * w2;
		grid_velocity[getKey(x2) * 4 + 1] += velocity.y * w2;
		grid_velocity[getKey(x2) * 4 + 2] += velocity.z * w2;	
				
		grid_velocity[getKey(x3) * 4] += velocity.x * w3;
		grid_velocity[getKey(x3) * 4 + 1] += velocity.y * w3;
		grid_velocity[getKey(x3) * 4 + 2] += velocity.z * w3;	
				
		grid_velocity[getKey(x4) * 4] += velocity.x * w4;
		grid_velocity[getKey(x4) * 4 + 1] += velocity.y * w4;
		grid_velocity[getKey(x4) * 4 + 2] += velocity.z * w4;
		
		grid_velocity[getKey(x5) * 4] += velocity.x * w5;
		grid_velocity[getKey(x5) * 4 + 1] += velocity.y * w5;
		grid_velocity[getKey(x5) * 4 + 2] += velocity.z * w5;	
				
		grid_velocity[getKey(x6) * 4] += velocity.x * w6;
		grid_velocity[getKey(x6) * 4 + 1] += velocity.y * w6;
		grid_velocity[getKey(x6) * 4 + 2] += velocity.z * w6;
		
		grid_velocity[getKey(x7) * 4] += velocity.x * w5;
		grid_velocity[getKey(x7) * 4 + 1] += velocity.y * w5;
		grid_velocity[getKey(x7) * 4 + 2] += velocity.z * w5;	
				
		grid_velocity[getKey(x8) * 4] += velocity.x * w6;
		grid_velocity[getKey(x8) * 4 + 1] += velocity.y * w6;
		grid_velocity[getKey(x8) * 4 + 2] += velocity.z * w6;
	}

	public void calculateAverageVelocityAndGradients() {
		for (int i = 0; i < size; i++) {
			if (grid_weight[i] > 0) {
				
				// Gradient
				float x1 = grid_weight[i + 1];
				float x2 = grid_weight[i - 1];
				float y1 = grid_weight[i + width];
				float y2 = grid_weight[i - width];
				float z1 = grid_weight[i + width * height];
				float z2 = grid_weight[i - width * height];

				Vector3f gradient = new Vector3f((x1 - x2) / (2 * spacing), (y1 - y2) / (2 * spacing), (z1 - z2) / (2 * spacing));
				gradient = normalizeGradient(gradient);
				grid_gradient[i * 4] = gradient.x;
				grid_gradient[i * 4 + 1] = gradient.y;
				grid_gradient[i * 4 + 2] = gradient.z;

				// Velocity
				grid_velocity[i * 4] /= grid_weight[i];
				grid_velocity[i * 4 + 1] /= grid_weight[i];
				grid_velocity[i * 4 + 2] /= grid_weight[i];
			}
		}
	}
	
	@Override
	public Node getNode(float x, float y, float z) {
		int key = getKey(new Vector3f(x, y, z));

		if (key > 0) {
			Node node = new Node();
			node.Weight = grid_weight[key];
			node.Velocity = new Vector3f(grid_velocity[key * 4], grid_velocity[key * 4 + 1], grid_velocity[key * 4 + 2]);
			node.Gradient = new Vector3f(grid_gradient[key * 4], grid_gradient[key * 4 + 1], grid_gradient[key * 4 + 2]);
		
			return node;
		}

		System.out.println("error");
		
		return new Node();
	}
}