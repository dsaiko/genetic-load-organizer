/**
 * $LastChangedDate: 2006-05-11 18:31:17 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 15 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/Preferences.java $
 * $Id: Preferences.java 15 2006-05-11 16:31:17Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * prefferences - configuration holder
 * preferences can be stored in LoadOrganizer.xml.cfg file in the class path,
 * the system tries to read them at startup, but no error is thrown
 * if the config file is missing
 * @see #save() 
 * @see #load()
 * @author dsaiko
 *
 */
public class Preferences {

	/**
	 * configuration file.
	 */
	private String configFile = "loadOrganizer.conf.xml"; //$NON-NLS-1$

	/**
	 * GUI elements
	 */
    private int guiNewWindowOffset = 30;
    private double guiNewWindowSizeRatio=3.0/4;
    private boolean guiNewWindowMaximize = true;
    private boolean guiStartupOpenNewWindow=true;
    private int guiPixelsPerMeter=75;
    private String guiBoxColor="#fafbb4"; //$NON-NLS-1$
    private String guiBoxLabelColor="#000000"; //$NON-NLS-1$

    
    private int enginePopulationSize=10;				//15
    private double enginePopulationGrowth=0;			// newPopulationSize = populationSize * (1+ enginePopulationGrowth/100)
    private int engineFinishStableCount=15;			// wehen to stop for stable results
    private int engineBoxMovementStep=150;			// movement unit for containers placing, in millimeters
    private int engineOptimizationDepth=1;			// optimization depth for optimizing optimized items
    private int engineRandomizeLoop=2;				// randomizing loop - count of cycles
    private int engineThreadCount = 4; 
      
    
    /** 
     * general settins
     */
    private DecimalFormat decimalFormatter=new DecimalFormat("###,##0.00"); //$NON-NLS-1$
    private DecimalFormat integerFormatter=new DecimalFormat("###,##0"); //$NON-NLS-1$
    private SimpleDateFormat dateFormatter=new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
    
    private String documentExtension = "lod"; //$NON-NLS-1$
    
    /**
     * preloaded images
     */
    private Image rotationImage;
    private Image mandatoryImage;
    private Image appImage;
    
    public Preferences() {
	    	try {
	    		load();
	    		
	    		rotationImage=Toolkit.getDefaultToolkit().getImage(Preferences.class.getClassLoader().getResource("rotation.gif")); //$NON-NLS-1$
	    		mandatoryImage=Toolkit.getDefaultToolkit().getImage(Preferences.class.getClassLoader().getResource("mandatory.gif")); //$NON-NLS-1$
	    		appImage=Toolkit.getDefaultToolkit().getImage(Preferences.class.getClassLoader().getResource("icon16.png")); //$NON-NLS-1$
	    		MediaTracker tracker=new MediaTracker(new JLabel());
	    		tracker.addImage(rotationImage,1);
	    		tracker.addImage(mandatoryImage,2);
	    		tracker.waitForAll();
	    	} catch(Throwable e) {
	    		e.printStackTrace();
	    		System.exit(-1);
	    	}
    }

	public DecimalFormat getDecimalFormatter() {
		return decimalFormatter;
	}

	public void setDecimalFormatter(DecimalFormat decimalFormatter) {
		this.decimalFormatter = decimalFormatter;
	}

	public int getEngineFinishStableCount() {
		return engineFinishStableCount;
	}

	public void setEngineFinishStableCount(int engineFinishStableCount) {
		this.engineFinishStableCount = engineFinishStableCount;
	}

	public int getEngineOptimizationDepth() {
		return engineOptimizationDepth;
	}

	public void setEngineOptimizationDepth(int engineOptimizationDepth) {
		this.engineOptimizationDepth = engineOptimizationDepth;
	}

	public int getEngineBoxMovementStep() {
		return engineBoxMovementStep;
	}

	public void setEngineBoxMovementStep(int enginePlacingStep1) {
		this.engineBoxMovementStep = enginePlacingStep1;
	}

	public double getEnginePopulationGrowth() {
		return enginePopulationGrowth;
	}

	public void setEnginePopulationGrowth(double enginePopulationGrowth) {
		this.enginePopulationGrowth = enginePopulationGrowth;
	}

	public int getEnginePopulationSize() {
		return enginePopulationSize;
	}

