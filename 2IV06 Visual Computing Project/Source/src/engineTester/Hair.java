package engineTester;

import java.util.ArrayList;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Hair {

	private float particleDistance;
	private ArrayList<Particle> particles = new ArrayList<Particle>();

	public Hair(TexturedModel model, Vector3f position, int num_of_particles, float particleDistance) {
		this.particleDistance = particleDistance;
		for (int i = 0; i < num_of_particles; i++) {
			particles.add(new Particle(model, new Vector3f(position.x, position.y - (i * particleDistance), position.z)));
		}
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

}
