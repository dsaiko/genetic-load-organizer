/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/ContainerComponent.java $
 * $Id: ContainerComponent.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */
package org.saiko.loadorganizer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.saiko.loadorganizer.engine.Container;

/**
 * graphical representation of the container
 */
public class ContainerComponent extends JLabel {
	

	private static final long serialVersionUID = 1243155480295784772L;
	
	Container container;
	WorkplacePanel workplacePanel;
	Point lastClick;
	DocumentFrame documentFrame;
	JLabel hiddenLabel;
	Preferences preferences;

	/**
	 * container preferences
	 */
	boolean selected=false;
	boolean rotated=false;
	boolean mandatory=false;

	/**
	 * rotate container, rotate size
	 */
	void rotate() {
		rotated=!rotated;
		int w=getWidth();
		int h=getHeight();
		setSize(h,w);
		hiddenLabel.setSize(w,h);
		hiddenLabel.repaint();
		repaint();
	}
	
	public ContainerComponent(WorkplacePanel parent, Container container) {
		this.container=container;
		this.workplacePanel=parent;
		preferences=workplacePanel.documentFrame.loadOrganizer.preferences;
		
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		Dimension boxSize=container.getSize();
		
		Dimension size=new Dimension(boxSize.width*preferences.getGuiPixelsPerMeter()/1000,boxSize.height*preferences.getGuiPixelsPerMeter()/1000);
		setSize(size);
		setPreferredSize(new Dimension(size));
		
		setOpaque(true);

		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		

		final ContainerComponent component=this;
		
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Point point=e.getPoint();
				Point location=getLocation();
				int x=location.x+point.x-lastClick.x;
				int y=location.y+point.y-lastClick.y;
				//check borders of other components
				workplacePanel.moveSelectedComponents(component, x, y);				
				workplacePanel.checkScrollBounds();
			}
		});
		addMouseListener(new MouseAdapter() {
		    @Override
			public void mousePressed(MouseEvent e) {
		    		workplacePanel.workplace.setComponentZOrder(component,0);
		    		if(selected==false) {
			    		if(e.isControlDown() || e.isShiftDown()) {
			    			workplacePanel.switchSelected(component,false,true);
			    		} else {
			    			workplacePanel.switchSelected(component,true,true);
			    		}
		    		}
		    		lastClick=e.getPoint();
		    }
		    @Override
			public void mouseReleased(MouseEvent e) {
	    			workplacePanel.computeInfo();
		    }		    
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int offset=1;
				Rectangle r=getBounds();
				boolean recompute=false;
				
				if(e.getKeyCode()==KeyEvent.VK_UP) {
					workplacePanel.moveSelectedComponents(component,r.x,r.y-offset);
					recompute=true;
				} else
				if(e.getKeyCode()==KeyEvent.VK_DOWN) {
					workplacePanel.moveSelectedComponents(component,r.x,r.y+offset);
					recompute=true;
				} else
				if(e.getKeyCode()==KeyEvent.VK_LEFT) {
					workplacePanel.moveSelectedComponents(component,r.x-offset,r.y);
					recompute=true;
				} else
				if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
					workplacePanel.moveSelectedComponents(component,r.x+offset,r.y);
					recompute=true;
				} else						
				if(e.getKeyCode()==KeyEvent.VK_DELETE) {
						workplacePanel.deleteSelected();
				}
				if(recompute) {
					workplacePanel.computeInfo();
				}
			}
		});
		
		setText("<html><table align=center valign=center><tr><td align=center><b>"+container.getName()+"</b><br><font size=1>"+container.getSap()+" "+container.getSize().width+"&nbsp;x&nbsp;"+container.getSize().height+" "+container.getWeight()+"kg</font></td></tr></table></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		setFont(new Font("verdana",Font.PLAIN,9)); //$NON-NLS-1$
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
		addPopupMenu();
		
		hiddenLabel=	new JLabel();
		hiddenLabel.updateUI();
		hiddenLabel.setBackground(this.getBackground());
		hiddenLabel.setSize(getHeight(), getWidth());
		hiddenLabel.setFont(this.getFont());
		hiddenLabel.setBorder(this.getBorder());
		hiddenLabel.setOpaque(true);
		hiddenLabel.setHorizontalAlignment(this.getHorizontalAlignment());
		hiddenLabel.setVerticalAlignment(this.getVerticalAlignment());
		hiddenLabel.setVerticalTextPosition(this.getVerticalTextPosition());
		hiddenLabel.setHorizontalAlignment(this.getHorizontalTextPosition());
		hiddenLabel.setPreferredSize(this.getPreferredSize());
		hiddenLabel.setText(this.getText());
	}
	
	void setSelected(boolean selected) {
		this.selected=selected;
		repaint();
	}

	boolean getSelected() {
		return selected;		
	}
	
	@Override
	/**
	 * paint component and highlite selected and mandatory components
	 */
	public void paint(Graphics g) {
		setBackground(Color.decode(preferences.getGuiBoxColor()));
		setForeground(Color.decode(preferences.getGuiBoxLabelColor()));

		int width=getWidth();
		int height=getHeight();
		
		if(width<preferences.getGuiPixelsPerMeter() && (width<height/2)) {
			paintHere(g,0,0,width,height);
		} else {			
			super.paint(g);
		}
		if(rotated) {
			int w=preferences.getRotationImage().getWidth(null);
			int h=preferences.getRotationImage().getHeight(null);
			g.drawImage(preferences.getRotationImage(),width-w-3,height-h-3,null);
		}
		if(mandatory) {
			g.drawImage(preferences.getMandatoryImage(),3,3,null);
		}
		if(selected) {
			g.fillRect(1,1,5,5);
			g.fillRect(1,height-6,5,5);
			g.fillRect(width-6,1,5,5);
			g.fillRect(width-6,height-6,5,5);
		}
	}
	
	/**
	 * paints rotated component in the way it paints into hidden component,
	 * and then rotates the image
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void paintHere(Graphics g, int x, int y, int width, int height)
	{
		if (height <= 0 || width <= 0) return;
 
		try {			
			hiddenLabel.setSize(getHeight(), getWidth());
			hiddenLabel.setBackground(Color.decode(preferences.getGuiBoxColor()));
			hiddenLabel.setForeground(Color.decode(preferences.getGuiBoxLabelColor()));

			// Paint the JLabel into an image buffer...
			BufferedImage buffer = new BufferedImage(hiddenLabel.getWidth(), hiddenLabel.getHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = buffer.createGraphics();
			hiddenLabel.paint(g2);
			
			// ...then apply a transform while painting the buffer into the component
			AffineTransform af = AffineTransform.getTranslateInstance(x, y + height);
			AffineTransform af2 = AffineTransform.getRotateInstance(-Math.toRadians(90.0));
			af.concatenate(af2);
	 
			((Graphics2D)g).drawImage(buffer, af, this);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	void addPopupMenu() {
		final JPopupMenu popup=workplacePanel.popup;

		this.addMouseListener(new MouseAdapter() {
	        @Override
			public void mousePressed(MouseEvent evt) {
	            if (evt.isPopupTrigger()) {
	            		popup.show(evt.getComponent(), evt.getX(), evt.getY());
	            }
	        }
	        @Override
			public void mouseReleased(MouseEvent evt) {
	            if (evt.isPopupTrigger()) {
	            		popup.show(evt.getComponent(), evt.getX(), evt.getY());
	            }
	        }
	    });
	}
	
	
	public String toXml() {

		String xml=
			"  <rotated>%s</rotated>\n" + //$NON-NLS-1$
			"  <mandatory>%s</mandatory>\n" + //$NON-NLS-1$
			"  <x>%s</x>\n" + //$NON-NLS-1$
			"  <y>%s</y>\n"  //$NON-NLS-1$
			;

		Rectangle r=getBounds();
		Rectangle truck=workplacePanel.truck.getBounds();
		double x=(r.x-truck.x)*1000/preferences.getGuiPixelsPerMeter();
		double y=(r.y-truck.y)*1000/preferences.getGuiPixelsPerMeter();
		xml=String.format(xml,
					rotated,
					mandatory,
					x,
					y
			);

		xml=container.xmlBody() + "\n" +xml; //$NON-NLS-1$
			
		return "<container>\n"+xml+"</container>\n"; //$NON-NLS-1$ //$NON-NLS-2$
	}	
}

