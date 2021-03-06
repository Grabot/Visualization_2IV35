package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0, 0, 40);
	private float pitch;
	private float yaw;
	private float roll;

	public Camera() {
	}

	public void move() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			pitch -= 1.25f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pitch += 1.25f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			yaw -= 1.25f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			yaw += 1.25f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			position.z += 1.25f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			position.z -= 1.25f;
		}
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

}