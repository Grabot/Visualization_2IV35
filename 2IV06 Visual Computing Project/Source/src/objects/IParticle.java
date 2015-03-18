package objects;

import org.lwjgl.util.vector.Vector3f;

public interface IParticle 
{
	public Vector3f getPosition();
	
	public void setPosition(Vector3f position);
	
	public Vector3f getVelocity();
	
	public void setVelocity(Vector3f velocity);
}