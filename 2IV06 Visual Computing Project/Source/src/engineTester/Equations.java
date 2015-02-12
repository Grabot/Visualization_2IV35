package engineTester;

import org.lwjgl.util.vector.Vector3f;

public class Equations {

	public static Vector3f Equation1(Vector3f position, Vector3f velocity, Vector3f force, float deltaT) {
		return new Vector3f(position.x + deltaT * velocity.x + deltaT * deltaT
				* force.x, position.y + deltaT * velocity.y + deltaT * deltaT
				* force.y, position.z + deltaT * velocity.z + deltaT * deltaT
				* force.z);
	}

	public static void SatisfyConstraints(Particle p1, Particle p2) {
		Vector3f x1 = p1.getPredictedPosition();
		Vector3f x2 = p2.getPredictedPosition();

		Vector3f delta = new Vector3f(x2.x - x1.x, x2.y - x1.y, x2.z - x1.z);
		float deltaLength = (float) Math.sqrt(Vector3f.dot(delta, delta));

		// Restlength is set to 2 for now
		float diff = (deltaLength - 2) / deltaLength;

		//x1.x -= delta.x * 0.5 * diff;
		//x1.y -= delta.y * 0.5 * diff;
		//x1.z -= delta.z * 0.5 * diff;

		x2.x -= delta.x *  diff;
		x2.y -= delta.y *  diff;
		x2.z -= delta.z * diff;

		p1.setPredictedPosition(x1);
		p2.setPredictedPosition(x2);
	}

	public static Vector3f Equation3(Vector3f predictedPosition, Vector3f currentPosition, float deltaT) {
		return new Vector3f((predictedPosition.x - currentPosition.x) / deltaT, (predictedPosition.y - currentPosition.y)
				/ deltaT, (predictedPosition.z - currentPosition.z) / deltaT);
	}
}
