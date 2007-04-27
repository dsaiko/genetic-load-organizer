/**
 * $LastChangedDate: 2006-06-30 11:25:21 +0200 (pá, 30 VI 2006) $
 * $LastChangedRevision: 19 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/Library.java $
 * $Id: Library.java 19 2006-06-30 09:25:21Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.saiko.loadorganizer.engine.Container;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Library functionality
 * loads file "library.xml" from the classpath
 * @author dsaiko
 *
 */
public class Library {
	
	static List<Container> containers;

	/**
	 * get the containers in library, 
	 * loads library firs, if necessary
	 * @return
	 */
	public static synchronized List<Container> getContainers() {
		if(containers==null) {
			load();
		}
		
		return containers;
	}
	
	/**
	 * load the library from file
	 */
	private static void load() {
		if(containers!=null) {
			containers.clear();
		}
		containers=new ArrayList<Container>();

		try {

			Document xmlDoc;
			
			try {
				xmlDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("library.xml")); //$NON-NLS-1$
			} catch(java.io.FileNotFoundException e) {
				xmlDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Library.class.getClassLoader().getResourceAsStream("org/saiko/loadorganizer/library.xml")); //$NON-NLS-1$
			}
			
			NodeList nodeList=xmlDoc.getElementsByTagName("container"); //$NON-NLS-1$
			for(int i=0; i<nodeList.getLength(); i++) {
				Element e=(Element)nodeList.item(i);
				String size1=((Element)e.getElementsByTagName("size1").item(0)).getTextContent(); //$NON-NLS-1$
				String size2=((Element)e.getElementsByTagName("size2").item(0)).getTextContent(); //$NON-NLS-1$
				String name=((Element)e.getElementsByTagName("name").item(0)).getTextContent(); //$NON-NLS-1$
				String sap=((Element)e.getElementsByTagName("sap").item(0)).getTextContent(); //$NON-NLS-1$
				String weight=((Element)e.getElementsByTagName("weight").item(0)).getTextContent(); //$NON-NLS-1$
				
				containers.add(new Container(
						name,
						sap,
						new Dimension(Integer.parseInt(size1),Integer.parseInt(size2)),
						Integer.parseInt(weight)
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	

	
}