	public void setEnginePopulationSize(int enginePopulationSize) {
		this.enginePopulationSize = enginePopulationSize;
	}

	public int getEngineRandomizeLoop() {
		return engineRandomizeLoop;
	}

	public void setEngineRandomizeLoop(int engineRandomizeLoop) {
		this.engineRandomizeLoop = engineRandomizeLoop;
	}

	public String getGuiBoxColor() {
		return guiBoxColor;
	}

	public void setGuiBoxColor(String guiBoxColor) {
		this.guiBoxColor = guiBoxColor;
	}

	public boolean isGuiNewWindowMaximize() {
		return guiNewWindowMaximize;
	}

	public void setGuiNewWindowMaximize(boolean guiNewWindowMaximize) {
		this.guiNewWindowMaximize = guiNewWindowMaximize;
	}

	public int getGuiNewWindowOffset() {
		return guiNewWindowOffset;
	}

	public void setGuiNewWindowOffset(int guiNewWindowOffset) {
		this.guiNewWindowOffset = guiNewWindowOffset;
	}

	public double getGuiNewWindowSizeRatio() {
		return guiNewWindowSizeRatio;
	}

	public void setGuiNewWindowSizeRatio(double guiNewWindowSizeRatio) {
		this.guiNewWindowSizeRatio = guiNewWindowSizeRatio;
	}

	public int getGuiPixelsPerMeter() {
		return guiPixelsPerMeter;
	}

	public void setGuiPixelsPerMeter(int guiPixelsPerMeter) {
		this.guiPixelsPerMeter = guiPixelsPerMeter;
	}

	public boolean isGuiStartupOpenNewWindow() {
		return guiStartupOpenNewWindow;
	}

	public void setGuiStartupOpenNewWindow(boolean guiStartupOpenNewWindow) {
		this.guiStartupOpenNewWindow = guiStartupOpenNewWindow;
	}

	public DecimalFormat getIntegerFormatter() {
		return integerFormatter;
	}

	public void setIntegerFormatter(DecimalFormat integerFormatter) {
		this.integerFormatter = integerFormatter;
	}

	public Image getMandatoryImage() {
		return mandatoryImage;
	}

	public void setMandatoryImage(Image mandatoryImage) {
		this.mandatoryImage = mandatoryImage;
	}

	public Image getRotationImage() {
		return rotationImage;
	}

	public void setRotationImage(Image rotationImage) {
		this.rotationImage = rotationImage;
	}

	public String getGuiBoxLabelColor() {
		return guiBoxLabelColor;
	}

