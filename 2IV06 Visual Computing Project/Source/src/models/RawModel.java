package models;

public class RawModel {

	private int vaoID;
	private int vertexCount;
	
	private float[] normals;
	
	public RawModel(int vaoID, int vertexCount, float[] normals) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.normals = normals;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float[] getNormals() {
		return normals;
	}
	
	
}
