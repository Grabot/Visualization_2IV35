/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import util.RendererChangeListener;
import util.TFChangeListener;

/**
 *
 * @author michel
 */
public abstract class Renderer {
     int winWidth, winHeight;
    boolean visible = false;
    boolean interactiveMode = false;
    ArrayList<TFChangeListener> listeners = new ArrayList<TFChangeListener>();
    ArrayList<RendererChangeListener> rendererChangeListeners = new ArrayList<RendererChangeListener>();
    
    public Renderer() {
        
    }

    public void setInteractiveMode(boolean flag) {
        interactiveMode = flag;
    }
    
    public void setWinWidth(int w) {
        winWidth = w;
    }

    public void setWinHeight(int h) {
        winHeight = h;
    }

    public int getWinWidth() {
        return winWidth;
    }

    public int getWinHeight() {
        return winHeight;
    }

    public void setVisible(boolean flag) {
        visible = flag;
    }

    public boolean getVisible() {
        return visible;
    }

    public void addTFChangeListener(TFChangeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }
    
    public void addRendererChangeListener(RendererChangeListener listener)
    {
        if (!rendererChangeListeners.contains(listener))
        {
            rendererChangeListeners.add(listener);
        }
    }
    
    protected void RenderingCompleted()
    {
        for (RendererChangeListener listener : rendererChangeListeners)
            listener.OnRenderingCompleted(this);
    }
    
    public abstract void visualize(GL2 gl);
}
