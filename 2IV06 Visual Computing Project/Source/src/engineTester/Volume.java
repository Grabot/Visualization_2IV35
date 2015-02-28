package engineTester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import toolbox.VectorMath;

public class Volume {
	private float cellSize = 10;
	private Map<Vector3f, Cell> cells = new HashMap<Vector3f, Cell>();

	public Volume(float cellSize) {
		this.cellSize = cellSize;
	}

	public void Clear() {
		cells.clear();
	}

	private Cell getCell(Vector3f key) {
		// If cell doesn't exist create one
		if (!cells.containsKey(key)) {
			Cell cell = new Cell();
			cells.put(key, cell);
			return cell;
		}

		return (Cell) cells.get(key);
	}
	
	private Vector3f getKey(Vector3f position) {
		Vector3f vec = new Vector3f();
		vec.x = (float) Math.floor(position.x / cellSize);
		vec.y = (float) Math.floor(position.y / cellSize);
		vec.z = (float) Math.floor(position.z / cellSize);
		return vec;
	}

	public void addVoxelWeight(Vector3f position, float weight) {
		Vector3f coord = getKey(position);
		
		getCell(VectorMath.Sum(coord, new Vector3f(0,0,0))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(1,0,0))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(0,1,0))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(1,1,0))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(0,0,1))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(1,0,1))).Weight += weight;
		getCell(VectorMath.Sum(coord, new Vector3f(1,1,1))).Weight += weight;
	}

	public float getVoxelWeight(Vector3f position) {
		Cell cell = getCell(position);
		return cell.Weight;
	}

	public void addVoxelVelocity(Vector3f position, Vector3f velocity){
		Vector3f loc = getKey(position);
				
		//getVoxel(VectorMath.Sum(loc, new Vector3f(1, 0, 0)));
			
		Cell cell = getCell(position);
		cell.Velocity = VectorMath.Sum(cell.Velocity, velocity);
	}

	public Vector3f getVoxelVelocity(Vector3f position) {
		Cell cell = getCell(position);
		return cell.Velocity;
	}
}
