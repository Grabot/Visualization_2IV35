package Volume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import engineTester.Node;
import toolbox.VectorMath;

public class DynamicVolume extends Volume {
	private float spacing = 5f;
	private float epsilon = 0.1f;
	private Map<String, Node> nodes = new HashMap<String, Node>();

	public DynamicVolume() {
	}

	public void Clear() {
		nodes.clear();
	}

	@Override
	public float getSpacing() {
		return spacing;
	}

	public ArrayList<Node> getGridCells() {

		return new ArrayList<Node>(nodes.values());
	}

	/* (non-Javadoc)
	 * @see engineTester.IVolume#getNode(org.lwjgl.util.vector.Vector3f)
	 */
	@Override
	public Node getNode(Vector3f key) {
		return getNode(key, true);
	}

	private String getUniqueCode(Vector3f vec) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.valueOf(vec.x));
		stringBuilder.append(":");
		stringBuilder.append(String.valueOf(vec.y));
		stringBuilder.append(":");
		stringBuilder.append(String.valueOf(vec.z));
		
		return stringBuilder.toString();
	}
	
	private Node getNode(Vector3f key, boolean create) {
		Vector3f newvec = getKey(key);
		String newkey = getUniqueCode(newvec);

		if (!nodes.containsKey(newkey)) {
			Node node = new Node(newvec);
			if (create) {
				nodes.put(newkey, node);
			}
			return node;
		}

		return (Node) nodes.get(newkey);
	}

	/* (non-Javadoc)
	 * @see engineTester.IVolume#addValues(org.lwjgl.util.vector.Vector3f, float, org.lwjgl.util.vector.Vector3f)
	 */
	@Override
	public void addValues(Vector3f position, float weight, Vector3f velocity) {

		Vector3f nodePosition = getKey(position);

		// Difference between coordinates
		float xd = (position.x - nodePosition.x) / (spacing);
		float yd = (position.y - nodePosition.y) / (spacing);
		float zd = (position.z - nodePosition.z) / (spacing);

		// Get all eight node values
		float w1 = weight * xd * yd * zd;
		float w2 = weight * (1 - xd) * yd * zd;
		float w3 = weight * xd * (1 - yd) * zd;
		float w4 = weight * (1 - xd) * (1 - yd) * zd;
		float w5 = weight * xd * yd * (1 - zd);
		float w6 = weight * (1 - xd) * yd * (1 - zd);
		float w7 = weight * xd * (1 - yd) * (1 - zd);
		float w8 = weight * (1 - xd) * (1 - yd) * (1 - zd);

		// All node postitions
		Vector3f x1 = nodePosition;
		Vector3f x2 = VectorMath.Sum(nodePosition, new Vector3f(spacing, 0, 0));
		Vector3f x3 = VectorMath.Sum(nodePosition, new Vector3f(0, spacing, 0));
		Vector3f x4 = VectorMath.Sum(nodePosition, new Vector3f(spacing,
				spacing, 0));
		Vector3f x5 = VectorMath.Sum(nodePosition, new Vector3f(0, 0, spacing));
		Vector3f x6 = VectorMath.Sum(nodePosition, new Vector3f(spacing, 0,
				spacing));
		Vector3f x7 = VectorMath.Sum(nodePosition, new Vector3f(0, spacing,
				spacing));
		Vector3f x8 = VectorMath.Sum(nodePosition, new Vector3f(spacing,
				spacing, spacing));

		Node n1 = getNode(x1);

		Node n2 = getNode(x2);
		Node n3 = getNode(x3);
		Node n4 = getNode(x4);
		Node n5 = getNode(x5);
		Node n6 = getNode(x6);
		Node n7 = getNode(x7);
		Node n8 = getNode(x8);

		// Add trilinear weights
		n1.Weight += w1;
		n2.Weight += w2;
		n3.Weight += w3;
		n4.Weight += w4;
		n5.Weight += w5;
		n6.Weight += w6;
		n7.Weight += w7;
		n8.Weight += w8;

		// Add weight multiplied velocities
		n1.setVelocity(VectorMath.Sum(n1.Velocity,
				VectorMath.Product(velocity, w1)));
		n2.setVelocity(VectorMath.Sum(n2.Velocity,
				VectorMath.Product(velocity, w2)));
		n3.setVelocity(VectorMath.Sum(n3.Velocity,
				VectorMath.Product(velocity, w3)));
		n4.setVelocity(VectorMath.Sum(n4.Velocity,
				VectorMath.Product(velocity, w4)));
		n5.setVelocity(VectorMath.Sum(n5.Velocity,
				VectorMath.Product(velocity, w5)));
		n6.setVelocity(VectorMath.Sum(n6.Velocity,
				VectorMath.Product(velocity, w6)));
		n7.setVelocity(VectorMath.Sum(n7.Velocity,
				VectorMath.Product(velocity, w7)));
		n8.setVelocity(VectorMath.Sum(n8.Velocity,
				VectorMath.Product(velocity, w8)));
	}

	/* (non-Javadoc)
	 * @see engineTester.IVolume#getKey(org.lwjgl.util.vector.Vector3f)
	 */
	public Vector3f getKey(Vector3f position) {
		Vector3f vec = new Vector3f();
		vec.x = (float) Math.floor(position.x / spacing) * spacing;
		vec.y = (float) Math.floor(position.y / spacing) * spacing;
		vec.z = (float) Math.floor(position.z / spacing) * spacing;
		return vec;
	}

	/* (non-Javadoc)
	 * @see engineTester.IVolume#getNodeValue(org.lwjgl.util.vector.Vector3f)
	 */
	public Node getNodeValue(Vector3f position) {

		// TODO: ADD TRILINEAIR

		Node node = getNode(position);
		Node new_node = new Node(getKey(position));
		if (node.Weight != 0) {
			new_node.Velocity = VectorMath.Divide(node.Velocity, node.Weight);
			new_node.setGradient(node.getGradient());
		}
		return new_node;
	}
	
	/* (non-Javadoc)
	 * @see engineTester.IVolume#calculateAverageVelocityAndGradients()
	 */
	
	/* (non-Javadoc)
	 * @see engineTester.IVolume#normalizeGradient(org.lwjgl.util.vector.Vector3f)
	 */
	public Vector3f normalizeGradient(Vector3f gradient) {
		float length = gradient.length();
		return VectorMath.Divide(gradient, length + epsilon);
	}

}
