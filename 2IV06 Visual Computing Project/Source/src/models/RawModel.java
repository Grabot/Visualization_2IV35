package models;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class RawModel {

	private int vaoID;
	private int vboID;
	private int normalvboID;
	private int vertexCount;
	private ArrayList<Vector3f> vertices;
	
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public RawModel(int vaoID, int vertexCount, ArrayList<Vector3f> vertices) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vertices = vertices;
	}
	
	public ArrayList<Vector3f> getVertices() {
		return vertices;
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
	
	public void setNormalsVboID(int value) {
		normalvboID = value;
	}
	
	public int getNormalsVboID() {
		return normalvboID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
}
