package toolbox;

import org.lwjgl.util.vector.Vector3f;

public class VectorMath {

	public static Vector3f Sum(Vector3f v1, Vector3f v2) {
		return new Vector3f(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static Vector3f Subtract(Vector3f v1, Vector3f v2) {
		return new Vector3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static Vector3f Product(Vector3f v, float value) {
		return new Vector3f(v.x * value, v.y * value, v.z * value);
	}

	public static Vector3f Divide(Vector3f v, float value) {
		return new Vector3f(v.x / value, v.y / value, v.z / value);
	}
}