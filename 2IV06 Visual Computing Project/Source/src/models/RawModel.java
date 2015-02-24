package models;

public class RawModel {

	private int vaoID;
	private int vboID;
	private int vertexCount;
	
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public void setPositionsVboID(int value) {
		vboID = value;
	}
	
	public int getPositionsVboID() {
		return vboID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
}
