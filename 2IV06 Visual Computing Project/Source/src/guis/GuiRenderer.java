package guis;

import java.awt.Font;
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
	
	public GuiRenderer(Loader loader) {

		font = new Font("Verdana", Font.BOLD, 20);
	    ttf = new TrueTypeFont(font, true);
	    
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVao(positions);
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis) {

		shader.start();
		getInput();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		for( int i = 0; i < guis.size(); i++ )
		{
			
			if( i <= 3 )
			{
				/*
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
			*/
			}
			else if( i >= 9 && i <= 11 )
			{
				//words
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i).getTexture());
				Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i).getPosition(), guis.get(i).getScale());
				shader.loadTransformation(matrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
			else if( i >= 12 )
			{
				//numbers
				//System.out.println("i: " + i );
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, guis.get(i).getTexture());
				Matrix4f matrix = Maths.createTransformationMatrix(guis.get(i).getPosition(), guis.get(i).getScale());
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
