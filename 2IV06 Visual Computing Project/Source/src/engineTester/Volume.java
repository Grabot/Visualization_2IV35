package engineTester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Position;

import org.lwjgl.util.vector.Vector3f;

import toolbox.VectorMath;

public class Volume {
	private float spacing = 5;
	private Map<Vector3f, Node> nodes = new HashMap<Vector3f, Node>();

	public Volume(float spacing) {
		this.spacing = spacing;
	}

	public void Clear() {
		nodes.clear();
	}

	private Node getNode(Vector3f key) {
		key = getKey(key);

		if (!nodes.containsKey(key)) {
			Node node = new Node();
			nodes.put(key, node);
			return node;
		}

		return (Node) nodes.get(key);
	}

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
		Vector3f x1 = position;
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
		n1.Velocity = VectorMath.Sum(n1.Velocity, VectorMath.Product(velocity, w1));
		n2.Velocity = VectorMath.Sum(n2.Velocity, VectorMath.Product(velocity, w2));
		n3.Velocity = VectorMath.Sum(n3.Velocity, VectorMath.Product(velocity, w3));
		n4.Velocity = VectorMath.Sum(n4.Velocity, VectorMath.Product(velocity, w4));
		n5.Velocity = VectorMath.Sum(n1.Velocity, VectorMath.Product(velocity, w5));
		n6.Velocity = VectorMath.Sum(n1.Velocity, VectorMath.Product(velocity, w6));
		n7.Velocity = VectorMath.Sum(n1.Velocity, VectorMath.Product(velocity, w7));
		n8.Velocity = VectorMath.Sum(n1.Velocity, VectorMath.Product(velocity, w8));
	}

	private Vector3f getKey(Vector3f position) {
		Vector3f vec = new Vector3f();
		vec.x = (float) Math.floor(position.x / spacing) * spacing;
		vec.y = (float) Math.floor(position.y / spacing) * spacing;
		vec.z = (float) Math.floor(position.z / spacing) * spacing;
		return vec;
	}

	public Node getNodeValue(Vector3f position) {

		// TODO: ADD TRILINEAIR

		return getNode(position);
	}

}
