/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/TruckPanel.java $
 * $Id: TruckPanel.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

/**
 * truck panel component with some custom drawing
 * @author dsaiko
 *
 */
public class TruckPanel extends JLabel {
	
	private static final long serialVersionUID = 1L;
	

	public TruckPanel() {
		super();
		setOpaque(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int width=getWidth();
		int height=getHeight();
		int slope=150;
		int spacing=10;
		
		Color oldColor=g.getColor();
		g.setColor(Color.GRAY);
		
		for(int i=-slope; i<width+slope; i+=spacing) {
			g.drawLine(i,0,i-slope,height);
			g.drawLine(i-slope,0,i,height);
		}
		g.setColor(Color.BLACK);
		g.drawRect(0,0,width-1,height-1);
		g.setColor(oldColor);		
	}
	
}
