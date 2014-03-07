/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/MenuDocumentFile.java $
 * $Id: MenuDocumentFile.java 13 2006-04-30 22:23:31Z saigon $
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

/**
 * menu File in the document window. handles saving the document
 * into gzipped xml file with the .LOD extension
 * 
 */
public class MenuDocumentFile {

	DocumentFrame documentFrame;
	Preferences preferences;

	MenuDocumentFile(DocumentFrame parentFrame) {
		this.documentFrame = parentFrame;
		preferences=documentFrame.loadOrganizer.preferences;
	}

	protected JMenu build() {
		JMenu file = new JMenu(Messages.getString("MenuDocumentFile.0")); //$NON-NLS-1$
		JMenuItem save = new JMenuItem(Messages.getString("MenuDocumentFile.1")); //$NON-NLS-1$
		JMenuItem preferences = new JMenuItem(Messages.getString("MenuDocumentFile.2")); //$NON-NLS-1$
		JMenuItem close = new JMenuItem(Messages.getString("MenuDocumentFile.3")); //$NON-NLS-1$


		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSettings();
			}			
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave();
			}			
		});
		
		file.add(save);
		file.add(preferences);
		file.addSeparator();
		file.add(close);
		
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				documentFrame.dispose();
			}			
		});
		
		return file;
	}
	
	
	void doSettings() {
		new DocumentPropertiesDialog(documentFrame, Messages.getString("MenuDocumentFile.4"), true, false,false).setVisible(true); //$NON-NLS-1$
	}
	

	/**
	 * save document into gzipped xml file with the LOD extension
	 */
	void doSave() {
		JFileChooser fc = new JFileChooser(); 
		String extension=preferences.getDocumentExtension();

		ExampleFileFilter filter = new ExampleFileFilter( 
		 		    new String[] {preferences.getDocumentExtension()}, Messages.getString("MenuDocumentFile.5")  //$NON-NLS-1$
		);
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		
		File f=new File(".").getAbsoluteFile(); //$NON-NLS-1$
		fc.setCurrentDirectory(f);
		fc.setSelectedFile(new File(f,documentFrame.documentName+"."+extension)); //$NON-NLS-1$
	
 		// show the filechooser 
 		int result = fc.showSaveDialog(documentFrame.loadOrganizer); 
 		 
 		// if we selected an image, load the image 
 		if(result == JFileChooser.APPROVE_OPTION) { 			
 			try {
 				String filename=fc.getSelectedFile().getPath();
 				if(!filename.toLowerCase().endsWith(extension.toLowerCase())) {
 					filename+="."+extension; //$NON-NLS-1$
 				}
	 			f=new File(filename);
	 			
	 			if(f.exists()) {
	 			     int answer =
	 			      JOptionPane.showConfirmDialog(documentFrame.loadOrganizer,
	 			               Messages.getString("MenuDocumentFile.9"), //$NON-NLS-1$
	 			                Messages.getString("MenuDocumentFile.10"), //$NON-NLS-1$
	 			       JOptionPane.YES_NO_OPTION,
	 			       JOptionPane.QUESTION_MESSAGE);
	 			     
	 			     if(answer != JOptionPane.YES_OPTION) return;
	 			}
	 			documentFrame.writeDocument(f);
 			} catch(Exception e) {
 				e.printStackTrace();
 				JOptionPane.showMessageDialog(documentFrame.loadOrganizer,Messages.getString("MenuDocumentFile.11"),Messages.getString("MenuDocumentFile.12"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
 			}
 		} 		
	}
	
}
