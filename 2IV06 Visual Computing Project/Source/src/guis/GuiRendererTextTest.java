package guis;

import java.awt.Font;
import java.io.InputStream;
import java.util.List;

import models.RawModel;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import renderEngine.Loader;
import toolbox.Maths;

public class GuiRendererTextTest {

	private TrueTypeFont trueTypeFont;

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

	public GuiRendererTextTest(Loader loader) {

		try {
			InputStream inputStream = ResourceLoader.getResourceAsStream("assets/OpenSans-Regular.ttf");

			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(20f); // set font size
			trueTypeFont = new TrueTypeFont(awtFont2, true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, false);

		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = loader.loadToVao(positions);
		shader = new GuiShader();
	}

	public void render(List<GuiTexture> guis) {
		shader.start();
		getInput();
		//GL11.glClearDepth(1);  
		
		// Temp for testing purposes only
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		// Temp for testing purposes only
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		

		
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					trueTypeFont.drawString(50, 50, "", Color.white);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					
					
					//GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
					//GL13.glActiveTexture(GL13.GL_TEXTURE0);
					
					Matrix4f matrix = Maths.createTransformationMatrix(guis.get(4).getPosition(), guis.get(4).getScale());
					//shader.loadTransformation(matrix);
					shader.loadTransformation(Matrix4f.setIdentity(new Matrix4f()));
					GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

					/*
		 * for(GuiTexture gui : guis){ GL13.glActiveTexture(GL13.GL_TEXTURE0);
		 * GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture()); Matrix4f
		 * matrix = Maths.createTransformationMatrix(gui.getPosition(),
		 * gui.getScale()); shader.loadTransformation(matrix);
		 * GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		 * }
		 */
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);

	}

	private void getInput() {
		leftButtonDown = Mouse.isButtonDown(0); // is left mouse button down.
		rightButtonDown = Mouse.isButtonDown(1); // is right mouse button down.

		x = Mouse.getX(); // will return the X coordinate on the Display.
		y = Mouse.getY(); // will return the Y coordinate on the Display.

		if (leftButtonDown) {
			realX = x;
			realY = y;
			System.out.println("x: " + realX);
			System.out.println("y: " + realY);
		} else {
			realX = -90;
			realY = -90;
		}
	}

	public void Dispose() {
		shader.Dispose();
	}

}
