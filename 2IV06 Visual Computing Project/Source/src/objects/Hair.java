package objects;

import java.util.ArrayList;

public class Hair {
	
	public float particledistance;
	public ArrayList<Particle> particles = new ArrayList<Particle>();
		
	public Hair() {
	}
	
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	
	public void addParticle(Particle particle) {
		particles.add(particle);
	}
}
