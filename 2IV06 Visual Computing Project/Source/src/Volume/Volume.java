package Volume;

import java.util.List;
import org.lwjgl.util.vector.Vector3f;

import engineTester.Node;
import toolbox.VectorMath;

public abstract class Volume {
	protected float spacing = 5f;
	private float epsilon = 0.1f;

	public Volume() {
	}

	public abstract void Clear();

	public float getSpacing() {
		return spacing;
	}
	
	public abstract int getGridSize();

	public Node getNode(Vector3f position) {
		return getNode(position.x, position.y, position.z);
	}

	public abstract Node getNode(float x, float y, float z);

	public Vector3f getRefrencePosition(Vector3f position) {
		Vector3f ref = new Vector3f();

		ref.x = (float) (Math.floor(position.x / spacing) * spacing);
		ref.y = (float) (Math.floor(position.y / spacing) * spacing);
		ref.z = (float) (Math.floor(position.z / spacing) * spacing);

		return ref;
	}

	public Node getNodeValue(Vector3f position) {

		float x = position.x;
		float y = position.y;
		float z = position.z;

		Vector3f n0 = getRefrencePosition(position);

		float x0 = n0.x;
		float y0 = n0.y;
		float z0 = n0.z;

		float xd = (x - x0) / spacing;
		float yd = (y - y0) / spacing;
		float zd = (z - z0) / spacing;

		Node n000 = getNode(x0, y0, z0);
		Node n010 = getNode(x0, y0 + spacing, z0);
		Node n001 = getNode(x0, y0, z0 + spacing);
		Node n011 = getNode(x0, y0 + spacing, z0 + spacing);
		Node n100 = getNode(x0 + spacing, y0, z0);
		Node n110 = getNode(x0 + spacing, y0 + spacing, z0);
		Node n101 = getNode(x0 + spacing, y0, z0 + spacing);
		Node n111 = getNode(x0 + spacing, y0 + spacing, z0 + spacing);

		Vector3f v00 = VectorMath.Sum(VectorMath.Product(n000.Velocity, (1 - xd)), VectorMath.Product(n100.Velocity, xd));
		Vector3f v10 = VectorMath.Sum(VectorMath.Product(n010.Velocity, (1 - xd)), VectorMath.Product(n110.Velocity, xd));
		Vector3f v01 = VectorMath.Sum(VectorMath.Product(n001.Velocity, (1 - xd)), VectorMath.Product(n101.Velocity, xd));
		Vector3f v11 = VectorMath.Sum(VectorMath.Product(n011.Velocity, (1 - xd)), VectorMath.Product(n111.Velocity, xd));

		Vector3f v0 = VectorMath.Sum(VectorMath.Product(v00, (1 - yd)), VectorMath.Product(v10, yd));
		Vector3f v1 = VectorMath.Sum(VectorMath.Product(v01, (1 - yd)), VectorMath.Product(v11, yd));

		Vector3f v = VectorMath.Sum(VectorMath.Product(v0, (1 - zd)), VectorMath.Product(v1, zd));

		Node new_node = new Node();
		if (n000.Weight != 0) {
			new_node.Velocity = v;
			new_node.Gradient = n000.Gradient;
		}
		
		return new_node;
	}
	
	public Vector3f normalizeGradient(Vector3f gradient) {
		float length = gradient.length();
		return VectorMath.Divide(gradient, length + epsilon);
	}

}