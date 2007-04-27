/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/MenuFile.java $
 * $Id: MenuFile.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * definition and handlers for main File menu
 * @author dsaiko
 *
 */
public class MenuFile {

	LoadOrganizer loadOrganizer;
	Preferences preferences;

	MenuFile(LoadOrganizer parentFrame) {
		this.loadOrganizer = parentFrame;
		preferences = parentFrame.preferences;
	}

	protected JMenu build() {
		JMenu file = new JMenu(Messages.getString("MenuFile.0")); //$NON-NLS-1$
		JMenuItem newWin = new JMenuItem(Messages.getString("MenuFile.1")); //$NON-NLS-1$
		JMenuItem open = new JMenuItem(Messages.getString("MenuFile.2")); //$NON-NLS-1$
		
		JMenuItem preferences = new JMenuItem(Messages.getString("MenuFile.3")); //$NON-NLS-1$
		JMenuItem quit = new JMenuItem(Messages.getString("MenuFile.4")); //$NON-NLS-1$

		newWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newDocument();
			}
		});

		
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOpen();
			}
		});
		
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preferences();
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		file.add(newWin);
		file.add(open);
		file.addSeparator();
		file.add(preferences);
		file.addSeparator();
		file.add(quit);
		return file;
	}

	public void quit() {
		System.exit(0);
	}

	public void newDocument() {
		try {
			final DocumentFrame doc = new DocumentFrame(loadOrganizer,null);
			loadOrganizer.openDocuments.add(doc);
			doc.addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					loadOrganizer.openDocuments.remove(doc);
				}
			});

			loadOrganizer.desktop.add(doc, LoadOrganizer.DOCLAYER);
			if (preferences.isGuiNewWindowMaximize()) {
				doc.setMaximum(true);
			}
			doc.setVisible(true);
			doc.setSelected(true);
		} catch(DocumentClosedException e) {
			//nop
		}
		catch (Throwable e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void preferences() {
		PreferencesDialog settings=new PreferencesDialog(loadOrganizer,Messages.getString("MenuFile.5"),true); //$NON-NLS-1$
		settings.setVisible(true);
	}
	

	/**
	 * open saved document
	 */
	void doOpen() {
		JFileChooser fc = new JFileChooser(); 

		ExampleFileFilter filter = new ExampleFileFilter( 
		 	new String[] {preferences.getDocumentExtension()}, Messages.getString("MenuFile.6")  //$NON-NLS-1$
		);
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		
		File f=new File(".").getAbsoluteFile(); //$NON-NLS-1$
		fc.setCurrentDirectory(f);
	
 		// show the filechooser 
 		int result = fc.showOpenDialog(loadOrganizer); 
 		 
 		// if we selected an image, load the image 
 		if(result == JFileChooser.APPROVE_OPTION) { 			
 			try {
 				final DocumentFrame doc = new DocumentFrame(loadOrganizer, fc.getSelectedFile());
 				loadOrganizer.openDocuments.add(doc);
 				doc.addInternalFrameListener(new InternalFrameAdapter() {
 					@Override
					public void internalFrameClosed(InternalFrameEvent e) {
 						loadOrganizer.openDocuments.remove(doc);
 					}
 				});

 				loadOrganizer.desktop.add(doc, LoadOrganizer.DOCLAYER);
 				if (preferences.isGuiNewWindowMaximize()) {
 					doc.setMaximum(true);
 				}
 				doc.setVisible(true);
 				doc.setSelected(true);
 			} catch(Exception e) {
 				e.printStackTrace();
 				JOptionPane.showMessageDialog(loadOrganizer,Messages.getString("MenuFile.8"),Messages.getString("MenuFile.9"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
 			}
 		} 		
	}
	
}
