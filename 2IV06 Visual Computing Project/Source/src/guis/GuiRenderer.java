package guis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.Loader;
import toolbox.Maths;
import engineTester.Hair;

public class GuiRenderer {

	public final RawModel quad;
	private GuiShader shader;
	
	private int x;
	private int y;

	private int realX = -90;
	private int realY = -90;
	
	private boolean leftButtonDown;
	private boolean rightButtonDown;
	
	public boolean button1pressed = false;
	public boolean button2pressed = false;
	public boolean button3pressed = false;
	public boolean button4pressed = false;
	public boolean buttonObjectGridpressed = false;
	public boolean buttonObjectHairpressed = false;
	public boolean buttonParticlespressed = false;
	
	private ArrayList<Hair> hairs = new ArrayList<Hair>();
	private int[] hairArray = new int[6];
	private int[] particleArray = new int[6];
	private int[] fpsArray = new int[6];
	
	public GuiRenderer(Loader loader, ArrayList<Hair> hairs ) 
	{
		for( int i = 0; i < fpsArray.length; i++ )
		{
			fpsArray[i] = -1;
		}
		for( int i = 0; i < hairArray.length; i++ )
		{
			hairArray[i] = -1;
		}
		for( int i = 0; i < particleArray.length; i++ )
		{
			particleArray[i] = -1;
		}
		
		this.hairs = hairs;
	    
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVao(positions);
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis) 
	{

		for( int i = 0; i < String.valueOf(Math.abs((long)hairs.size())).length(); i++ )
		{
			hairArray[i] = Character.getNumericValue(String.valueOf(Math.abs((long)hairs.size())).charAt(i));
		}
		int numberOfParticles = String.valueOf(hairs.size() * hairs.get(0).getParticles().size()).length();
		for( int i = 0; i < numberOfParticles; i++ )
		{
			particleArray[i] = Character.getNumericValue(String.valueOf(Math.abs((long)(hairs.size() * hairs.get(0).getParticles().size()))).charAt(i));
		}

		shader.start();
		getInput();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		//button 1
		if((( realX >= 90 && realX <= 166 ) && (realY >= 396 && realY <= 468) ) || Keyboard.isKeyDown(Keyboard.KEY_UP))
		{					
			button1pressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(0).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(0).getPosition(), guis.get(0).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			button1pressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(4).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(4).getPosition(), guis.get(4).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		//button 2
		if((( realX >= 26 && realX <= 102 ) && (realY >= 324 && realY <= 396) )|| Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{					
			button2pressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(1).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(1).getPosition(), guis.get(1).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			button2pressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(5).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(5).getPosition(), guis.get(5).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		//button 3
		if((( realX >= 154 && realX <= 230 ) && (realY >= 324 && realY <= 396) )|| Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{					
			button3pressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(2).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(2).getPosition(), guis.get(2).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			button3pressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(6).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(6).getPosition(), guis.get(6).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		//button 4
		if((( realX >= 90 && realX <= 166 ) && (realY >= 255 && realY <= 324)) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{					
			button4pressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(3).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(3).getPosition(), guis.get(3).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			button4pressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(7).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(7).getPosition(), guis.get(7).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		for( int i = 8; i < 13; i++ )
		{
			//words
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i).getPosition(), guis.get(i).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		for( int j = 0; j < 6; j++ )
		{
			//j_th character of the hair number sequence
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			if( !(hairArray[j] == -1) )
			{
				int k = (13 + (10*j));
				k = (k + hairArray[j] );
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(k).getTexture());
				Matrix4f matrix = Maths.createTransformationMatrix(guis.get(k).getPosition(), guis.get(k).getScale());
				shader.loadTransformation(matrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
			
			//j_th character of the particle number sequence
			if( !(particleArray[j] == -1) )
			{
				int k = (63 + (10*j));
				k = (k + particleArray[j]);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(k).getTexture());
				Matrix4f matrix = Maths.createTransformationMatrix(guis.get(k).getPosition(), guis.get(k).getScale());
				shader.loadTransformation(matrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}

			//j_th character of the fps number sequence
			if( !(fpsArray[j] == -1) )
			{
				int k = (123 + (10*j));
				k = (k + fpsArray[j]);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(k).getTexture());
				Matrix4f matrix = Maths.createTransformationMatrix(guis.get(k).getPosition(), guis.get(k).getScale());
				shader.loadTransformation(matrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
		}
		
		//apply forces text
		for( int i = 153; i < 157; i++ )
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i).getPosition(), guis.get(i).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}

		//button Object Grid
		if((( realX >= 218 && realX <= 294 ) && (realY >= 180 && realY <= 252) ) || Keyboard.isKeyDown(Keyboard.KEY_B))
		{
			buttonObjectGridpressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(157).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(157).getPosition(), guis.get(157).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			buttonObjectGridpressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(160).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(160).getPosition(), guis.get(160).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		//button Hair Grid
		if((( realX >= 218 && realX <= 294 ) && (realY >= 100 && realY <= 172) ) || Keyboard.isKeyDown(Keyboard.KEY_N))
		{
			buttonObjectHairpressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(158).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(158).getPosition(), guis.get(158).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			buttonObjectHairpressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(161).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(161).getPosition(), guis.get(161).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		//button Particles
		if((( realX >= 218 && realX <= 294 ) && (realY >= 20 && realY <= 92) ) || Keyboard.isKeyDown(Keyboard.KEY_M))
		{
			buttonParticlespressed = true;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(159).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(159).getPosition(), guis.get(159).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		else
		{
			buttonParticlespressed = false;
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(162).getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guis.get(162).getPosition(), guis.get(162).getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		//System.out.println("size: " + guis.size() );
		
		/*
		for(GuiTexture gui : guis){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		*/
		
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
	}
	
	public void setFPS( float fps )
	{

		for( int i = 0; i < fpsArray.length; i++ )
		{
			fpsArray[i] = -1;
		}
		DecimalFormat df = new DecimalFormat("00.0");
		String output = df.format(fps);
		fpsArray[0] = Character.getNumericValue(output.charAt(0));
		fpsArray[1] = Character.getNumericValue(output.charAt(1));
		fpsArray[2] = Character.getNumericValue(output.charAt(3));
	}
	private void getInput()
	{
		leftButtonDown = Mouse.isButtonDown(0); // is left mouse button down.
		rightButtonDown = Mouse.isButtonDown(1); // is right mouse button down.
		
		x = Mouse.getX(); // will return the X coordinate on the Display.
		y = Mouse.getY(); // will return the Y coordinate on the Display.
		
		if( leftButtonDown )
		{
			realX = x;
			realY = y;
		}
		else
		{
			realX = -90;
			realY = -90;
		}
	}
	
	public void Dispose() {
		shader.Dispose();
	}
	
}
