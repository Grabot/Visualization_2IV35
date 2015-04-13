package objects;

import java.util.ArrayList;

import models.RawModel;

public class Hair {
	
	private RawModel model;
	private float particleDistance;
	private ArrayList<Particle> particles = new ArrayList<Particle>();
		
	public Hair(float particleDistance) {
		this.setParticleDistance(particleDistance);
	}
	
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	
	public void addParticle(Particle particle) {
		particles.add(particle);
	}

	public float getParticleDistance() {
		return particleDistance;
	}

	public void setParticleDistance(float particleDistance) {
		this.particleDistance = particleDistance;
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
			//Particle particle = getParticles().get(i);
			normals[i * 3] = 1;
			normals[i * 3 + 1] = -1;
			normals[i * 3 + 2] = 1;
		}
		return normals;
	}
}