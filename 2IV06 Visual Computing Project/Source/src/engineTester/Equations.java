package engineTester;

import org.lwjgl.util.vector.Vector3f;

import toolbox.VectorMath;

public class Equations {
	private static float time_damping = 0.95f;

	// Verlet integration to calculate predicted position
	public static void CalculatePredictedPositions(Hair hair, Vector3f force,
			float deltaT) {
		for (Particle particle : hair.getParticles()) {

			particle.setPredictedPosition(new Vector3f(
					particle.getPosition().x + deltaT * particle.getVelocity().x * time_damping + deltaT * deltaT * force.x,
					particle.getPosition().y + deltaT * particle.getVelocity().y * time_damping + deltaT * deltaT * force.y,
					particle.getPosition().z + deltaT * particle.getVelocity().z * time_damping + deltaT * deltaT * force.z));
		}
	}

	// Equidistance contraint
	public static void FixedDistanceContraint(Hair hair) {
		for (int i = 1; i < hair.getParticles().size(); i++) {
			Particle parent = hair.getParticles().get(i - 1);
			Particle particle = hair.getParticles().get(i);

			Vector3f x1 = parent.getPredictedPosition();
			Vector3f x2 = particle.getPredictedPosition();

			Vector3f delta = VectorMath.Subtract(x2, x1);
			delta.normalise();

			float particleDistance = hair.getParticleDistance();

			particle.setPredictedPosition(new Vector3f(x1.x + delta.x
					* particleDistance, x1.y + delta.y * particleDistance, x1.z
					+ delta.z * particleDistance));

			particle.setFTLCorrectionVector(VectorMath.Subtract(x2,
					particle.getPredictedPosition()));
		}
	}

	public static void CalculateParticleVelocities(Hair hair, float deltaT,
			float correctionScale) {
		for (int i = 1; i < hair.getParticles().size(); i++) {
			Particle parent = hair.getParticles().get(i - 1);
			Particle particle = hair.getParticles().get(i);

			if(parent.isRoot()) {
				continue;
			}
			
			Vector3f firstPart = VectorMath.Divide(VectorMath.Subtract(parent.getPredictedPosition(), parent.getPosition()), deltaT);
			
			parent.setVelocity(VectorMath.Sum(firstPart, VectorMath.Product(VectorMath.Divide(particle.getFTLCorrectionVector(), deltaT), correctionScale)));
		
			// if last particle
			if (i == hair.getParticles().size() - 1) {
				particle.setVelocity(VectorMath.Divide(
						VectorMath.Subtract(particle.getPredictedPosition(),
								particle.getPosition()), deltaT));
			}
		}
	}

	public static void UpdateParticlePositions(Hair hair) {
		for (Particle particle: hair.getParticles()) {
			particle.setPosition(particle.getPredictedPosition());
		}
	}

}
