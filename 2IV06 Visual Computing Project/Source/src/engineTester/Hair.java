package engineTester;

import java.util.ArrayList;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Hair {

	private float particleDistance;
	private RawModel model;
	private Particle root;
	private ArrayList<Particle> particles = new ArrayList<Particle>();

	public Hair(TexturedModel model, Vector3f position, int num_of_particles, float particleDistance) {
		this.particleDistance = particleDistance;
		for (int i = 0; i < num_of_particles; i++) {
			if (i == 0) {
				this.root = new Particle(model, new Vector3f(position.x, position.y,
						position.z), true);
				particles.add(root);
			} else {
				particles.add(new Particle(model, new Vector3f(
						position.x , position.y+ (i * particleDistance),
						position.z)));
			}
		}
	}

	public Vector3f getPosition() {
		return root.getPosition();
	}

	public void setPosition(Vector3f position) {
		root.setPosition(position);
	}

	public RawModel getRawModel() {
		return model;
	}

	public void setRawModel(RawModel model) {
		this.model = model;
	}

	public int[] getIndices() {
		int[] indices = new int[getParticles().size() * 2];
		for (int i = 0; i < getParticles().size(); i++) {
			if (i > 0) {
				indices[(i - 1) * 2] = i - 1;
				indices[(i - 1) * 2 + 1] = i;
			}
		}
		return indices;
	}

	public float[] getVertices() {
		float[] positions = new float[getParticles().size() * 3];
		for (int i = 0; i < getParticles().size(); i++) {
			Particle particle = getParticles().get(i);
			positions[i * 3] = particle.getPosition().x;
			positions[i * 3 + 1] = particle.getPosition().y;
			positions[i * 3 + 2] = particle.getPosition().z;
		}
		return positions;
	}

	public float[] getNormals() {
		float[] normals = new float[getParticles().size() * 3];
		for (int i = 0; i < getParticles().size(); i++) {
			Particle particle = getParticles().get(i);
			normals[i * 3] = 1;
			normals[i * 3 + 1] = 0;
			normals[i * 3 + 2] = 1;
		}
		return normals;
	}
	
	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public float getParticleDistance() {
		return particleDistance;
	}

}
