/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/WorkplaceBorder.java $
 * $Id: WorkplaceBorder.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import javax.swing.border.BevelBorder;

/**
 * draw workplace border and background
 * @author dsaiko
 *
 */
public class WorkplaceBorder extends BevelBorder {


	private static final long serialVersionUID = 1L;
	LoadOrganizer loadOrganizer;
	Preferences preferences;



	WorkplaceBorder(LoadOrganizer loadOrganizer) {
			super(BevelBorder.LOWERED);
			this.loadOrganizer=loadOrganizer;
			preferences=loadOrganizer.preferences;
	}
	


    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    @Override
	public Insets getBorderInsets(Component c)       {
    		return new Insets(5, 5, 5, 5);
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    @Override
	public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 12;
        return insets;
    }


    
    @Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		paintBorder(Color.decode("#c1c1c1"), g, width, height,true); //$NON-NLS-1$
	}


    /**
     * draw lines inside the workplace
     * @param color
     * @param g
     * @param width
     * @param height
     * @param offset
     */
	public void paintBorder(Color color, Graphics g, int width, int height, boolean offset)  {
        Color oldColor = g.getColor();

        //g.translate(x, y);

        g.setColor(color);
       
        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke=g2.getStroke();
        
        Stroke stroke = new BasicStroke(0.5f, // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] {
        				2.0f,
        				4.0f,
        			}, // Dash pattern
                0.0f);                     // Dash phase        
        
        g2.setStroke(stroke);
        
        for(int i=preferences.getGuiPixelsPerMeter()/(offset ? 2 : 1); i<width; i+=preferences.getGuiPixelsPerMeter()) {
            g2.drawLine(i,0,i,height);
        }
       
        for(int i=preferences.getGuiPixelsPerMeter()/(offset ? 2 : 1); i<height; i+=preferences.getGuiPixelsPerMeter()) {
            g2.drawLine(0,i,width,i);
        }
        
        g.setColor(Color.decode("#d1d1d1")); //$NON-NLS-1$
        
        for(int i=preferences.getGuiPixelsPerMeter()/(offset ? 1 : 2); i<width; i+=preferences.getGuiPixelsPerMeter()) {
            g2.drawLine(i,0,i,height);
            g2.drawLine(i,0,i,height);
        }
       
        
        for(int i=preferences.getGuiPixelsPerMeter()/(offset ? 1 : 2); i<height; i+=preferences.getGuiPixelsPerMeter()) {
           g2.drawLine(0,i,width,i);
           g2.drawLine(0,i,width,i);
        }
        
        g2.setStroke(oldStroke);
        g2.setColor(oldColor);
        
    }	
}
