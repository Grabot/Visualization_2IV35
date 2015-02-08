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
			pitch -= 0.05f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pitch += 0.05f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			yaw -= 0.05f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			yaw += 0.05f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			position.z += 0.05f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			position.z -= 0.05f;
		}
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