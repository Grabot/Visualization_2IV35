package guis;

import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.TrueTypeFont;

import renderEngine.Loader;
import toolbox.Maths;
import engineTester.Hair;

public class GuiRenderer {

	public final RawModel quad;
	private GuiShader shader;
	
	private Font font;
	private TrueTypeFont ttf;
	
	private int x;
	private int y;

	private int realX = -90;
	private int realY = -90;
	
	private boolean leftButtonDown;
	private boolean rightButtonDown;
	
	private boolean button1pressed = false;
	private boolean button2pressed = false;
	private boolean button3pressed = false;
	private boolean button4pressed = false;
	
	private ArrayList<Hair> hairs = new ArrayList<Hair>();
	private int[] hairArray = new int[6];
	private int[] particleArray = new int[6];
	private int[] fpsArray = new int[6];
	
	private String fpsValue = "60,0";
	
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
		font = new Font("Verdana", Font.BOLD, 20);
	    ttf = new TrueTypeFont(font, true);
	    
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
		for( int i = 0; i < 4; i++ )
		{
			if( i <= 3 )
			{
				//buttons
				if(( realX >= 0 && realX <= 255 ) && (realY >= (645 - (75 * i )) && realY <= (720 - (75 * i ))) )
				{					
					button1pressed = true;
					System.out.println("button " + i + " pressed");
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i).getTexture());
					Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i).getPosition(), guis.get(i).getScale());
					shader.loadTransformation(matrix);
					GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
				}
				else
				{
					button1pressed = false;
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i+4).getTexture());
					Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i+4).getPosition(), guis.get(i+4).getScale());
					shader.loadTransformation(matrix);
					GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
				}
			}
		}
		
		for( int i = 9; i < 13; i++ )
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
			System.out.println("x: " + realX );
			System.out.println("y: " + realY );
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
