package Volume;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;

import toolbox.UtilCL;
import toolbox.VectorMath;
import engineTester.Node;

public class FixedVolume extends Volume {

	private FloatBuffer buf_grid_weight;
	private FloatBuffer buf_grid_velocity;
	private FloatBuffer buf_grid_gradient;

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
		buf_grid_weight = UtilCL.toFloatBuffer(new float[width * height * depth]);
		buf_grid_velocity = UtilCL.toFloatBuffer(new float[width * height * depth * 4]);
		buf_grid_gradient = UtilCL.toFloatBuffer(new float[width * height * depth * 4]);
	}
	
	public int getGridSize() {
		return size;
	}
	
	public FloatBuffer getWeight() {
		return buf_grid_weight;
	}

	public FloatBuffer getVelocity() {
		return buf_grid_velocity;
	}

	public FloatBuffer getGradients() {
		return buf_grid_gradient;
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

			buf_grid_weight.put(i, 0);

			buf_grid_velocity.put(i * 4, 0);
			buf_grid_velocity.put(i * 4 + 1, 0);
			buf_grid_velocity.put(i * 4 + 2, 0);
			buf_grid_velocity.put(i * 4 + 3, 0);

			buf_grid_gradient.put(i * 4, 0);
			buf_grid_gradient.put(i * 4 + 1, 0);
			buf_grid_gradient.put(i * 4 + 2, 0);
			buf_grid_gradient.put(i * 4 + 3, 0);
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
		
		int x1 = getKey(nodePosition);
		int x2 = getKey(VectorMath.Sum(nodePosition, new Vector3f(spacing, 0, 0)));
		int x3 = getKey(VectorMath.Sum(nodePosition, new Vector3f(0, spacing, 0)));
		int x4 = getKey(VectorMath.Sum(nodePosition, new Vector3f(spacing, spacing, 0)));
		int x5 = getKey(VectorMath.Sum(nodePosition, new Vector3f(0, 0, spacing)));
		int x6 = getKey(VectorMath.Sum(nodePosition, new Vector3f(spacing, 0, spacing)));
		int x7 = getKey(VectorMath.Sum(nodePosition, new Vector3f(0, spacing, spacing)));
		int x8 = getKey(VectorMath.Sum(nodePosition, new Vector3f(spacing, spacing, spacing)));
		
		buf_grid_weight.put(x1, buf_grid_weight.get(x1) + w1);
		buf_grid_weight.put(x2, buf_grid_weight.get(x2) + w2);
		buf_grid_weight.put(x3, buf_grid_weight.get(x3) + w3);
		buf_grid_weight.put(x4, buf_grid_weight.get(x4) + w4);
		buf_grid_weight.put(x5, buf_grid_weight.get(x5) + w5);
		buf_grid_weight.put(x6, buf_grid_weight.get(x6) + w6);
		buf_grid_weight.put(x7, buf_grid_weight.get(x7) + w7);
		buf_grid_weight.put(x8, buf_grid_weight.get(x8) + w8);
		
		buf_grid_velocity.put(x1 * 4, buf_grid_velocity.get(x1 * 4) + velocity.x * w1);
		buf_grid_velocity.put(x1 * 4 + 1, buf_grid_velocity.get(x1 * 4 + 1) + velocity.y * w1);
		buf_grid_velocity.put(x1 * 4 + 2, buf_grid_velocity.get(x1 * 4 + 2) + velocity.z * w1);
		
		buf_grid_velocity.put(x2 * 4, buf_grid_velocity.get(x2 * 4) + velocity.x * w2);
		buf_grid_velocity.put(x2 * 4 + 1, buf_grid_velocity.get(x2 * 4 + 1) + velocity.y * w2);
		buf_grid_velocity.put(x2 * 4 + 2, buf_grid_velocity.get(x2 * 4 + 2) + velocity.z * w2);
		
		buf_grid_velocity.put(x3 * 4, buf_grid_velocity.get(x3 * 4) + velocity.x * w3);
		buf_grid_velocity.put(x3 * 4 + 1, buf_grid_velocity.get(x2 * 4 + 1) + velocity.y * w3);
		buf_grid_velocity.put(x3 * 4 + 2, buf_grid_velocity.get(x2 * 4 + 2) + velocity.z * w3);
		
		buf_grid_velocity.put(x4 * 4, buf_grid_velocity.get(x4 * 4) + velocity.x * w4);
		buf_grid_velocity.put(x4 * 4 + 1, buf_grid_velocity.get(x4 * 4 + 1) + velocity.y * w4);
		buf_grid_velocity.put(x4 * 4 + 2, buf_grid_velocity.get(x4 * 4 + 2) + velocity.z * w4);
		
		buf_grid_velocity.put(x5 * 4, buf_grid_velocity.get(x5 * 4) + velocity.x * w5);
		buf_grid_velocity.put(x5 * 4 + 1, buf_grid_velocity.get(x5 * 4 + 1) + velocity.y * w5);
		buf_grid_velocity.put(x5 * 4 + 2, buf_grid_velocity.get(x5 * 4 + 2) + velocity.z * w5);
		
		buf_grid_velocity.put(x6 * 4, buf_grid_velocity.get(x6 * 4) + velocity.x * w6);
		buf_grid_velocity.put(x6 * 4 + 1, buf_grid_velocity.get(x6 * 4 + 1) + velocity.y * w6);
		buf_grid_velocity.put(x6 * 4 + 2, buf_grid_velocity.get(x6 * 4 + 2) + velocity.z * w6);
		
		buf_grid_velocity.put(x7 * 4, buf_grid_velocity.get(x7 * 4) + velocity.x * w7);
		buf_grid_velocity.put(x7 * 4 + 1, buf_grid_velocity.get(x7 * 4 + 1) + velocity.y * w7);
		buf_grid_velocity.put(x7 * 4 + 2, buf_grid_velocity.get(x7 * 4 + 2) + velocity.z * w7);
	
		buf_grid_velocity.put(x8 * 4, buf_grid_velocity.get(x8 * 4) + velocity.x * w8);
		buf_grid_velocity.put(x8 * 4 + 1, buf_grid_velocity.get(x8 * 4 + 1) + velocity.y * w8);
		buf_grid_velocity.put(x8 * 4 + 2, buf_grid_velocity.get(x8 * 4 + 2) + velocity.z * w8);
	}

	public void calculateAverageVelocityAndGradients() {
		for (int i = 0; i < size; i++) {
			if (buf_grid_weight.get(i) > 0) {
				
				// Gradient
				float x1 = buf_grid_weight.get(i + 1);
				float x2 = buf_grid_weight.get(i - 1);
				float y1 = buf_grid_weight.get(i + width);
				float y2 = buf_grid_weight.get(i - width);
				float z1 = buf_grid_weight.get(i + width * height);
				float z2 = buf_grid_weight.get(i - width * height);

				Vector3f gradient = new Vector3f((x1 - x2) / (2 * spacing), (y1 - y2) / (2 * spacing), (z1 - z2) / (2 * spacing));
				gradient = normalizeGradient(gradient);
				buf_grid_gradient.put(i * 4, gradient.x);
				buf_grid_gradient.put(i * 4 + 1, gradient.y);
				buf_grid_gradient.put(i * 4 + 2, gradient.z);

				// Velocity
				buf_grid_velocity.put(i * 4, buf_grid_velocity.get(i * 4) / buf_grid_weight.get(i));
				buf_grid_velocity.put(i * 4 + 1, buf_grid_velocity.get(i * 4 + 1) / buf_grid_weight.get(i));
				buf_grid_velocity.put(i * 4 + 2, buf_grid_velocity.get(i * 4 + 2) / buf_grid_weight.get(i));
			}
		}
	}
	
	@Override
	public Node getNode(float x, float y, float z) {
		int key = getKey(new Vector3f(x, y, z));

		if (key > 0) {
			Node node = new Node();
			node.Weight = buf_grid_weight.get(key);
			node.Velocity = new Vector3f(buf_grid_velocity.get(key * 4), buf_grid_velocity.get(key * 4 + 1), buf_grid_velocity.get(key * 4 + 2));
			node.Gradient = new Vector3f(buf_grid_gradient.get(key * 4), buf_grid_gradient.get(key * 4 + 1), buf_grid_gradient.get(key * 4 + 2));
		
			return node;
		}

		System.out.println("error");
		
		return new Node();
	}
}