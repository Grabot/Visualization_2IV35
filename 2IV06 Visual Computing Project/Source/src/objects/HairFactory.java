package objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import models.TexturedModel;
import toolbox.UtilCL;

public class HairFactory {

	private float particleDistance = 5.0f;
	private TexturedModel model;
	private ArrayList<HairDescription> hairDescriptions = new ArrayList<HairDescription>();

	public HairFactory(TexturedModel model, float particleDistance) {
		this.particleDistance = particleDistance;
		this.model = model;
	}

	public void addHairDescription(HairDescription hairDescription) {
		hairDescriptions.add(hairDescription);
	}

	public Hairs Build() {

		// Memory size required
		int size = 0;
		int haircount = hairDescriptions.size();
		for (HairDescription hairDescription : hairDescriptions) {
			size += hairDescription.getSize();
		}
		
		// num of hairs
		int[] startindex = new int[haircount];
		int[] endindex = new int[haircount];

		// num of particles
		float[] positions = new float[size * 4];
		float[] velocities = new float[size *  4];
		float[] predictedpositions = new float[size *  4];
		
		// create all particle positions
		int index = 0;
		for (int h = 0; h < haircount; h++) {

			startindex[h] = index;

			for (int p = 0; p < hairDescriptions.get(h).getSize(); p++) {
				
				positions[index * 4] = hairDescriptions.get(h).getPosition().x + particleDistance * p;
				positions[index * 4 + 1] = hairDescriptions.get(h).getPosition().y;
				positions[index * 4 + 2] = hairDescriptions.get(h).getPosition().z;
				
				index++;
			}

			endindex[h] = index;
		}
		
		// Copy data to buffers
		IntBuffer buf_startindex = UtilCL.toIntBuffer(startindex);
		IntBuffer buf_endindex = UtilCL.toIntBuffer(endindex);

		FloatBuffer buf_pos = UtilCL.toFloatBuffer(positions);
		FloatBuffer buf_vel = UtilCL.toFloatBuffer(velocities);
		FloatBuffer buf_pred_pos = UtilCL.toFloatBuffer(predictedpositions);
		
		// Create corresponding objects
		Hairs hairs = new Hairs(buf_startindex, buf_endindex, buf_pos, buf_vel, buf_pred_pos);
		index = 0;
		for (int h = 0; h < haircount; h++) {
			
			Hair hair = new Hair(particleDistance);
			
			for (int p = 0; p < hairDescriptions.get(h).getSize(); p++) {
				
				Particle particle = new Particle(model, index, buf_pos, buf_vel);
				hair.addParticle(particle);
				index++;
			}

			hairs.add(hair);
		}
		
		return hairs;		
	}

}
