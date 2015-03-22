package Volume;

import java.util.List;
import org.lwjgl.util.vector.Vector3f;

import engineTester.Node;
import toolbox.VectorMath;

public abstract class Volume {
	protected float spacing = 5f;
	protected float epsilon = 0.1f;

	public Volume() {
	}

	public float getSpacing() {
		return spacing;
	}

	public abstract void Clear();
	
	public abstract Node getNode(Vector3f key);

	public abstract List<Node> getGridCells();
	
	public void addValues(Vector3f position, float weight, Vector3f velocity) {

		Vector3f nodePosition = getNode(position).getPosition();

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
	
	public void calculateAverageVelocityAndGradients() {
		List<Node> nodes = getGridCells();
		for(Node node: nodes) {
			if (node.Weight >0){
			// Gradient
			float x1 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(spacing, 0, 0))).Weight;
			float x2 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(-spacing, 0, 0))).Weight;
			float y1 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(0, spacing, 0))).Weight;
			float y2 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(0, -spacing, 0))).Weight;
			float z1 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(0, 0, spacing))).Weight;
			float z2 = getNode(VectorMath.Sum(node.getPosition(), new Vector3f(0, 0, -spacing))).Weight;
			
			Vector3f gradient = new Vector3f( (x1 - x2)/(2*spacing), (y1 - y2)/(2*spacing), (z1 - z2)/(2*spacing) );
			gradient = normalizeGradient(gradient);
			node.setGradient( gradient );

			// Velocity
			node.Velocity = VectorMath.Divide(node.Velocity, node.Weight);
		}
		}
	}
	
	public Vector3f normalizeGradient(Vector3f gradient) {
		float length = gradient.length();
		return VectorMath.Divide(gradient, length + epsilon);
	}

}