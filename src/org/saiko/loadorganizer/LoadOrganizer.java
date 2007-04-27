/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/LoadOrganizer.java $
 * $Id: LoadOrganizer.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * main GUI class of the application
 */
public class LoadOrganizer extends JFrame {
	
	private static final long serialVersionUID = 1L;

	JMenuBar menuBar;

	JDesktopPane desktop;

	JInternalFrame toolPalette;

	JCheckBoxMenuItem showToolPaletteMenuItem;
	
	/** open document windows **/
	final List<DocumentFrame> openDocuments=Collections.synchronizedList(new ArrayList<DocumentFrame>());
	

	static final Integer DOCLAYER = new Integer(5);

	static final Integer TOOLLAYER = new Integer(6);

	static final Integer HELPLAYER = new Integer(7);

	MenuFile menuFile;

	Preferences preferences=new Preferences();

	/**
	 * constructor.
	 * Creates main application window
	 */
	public LoadOrganizer() {
		super(Messages.getString("appTitle")); //$NON-NLS-1$
		final int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
		buildContent();
		buildMenus();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuFile.quit();
			}
		});
		//open new document if set in preferences to do so on start of application
		if (preferences.isGuiStartupOpenNewWindow()) {
			menuFile.newDocument();
		}
		setIconImage(preferences.getAppImage());
	}

	protected void buildMenus() {

		menuBar = new JMenuBar();
		menuBar.setOpaque(true);

		menuFile = new MenuFile(this);

		menuBar.add(menuFile.build());
		setJMenuBar(menuBar);
	}

	protected void buildContent() {
		desktop = new JDesktopPane();
		getContentPane().add(desktop);
	}	

	/**
	 * main 
	 * argument can be "JAVALF" or "SYSTEMLF" to set Java or System look and feel
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//select the look and feel
			if(args!=null && args.length==1 && "JAVALF".equalsIgnoreCase(args[0])) { //$NON-NLS-1$
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
			JFrame frame = new LoadOrganizer();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}