/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/DocumentFrame.java $
 * $Id: DocumentFrame.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.saiko.loadorganizer.engine.Container;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Document frame window.
 * Displays document, its areas.
 * saving and loading functionality
 * 
 * @author dsaiko
 *
 */
public class DocumentFrame extends JInternalFrame {

    
	private static final long serialVersionUID = 1L;
	
	static volatile int openFrameCount = 0;

	LoadOrganizer loadOrganizer;

	JMenuBar menuBar;
	
	MenuDocumentFile menuFile;
	MenuDocumentEdit menuEdit;

   	LibraryPanel 	panelLibrary;
   	WorkplacePanel 	panelWorkplace;
   	
   	Preferences preferences;
   	
   		
    /**
     * document preferences
     */
     Dimension documentTruckSize=new Dimension(13450,2450);
     int documentTruckTonnage=24000;
     String documentName;
     String documentAuthor;
     Date  documentDate;
     
     /**
      * Constructor off document
      * @param parentFrame
      * @param fileToLoad 
      * @throws Exception
      */
	public DocumentFrame(LoadOrganizer parentFrame, File fileToLoad) throws Exception {
		super(null, true, true, true, true);
		this.loadOrganizer = parentFrame;
		

		preferences=loadOrganizer.preferences;
		setFrameIcon(new ImageIcon(preferences.getAppImage()));

		//fills the date and author for new document
		documentAuthor=System.getenv("user.name"); //$NON-NLS-1$
		if(documentAuthor==null || documentAuthor.trim().length()==0) documentAuthor=System.getenv("USER"); //$NON-NLS-1$
		documentDate=new Date();
		
		openFrameCount++;
		documentName=Messages.getString("newDocumentTitle") + openFrameCount; //$NON-NLS-1$
		setTitle(documentName); //$NON-NLS-1$

		//load document if requested.
		//only document properties will be loaded, the objects later on
		Document loadDocument=null;
		if(fileToLoad!=null) {
			loadDocument=loadDocument(fileToLoad);
		}
		//displays properties of new document or existing
		DocumentPropertiesDialog propertieDlg=new DocumentPropertiesDialog(this,Messages.getString("DocumentFrame.3"),true,true,loadDocument!=null); //$NON-NLS-1$
		if(!propertieDlg.doModal()) {
			this.setVisible(false);
			dispose();
			throw new DocumentClosedException();
		}
		Dimension parentSize = parentFrame.getSize();
		setSize((int) (parentSize.width * preferences.getGuiNewWindowSizeRatio()),
				(int) (parentSize.height * preferences.getGuiNewWindowSizeRatio()));

		setLocation(preferences.getGuiNewWindowOffset() * openFrameCount,
				preferences.getGuiNewWindowOffset() * openFrameCount);
		
		buildMenus();
		createComponents();	
		if(loadDocument!=null) {
			loadObjects(loadDocument);
		}		
	}
	
	protected void buildMenus() {

		menuBar = new JMenuBar();
		menuBar.setOpaque(true);

		menuFile = new MenuDocumentFile(this);
		menuEdit = new MenuDocumentEdit(this);

		menuBar.add(menuFile.build());
		menuBar.add(menuEdit.build());
		setJMenuBar(menuBar);
	}
	
	
	void createComponents() {
	   	
	   	
	   	panelWorkplace = new WorkplacePanel(this);	   	
	   	panelLibrary = new LibraryPanel(this);	   	
	   	panelLibrary.setBorder(new BevelBorder(BevelBorder.LOWERED));
	   	panelWorkplace.setBorder(new BevelBorder(BevelBorder.LOWERED));
	   	JSplitPane splitter=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelLibrary,panelWorkplace);
	   	splitter.setContinuousLayout(true);
	   	splitter.setOneTouchExpandable(true);
	   	
