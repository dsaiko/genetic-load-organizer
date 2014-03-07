/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/engine/ContainerPosition.java $
 * $Id: ContainerPosition.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer.engine;

import java.awt.Rectangle;


/**
 * this is the positioned container definition
 * difference between this and ContainerComponent is, 
 * that ContainerComponent has much more logic which has nothing to do with the 
 * computations, and that ContainerComponent measures are in pixels, not in real units
 */
public class ContainerPosition  {

	private Container container;
	private int x;
	private int y;
	private boolean rotated;
	private Rectangle bounds;
	
	public ContainerPosition(int x, int y, boolean rotated, Container container) {
		this.container=container;
		this.x=x;
		this.y=y;
		this.rotated=rotated;
		recomputeBounds();
	}

	/** 
	 * recompute ounds according to the rotation of box
	 */
	private void recomputeBounds() {
		if(rotated) { 
			bounds = new Rectangle(x,y,container.getSize().height, container.getSize().width);
		} else {
			bounds = new Rectangle(x,y,container.getSize().width, container.getSize().height);
		}
	}
	
	public boolean isRotated() {
		return rotated;
	}


	public void setRotated(boolean rotated) {
		boolean recompute=(rotated!=this.rotated);
		this.rotated = rotated;
		if(recompute) {
			recomputeBounds();
		}
	}

	
	public int getX() {
		return x;
	}


	public void setX(int x) {
		boolean recompute=(x!=this.x);		
		this.x = x;
		if(recompute) {
			recomputeBounds();
		}
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		boolean recompute=(y!=this.y);		
		this.y = y;
		if(recompute) {
			recomputeBounds();
		}
	}
	

	public Container getContainer() {
		return container;
	}


	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * return xml representation of item
	 * @return
	 */
	public String toXml() {
		String xml=
			"  <sap>%s</sap>\n" + //$NON-NLS-1$
			"  <size1>%s</size1>\n" + //$NON-NLS-1$
			"  <size2>%s</size2>\n" + //$NON-NLS-1$
			"  <weight>%s</weight>\n" + //$NON-NLS-1$
			"  <name>%s</name>"+ //$NON-NLS-1$
			"  <x>%s</x>"+ //$NON-NLS-1$
			"  <y>%s</y>"+ //$NON-NLS-1$
			"  <rotated>%s</rotated>" //$NON-NLS-1$
			;
			
		xml=String.format(xml,
					container.getSap()==null ? "" : container.getSap(), //$NON-NLS-1$
					container.getSize()==null ? 0 : container.getSize().width,
					container.getSize()==null ? 0 : container.getSize().height,
					container.getWeight(),
					container.getName()==null ? "" : container.getName(), //$NON-NLS-1$
					x,
					y,
					rotated
			);
		return "<container>"+xml+"</container>"; //$NON-NLS-1$ //$NON-NLS-2$
	}	
		
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(this==obj) return true;
		return obj.hashCode()==this.hashCode();
	}

	@Override
	public int hashCode() {
		return toXml().hashCode();
	}	
}
