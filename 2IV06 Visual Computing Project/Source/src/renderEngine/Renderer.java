package renderEngine;

import java.util.List;
import java.util.Map;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shaders.StaticShader;
import toolbox.Maths;
import engineTester.Hair;
import entities.Entity;

public class Renderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private Matrix4f projectionMatrix;
	private StaticShader shader;
	
	public Renderer(StaticShader shader) {
		this.shader = shader;

		// Do not draw vertices away from view
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void render(RawModel model) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(
				0, 0, 0), new Vector3f(0, 0, 0), 1);
		shader.loadTransformationMatrix(transformationMatrix);

		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	public void render(TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	public void render(Entity entity, StaticShader shader) {
		TexturedModel model = entity.getModel();
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void renderGUI()
	{
		//Generally a good idea to enable texturing first

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture );
		
		GL11.glBegin( GL11.GL_QUADS );
		GL11.glTexCoord2d(0.0,0.0); 
		GL11.glVertex2d(-20.0,-20.0);
		GL11.glTexCoord2d(1.0,0.0); 
		GL11.glVertex2d(20.0,-20.0);
		GL11.glTexCoord2d(1.0,1.0); 
		GL11.glVertex2d(20.0,20.0);
		GL11.glTexCoord2d(0.0,1.0); 
		GL11.glVertex2d(-20.0,20.0);
		GL11.glEnd();
		
		/*
		GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(-139.0, -78.0);			// bottom left
		GL11.glVertex2d(139.0, -78.0);		// bottom-right
		GL11.glVertex2d(139.0, 78.0);		// top-right
		GL11.glVertex2d(-139.0, 78.0);		// top-left
		GL11.glEnd();
		*/
		
		int x = 100;
		int y = 60;
		GL11.glBegin(GL11.GL_POINTS);
		//f
		for (int i = 0; i <= 8; i++) {
			GL11.glVertex2f(x + 1, y + i);
		}
		for (int i = 1; i <= 6; i++) {
            GL11.glVertex2f(x + i, y + 8);
		}
		for (int i = 2; i <= 5; i++) {
            GL11.glVertex2f(x + i, y + 4);
		}
		x+=8;
		//p
		for (int i = 0; i <= 8; i++) {
			GL11.glVertex2f(x + 1, y + i);
		}
		for (int i = 2; i <= 5; i++) {
			GL11.glVertex2f(x + i, y + 8);
			GL11.glVertex2f(x + i, y + 4);
		}
		GL11.glVertex2f(x + 6, y + 7);
		GL11.glVertex2f(x + 6, y + 5);
		GL11.glVertex2f(x + 6, y + 6);
		x += 8;
		//s
		for (int i = 2; i <= 7; i++) {
			GL11.glVertex2f(x + i, y + 8);
		}
		GL11.glVertex2f(x + 1, y + 7);
		GL11.glVertex2f(x + 1, y + 6);
		GL11.glVertex2f(x + 1, y + 5);
		for (int i = 2; i <= 6; i++) {
			GL11.glVertex2f(x + i, y + 4);
			GL11.glVertex2f(x + i, y);
		}
		GL11.glVertex2f(x + 7, y + 3);
		GL11.glVertex2f(x + 7, y + 2);
		GL11.glVertex2f(x + 7, y + 1);
		GL11.glVertex2f(x + 1, y + 1);
		GL11.glVertex2f(x + 1, y + 2);
		x -= 12;
		y -= 12;
		for (int i = 1; i <= 7; i++) {
			GL11.glVertex2f(x + 7, y + i);
		}
		for (int i = 5; i <= 7; i++) {
			GL11.glVertex2f(x + 1, y + i);
		}
		for (int i = 2; i <= 6; i++) {
			GL11.glVertex2f(x + i, y + 8);
			GL11.glVertex2f(x + i, y + 0);
		}
		for (int i = 2; i <= 6; i++) {
			GL11.glVertex2f(x + i, y + 4);
		}
		GL11.glVertex2f(x + 1, y + 0);
		x += 8;
		for (int i = 1; i <= 7; i++) {
			GL11.glVertex2f(x + 1, y + i);
			GL11.glVertex2f(x + 7, y + i);
		}
		for (int i = 2; i <= 6; i++) {
			GL11.glVertex2f(x + i, y + 8);
			GL11.glVertex2f(x + i, y + 0);
		}
		for (int i = 2; i <= 6; i++) {
			GL11.glVertex2f(x + i, y + 4);
		}
           
        GL11.glEnd();
        
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				if (entity.getWireFrame()) {
					GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				} else {
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}				
			}
			unbindTexturedModel();
		}
	}

	public void render(List<Hair> hairs) {
		for (Hair hair : hairs) {
			RawModel rawModel = hair.getRawModel();
			GL30.glBindVertexArray(rawModel.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0,0,0), new Vector3f(
					0, 0, 0), 1);
			shader.loadTransformationMatrix(transformationMatrix);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, hair.getParticles().get(0).getModel().getTexture().getID());
			GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			GL20.glDisableVertexAttribArray(0);
			// GL20.glDisableVertexAttribArray(1);
			// GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	

	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
}