	   	getContentPane().add(splitter);
	}

	void writeDocument(File f) throws Exception {
		StringBuffer xml=new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n"); //$NON-NLS-1$
		xml.append("<load-organized-document version=\"1.0\">\n"); //$NON-NLS-1$

		xml.append("<settings>\n"); //$NON-NLS-1$
		xml.append("<name>"+this.documentName+"</name>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<author>"+this.documentAuthor+"</author>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<date>"+new SimpleDateFormat("yyyy/MM/dd").format(this.documentDate)+"</date>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		xml.append("<truck-width>"+this.documentTruckSize.width+"</truck-width>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<truck-height>"+this.documentTruckSize.height+"</truck-height>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<truck-tonnage>"+this.documentTruckTonnage+"</truck-tonnage>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("</settings>\n"); //$NON-NLS-1$

		xml.append("<statistics>\n"); //$NON-NLS-1$

		DocumentStatistics info=panelWorkplace.computeInfo();
		
		double areaSize=(((double)documentTruckSize.width*documentTruckSize.height)/1000000.0);
		
		xml.append("<total-containers>"+info.count+"</total-containers>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<used-area>"+info.area+"</used-area>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<used-area-percentage>"+(info.area*100.0/areaSize)+"</used-area-percentage>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<total-weight>"+(info.totalWeight)+"</total-weight>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		double percentageFront=0;
		double percentageLeft=0;
		if(info.totalWeight>0) {
			percentageFront=info.weightFront*100.0/info.totalWeight;
			percentageLeft=info.weightLeft*100.0/info.totalWeight;
		}
		xml.append("<load-percentage-front>"+percentageFront+"</load-percentage-front>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("<load-percentage-left>"+percentageLeft+"</load-percentage-left>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		xml.append("</statistics>\n"); //$NON-NLS-1$

	    
		
		xml.append("<workplace>\n"); //$NON-NLS-1$
		for(ContainerComponent c:panelWorkplace.workplaceComponents) {
			xml.append(c.toXml());
		}
		xml.append("</workplace>\n");		 //$NON-NLS-1$
		xml.append("</load-organized-document>\n"); //$NON-NLS-1$
		
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(f)),"UTF-8")); //$NON-NLS-1$
		writer.write(xml.toString());
		writer.close();
	}
	
	Document loadDocument(File f) throws Exception {
		Document xmlDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new GZIPInputStream(new FileInputStream(f)));

		///settings
		NodeList nodeList=xmlDoc.getDocumentElement().getElementsByTagName("settings").item(0).getChildNodes(); //$NON-NLS-1$
		for(int i=0; i<nodeList.getLength(); i++) {
			Node e=nodeList.item(i);
			if(!(e instanceof Element)) continue;
			
			String name=e.getNodeName();
			String value=e.getTextContent();
			
			loadSettings(name,value);
		}	
		
		return xmlDoc;
	}
	
	void loadSettings(String name, String value) throws Exception {
		if(name==null) return;
		
		if(name.equals("name")) { //$NON-NLS-1$
			documentName=value;
		}
		if(name.equals("author")) { //$NON-NLS-1$
			documentAuthor=value;
		}
		if(name.equals("date")) { //$NON-NLS-1$
			documentDate=new SimpleDateFormat("yyyy/MM/dd").parse(value); //$NON-NLS-1$
		}
		if(name.equals("truck-width")) { //$NON-NLS-1$
			documentTruckSize.width=new Integer(value);
		}
		if(name.equals("truck-height")) { //$NON-NLS-1$
			documentTruckSize.height=new Integer(value);
		}
		if(name.equals("truck-tonnage")) { //$NON-NLS-1$
			documentTruckTonnage=new Integer(value);
		}
	}
	
	/**
	 * the function places object from saved file to the workplace
	 * if there is no item in lirary, than it is created
	 * and the lirary panels are reconstructed
	 * @param xmlDoc
	 * @throws Exception
	 */
	void loadObjects(Document xmlDoc) throws Exception {
		Rectangle truck=panelWorkplace.truck.getBounds();

		
		panelLibrary.destroyLiraryTable();

		boolean libraryModified=false;
		///first add containers to library, if missing
		NodeList nodeList=xmlDoc.getElementsByTagName("container"); //$NON-NLS-1$
		for(int i=0; i<nodeList.getLength(); i++) {
			Element e=(Element)nodeList.item(i);
			String size1=((Element)e.getElementsByTagName("size1").item(0)).getTextContent(); //$NON-NLS-1$
			String size2=((Element)e.getElementsByTagName("size2").item(0)).getTextContent(); //$NON-NLS-1$
			String name=((Element)e.getElementsByTagName("name").item(0)).getTextContent(); //$NON-NLS-1$
			String sap=((Element)e.getElementsByTagName("sap").item(0)).getTextContent(); //$NON-NLS-1$
			String weight=((Element)e.getElementsByTagName("weight").item(0)).getTextContent(); //$NON-NLS-1$

			String rotated=((Element)e.getElementsByTagName("rotated").item(0)).getTextContent(); //$NON-NLS-1$
			String mandatory=((Element)e.getElementsByTagName("mandatory").item(0)).getTextContent(); //$NON-NLS-1$
			String x=((Element)e.getElementsByTagName("x").item(0)).getTextContent(); //$NON-NLS-1$
			String y=((Element)e.getElementsByTagName("y").item(0)).getTextContent(); //$NON-NLS-1$

			Container c1=new Container(
					name,
					sap,
					new Dimension(Integer.parseInt(size1),Integer.parseInt(size2)),
					Integer.parseInt(weight)
			);

			ContainerComponent component=null;
			for(Container c2:Library.getContainers()) {
				if(c2.toXml().equals(c1.toXml())) {
					component=panelWorkplace.addContainer(c2,true);
					break;
				}
			} 
			if(component==null) {
				libraryModified=true;
				Library.getContainers().add(c1);
				component=panelWorkplace.addContainer(c1,true);
			}
			if(new Boolean(rotated).booleanValue()) {
				component.rotate();
			}
			if(new Boolean(mandatory).booleanValue()) {
				component.mandatory=true;
			}
			//place component to original position
			double xPos=new Double(x).doubleValue();
			double yPos=new Double(y).doubleValue();
			
			//recompute the position to pixels, position is relative to truc origin
			xPos=xPos*preferences.getGuiPixelsPerMeter()/1000;
			yPos=yPos*preferences.getGuiPixelsPerMeter()/1000;
			
			xPos+=truck.x;
			yPos+=truck.y;
			component.setLocation((int) xPos, (int)yPos);
		}
		panelLibrary.createLibraryTable();

		if(libraryModified) {
			for(DocumentFrame doc:loadOrganizer.openDocuments) {
				doc.panelLibrary.destroyLiraryTable();
				doc.panelLibrary.createLibraryTable();
			}
		}
		panelWorkplace.computeInfo();
		panelWorkplace.getVerticalScrollBar().setValue(0);
		panelWorkplace.getHorizontalScrollBar().setValue(0);
	}	
}
