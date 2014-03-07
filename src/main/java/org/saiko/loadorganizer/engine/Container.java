/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/engine/Container.java $
 * $Id: Container.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer.engine;

import java.awt.Dimension;

/**
 * Container class
 * represents the library definition of the container without any positioning information
 */
public class Container implements Cloneable {
	private String 		sap; 	//SAP id
	private Dimension 	size;	//size in mm
	private int 			weight;	//weight in kg
	private String		name;	//name
	
	
	public Container(String name, String sap, Dimension size, int weight) {
		this.name = name;
		this.sap = sap;
		this.size = size;
		this.weight = weight;
	}

	public String xmlBody() {
		String xml=
			"  <sap>%s</sap>\n" + //$NON-NLS-1$
			"  <size1>%s</size1>\n" + //$NON-NLS-1$
			"  <size2>%s</size2>\n" + //$NON-NLS-1$
			"  <weight>%s</weight>\n" + //$NON-NLS-1$
			"  <name>%s</name>" //$NON-NLS-1$
			;
			
			return String.format(xml,
					sap==null ? "" : sap, //$NON-NLS-1$
					size==null ? 0 : size.width,
					size==null ? 0 : size.height,
					weight,
					name==null ? "" : name //$NON-NLS-1$
			);
	}
	
	public String toXml() {
		return "<container>\n"+xmlBody()+"\n</container>\n"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public String toString() {
		return toXml();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSap() {
		return sap;
	}

	public void setSap(String sap) {
		this.sap = sap;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Container(name, sap, size, weight);
	}
	
}
