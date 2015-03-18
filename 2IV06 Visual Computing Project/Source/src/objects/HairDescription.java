package objects;

import org.lwjgl.util.vector.Vector3f;

public class HairDescription {
	private int size = 0;
	private Vector3f position = new Vector3f();
	
	public HairDescription(Vector3f position, int size)
	{
		this.position = position;
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public Vector3f getPosition() {
		return position;
	}
	
}
