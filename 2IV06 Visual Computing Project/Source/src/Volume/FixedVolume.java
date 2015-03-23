package Volume;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import engineTester.Node;

public class FixedVolume extends Volume {

	private int width = 32;
	private int height = 32;
	private int depth = 32;

	private Node[] nodes = new Node[width * height * depth];

	public FixedVolume() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					Vector3f position = new Vector3f(x * spacing, y * spacing, z * spacing);
					nodes[getKey(position)] = new Node(position);
				}
			}
		}
	}

	public int getKey(Vector3f position) {
		int x = (int) Math.floor(position.x / spacing);
		int y = (int) Math.floor(position.y / spacing);
		int z = (int) Math.floor(position.z / spacing);

		if (x < width && y < height && z < depth && x >= 0 && y >= 0 && z >= 0) {

			return x + width * (y + height * z);
		}

		return -1;
	}

	@Override
	public void Clear() {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].Velocity = new Vector3f(0, 0, 0);
			nodes[i].Weight = 0;
		}
	}

	@Override
	public List<Node> getGridCells() {
		return Arrays.asList(nodes);
	}

	@Override
	public Node getNode(float x, float y, float z) {
		int key = getKey(new Vector3f(x, y, z));

		if (key > 0) {
			return nodes[key];
		}
		
		return new Node(getRefrencePosition(new Vector3f(x, y, z)));
	}
}
