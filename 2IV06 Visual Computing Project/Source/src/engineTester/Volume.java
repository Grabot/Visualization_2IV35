package engineTester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

public class Volume {
	private float cellSize = 10;
	private Vector3f[][][] velocityGrid;
	private Vector3f[][][] densityGrid;
		
	Map<Vector3f, Cell> cells = new HashMap<Vector3f, Cell>();
	
	public Volume(float cellSize)
	{
		 this.cellSize = cellSize;
	}
	
	private Cell getCell(Vector3f position) {
		
		Vector3f key = getKey(position);
		
		// If cell doesn't exist create one
		if (!cells.containsKey(getKey(position)))
		{
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
	
	public void addVoxelWeight(Vector3f position, float weight){
		Cell cell = getCell(position);
		cell.Weight += weight;	
	}
	
	public float getVoxelWeight(Vector3f position){
		
		return 0.0f;
	}
	
}