	public void setGuiBoxLabelColor(String guiBoxLabelColor) {
		this.guiBoxLabelColor = guiBoxLabelColor;
	}
    

	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(SimpleDateFormat dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	
	public String getDocumentExtension() {
		return documentExtension;
	}

	public void setDocumentExtension(String documentExtension) {
		this.documentExtension = documentExtension;
	}

	/** 
	 * save preferences to loadOrganizer.conf.xml
	 * the program ignores, if the file can not be open
	 */
	public void save() {
		StringBuffer config=new StringBuffer();
		config.append("<config>\n"); //$NON-NLS-1$
		config.append("   <guiNewWindowOffset>"+guiNewWindowOffset+"</guiNewWindowOffset>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiNewWindowSizeRatio>"+guiNewWindowSizeRatio+"</guiNewWindowSizeRatio>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiNewWindowMaximize>"+guiNewWindowMaximize+"</guiNewWindowMaximize>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiStartupOpenNewWindow>"+guiStartupOpenNewWindow+"</guiStartupOpenNewWindow>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiPixelsPerMeter>"+guiPixelsPerMeter+"</guiPixelsPerMeter>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiBoxColor>"+guiBoxColor+"</guiBoxColor>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <guiBoxLabelColor>"+guiBoxLabelColor+"</guiBoxLabelColor>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <enginePopulationSize>"+enginePopulationSize+"</enginePopulationSize>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <enginePopulationGrowth>"+enginePopulationGrowth+"</enginePopulationGrowth>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <engineFinishStableCount>"+engineFinishStableCount+"</engineFinishStableCount>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <engineBoxMovementStep>"+engineBoxMovementStep+"</engineBoxMovementStep>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <engineOptimizationDepth>"+engineOptimizationDepth+"</engineOptimizationDepth>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <engineThreadCount>"+engineThreadCount+"</engineThreadCount>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <engineRandomizeLoop>"+engineRandomizeLoop+"</engineRandomizeLoop>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <decimalFormat>"+decimalFormatter.toPattern()+"</decimalFormat>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <integerFormat>"+integerFormatter.toPattern()+"</integerFormat>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("   <dateFormat>"+dateFormatter.toPattern()+"</dateFormat>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		config.append("</config>"); //$NON-NLS-1$
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
			writer.write(config.toString());
			writer.close();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public Image getAppImage() {
		return appImage;
	}

	/** 
	 * save preferences to loadOrganizer.conf.xml
	 * the program ignores, if the file can not be open
	 */
	private void load() {
		try {
			Document xmlDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(configFile));
			
			NodeList nodeList=xmlDoc.getDocumentElement().getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++) {
				Node e=nodeList.item(i);
				if(!(e instanceof Element)) continue;
				
				String name=e.getNodeName();
				String value=e.getTextContent();
				
				setProperty(name,value);
			}
		
		}  catch(java.io.FileNotFoundException e) {
			// nop
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public int getEngineThreadCount() {
		if(engineThreadCount<1) engineThreadCount=1;
		return engineThreadCount;
	}

	public void setEngineThreadCount(int threadCount) {
		this.engineThreadCount = threadCount;
		if(engineThreadCount<1) engineThreadCount=1;
	}

	private void setProperty(String name, String value) {
		if(name==null) return;
		if(value==null) value=""; //$NON-NLS-1$
		
		if(name.equalsIgnoreCase("guiNewWindowOffset")) { //$NON-NLS-1$
			setGuiNewWindowOffset(new Integer(value));
		}
		if(name.equalsIgnoreCase("guiNewWindowSizeRatio")) { //$NON-NLS-1$
			setGuiNewWindowSizeRatio(new Double(value));
		}
		if(name.equalsIgnoreCase("guiNewWindowMaximize")) { //$NON-NLS-1$
			setGuiNewWindowMaximize(new Boolean(value));
		}
		if(name.equalsIgnoreCase("guiStartupOpenNewWindow")) { //$NON-NLS-1$
			setGuiStartupOpenNewWindow(new Boolean(value));
		}
		if(name.equalsIgnoreCase("guiPixelsPerMeter")) { //$NON-NLS-1$
			setGuiPixelsPerMeter(new Integer(value));
		}		
		if(name.equalsIgnoreCase("guiBoxColor")) { //$NON-NLS-1$
			setGuiBoxColor(value);
		}		
		if(name.equalsIgnoreCase("guiBoxLabelColor")) { //$NON-NLS-1$
			setGuiBoxLabelColor(value);
		}		
		if(name.equalsIgnoreCase("enginePopulationSize")) { //$NON-NLS-1$
			setEnginePopulationSize(new Integer(value));
		}
		if(name.equalsIgnoreCase("enginePopulationGrowth")) { //$NON-NLS-1$
			setEnginePopulationGrowth(new Double(value));
		}
		if(name.equalsIgnoreCase("engineFinishStableCount")) { //$NON-NLS-1$
			setEngineFinishStableCount(new Integer(value));
		}
		if(name.equalsIgnoreCase("engineBoxMovementStep")) { //$NON-NLS-1$
			setEngineBoxMovementStep(new Integer(value));
		}
		if(name.equalsIgnoreCase("engineOptimizationDepth")) { //$NON-NLS-1$
			setEngineOptimizationDepth(new Integer(value));
		}
		if(name.equalsIgnoreCase("engineRandomizeLoop")) { //$NON-NLS-1$
			setEngineRandomizeLoop(new Integer(value));
		}
		if(name.equalsIgnoreCase("engineThreadCount")) { //$NON-NLS-1$
			setEngineThreadCount(new Integer(value));
		}		
		if(name.equalsIgnoreCase("decimalFormat")) { //$NON-NLS-1$
			decimalFormatter=new DecimalFormat(value);
		}
		if(name.equalsIgnoreCase("integerFormat")) { //$NON-NLS-1$
			integerFormatter=new DecimalFormat(value);
		}
		if(name.equalsIgnoreCase("dateFormat")) { //$NON-NLS-1$
			dateFormatter=new SimpleDateFormat(value);
		}
	}
}
