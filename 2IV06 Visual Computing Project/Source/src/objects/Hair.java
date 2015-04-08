package objects;

import java.util.ArrayList;

public class Hair {
	
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
}
