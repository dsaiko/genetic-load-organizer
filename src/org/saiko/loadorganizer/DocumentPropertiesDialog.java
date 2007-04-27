/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/DocumentPropertiesDialog.java $
 * $Id: DocumentPropertiesDialog.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * dialog with document properties
 * this dialog is shown to set or display document properties,
 * even for opening new document or loading existing one
 * @author dsaiko
 */
public class DocumentPropertiesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DocumentFrame documentFrame=null;
	Preferences preferences;
	boolean allowTruckModifications=true;
	boolean disableAllModifications=false;
	
	/**
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @param allowTruckModifications - truck modifications are only allowed for new documents
	 * @param disableAllModifications - if all modifications are disabled, then only OK button is shown, no Cancel
	 * @throws HeadlessException
	 */
	public DocumentPropertiesDialog(DocumentFrame owner, String title, boolean modal, boolean allowTruckModifications, boolean disableAllModifications) throws HeadlessException {
		super(owner.loadOrganizer, title, modal);
		
		documentFrame=owner;
		preferences=owner.preferences;
		this.allowTruckModifications=allowTruckModifications;
		this.disableAllModifications=disableAllModifications;

		initialize();
	}

	JTextField documentName;
	JTextField documentAuthor;
	JTextField documentDate;

	JTextField truckWidth;
	JTextField truckHeight;
	JTextField truckTonnage;
		
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel settings=new JPanel();
		setContentPane(settings);

		settings.setLayout(new BoxLayout(settings,BoxLayout.Y_AXIS));
		JPanel panelSettings=new JPanel();
		GridBagConstraints c=new GridBagConstraints();

		setTitle(Messages.getString("DocumentPropertiesDialog.0")); //$NON-NLS-1$

		{
			panelSettings.setLayout(new GridBagLayout());
			
			panelSettings.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.BLACK),
					Messages.getString("DocumentPropertiesDialog.1") //$NON-NLS-1$
			));
			JLabel labels[] = new JLabel[] {
					new JLabel(Messages.getString("DocumentPropertiesDialog.2")), //$NON-NLS-1$
					new JLabel(Messages.getString("DocumentPropertiesDialog.3")), //$NON-NLS-1$
					new JLabel(Messages.getString("DocumentPropertiesDialog.4")), //$NON-NLS-1$
					new JLabel("<html>&nbsp;</html>"), //$NON-NLS-1$
					new JLabel(Messages.getString("DocumentPropertiesDialog.6")), //$NON-NLS-1$
					new JLabel(Messages.getString("DocumentPropertiesDialog.7")), //$NON-NLS-1$
					new JLabel(Messages.getString("DocumentPropertiesDialog.8")), //$NON-NLS-1$
			};
			
			String tips[] = new String[] {
					Messages.getString("DocumentPropertiesDialog.9"), //$NON-NLS-1$
					Messages.getString("DocumentPropertiesDialog.10"), //$NON-NLS-1$
					Messages.getString("DocumentPropertiesDialog.11"), //$NON-NLS-1$
					null,
					Messages.getString("DocumentPropertiesDialog.12"), //$NON-NLS-1$
					Messages.getString("DocumentPropertiesDialog.13"), //$NON-NLS-1$
					Messages.getString("DocumentPropertiesDialog.14"), //$NON-NLS-1$
			};
							
			JTextField inputs[] = new JTextField[] {
					documentName=new JTextField(documentFrame.documentName),
					documentAuthor=new JTextField(documentFrame.documentAuthor),
					documentDate=new JTextField(preferences.getDateFormatter().format(documentFrame.documentDate)),
					null,
					truckWidth=new JTextField(String.valueOf(documentFrame.documentTruckSize.width)),
					truckHeight=new JTextField(String.valueOf(documentFrame.documentTruckSize.height)),
					truckTonnage=new JTextField(String.valueOf(documentFrame.documentTruckTonnage)),
			};
	
	
			for(int i=0; i<labels.length; i++) {
				JLabel label=labels[i];
				label.setHorizontalAlignment(SwingConstants.RIGHT);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx=0;
				c.gridy=i;
				c.insets=new Insets(3,3,3,3);
				panelSettings.add(label,c);
				
				if(inputs[i]!=null) {
					JTextField input=inputs[i];
					
					c.gridx=1;
					c.fill=GridBagConstraints.NONE;
					panelSettings.add(input,c);
		    			
					input.setPreferredSize(new Dimension(150,(input.getFont().getSize()*2)));
		    			input.setEditable(true);	
		    			label.setToolTipText(tips[i]);
					input.setToolTipText(tips[i]);
					
					if(disableAllModifications) {
						input.setEnabled(false);
					}
				}
			}

			truckHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			truckWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			truckTonnage.setHorizontalAlignment(SwingConstants.RIGHT);
			if(!allowTruckModifications) {
				truckHeight.setEnabled(false);
				truckWidth.setEnabled(false);
				truckTonnage.setEnabled(false);
			}
		}
				       
		
		JButton ok=new JButton(Messages.getString("DocumentPropertiesDialog.15")); //$NON-NLS-1$
		JButton cancel=new JButton(Messages.getString("DocumentPropertiesDialog.16")); //$NON-NLS-1$
		JPanel toolbar=new JPanel(new GridBagLayout());
		c.gridx=0;
		c.gridy=0;
		c.fill=GridBagConstraints.NONE;
		c.gridx=1;
		toolbar.add(ok,c);
		
		if(!disableAllModifications) {
			c.gridx=2;
			toolbar.add(cancel,c);
		}
		
		
		
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okAction();
			}
		});
		
		
		settings.setLayout(new GridBagLayout());
		c.insets=new Insets(6,6,6,6);
		c.gridx=0;
		c.gridy=0;
		settings.add(panelSettings,c);
		c.gridy=1;
		settings.add(toolbar,c);

		pack();
		setLocationRelativeTo(documentFrame.loadOrganizer);
		ok.requestFocus();
	}
	
	boolean pressedOk=false;
	
	void okAction() {
		pressedOk=true;
		if(disableAllModifications) {
			dispose();
			return;
		}
		
		JTextField numbers[]=new JTextField[] {
				truckWidth,
				truckHeight,
				truckTonnage,
		};
		for(int i=0; i<numbers.length; i++) {
			try {
				int v=Integer.valueOf(numbers[i].getText());
				if(v<0) throw new Exception();
			} catch(Throwable e) {
				JOptionPane.showMessageDialog(this,Messages.getString("DocumentPropertiesDialog.17"),Messages.getString("DocumentPropertiesDialog.18"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				numbers[i].requestFocus();
				return;
			}
		}
		
		try {
			preferences.getDateFormatter().parse(documentDate.getText());
		} catch(Throwable e) {
			JOptionPane.showMessageDialog(this,Messages.getString("DocumentPropertiesDialog.19"),Messages.getString("DocumentPropertiesDialog.20"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			documentDate.requestFocus();
			return;
		}

		if(documentName.getText()==null || documentName.getText().trim().length()==0) {
			JOptionPane.showMessageDialog(this,Messages.getString("DocumentPropertiesDialog.21"),Messages.getString("DocumentPropertiesDialog.22"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			documentName.requestFocus();
			return;
		}
		
		if(documentAuthor.getText()==null || documentAuthor.getText().trim().length()==0) {
			JOptionPane.showMessageDialog(this,Messages.getString("DocumentPropertiesDialog.23"),Messages.getString("DocumentPropertiesDialog.24"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			documentDate.requestFocus();
			return;
		}

		if(allowTruckModifications) {
			documentFrame.documentTruckSize=new Dimension(new Integer(truckWidth.getText()), new Integer(truckHeight.getText()));
		}
		
		documentFrame.documentAuthor=documentAuthor.getText();
		documentFrame.documentName=documentName.getText();
		documentFrame.setTitle(documentFrame.documentName);
		try {
			documentFrame.documentDate=preferences.getDateFormatter().parse(documentDate.getText());
		} catch(Throwable e) {
			// format of date was already checked
			e.printStackTrace();
		}
		
		if(documentFrame.panelWorkplace!=null) {
			documentFrame.panelWorkplace.computeInfo();
		}
		documentFrame.repaint();
		dispose();
		
	}
	
	/**
	 * invokes modal dialog and returns true, if ok button was pressed
	 *
	 */
	public boolean doModal() {
		setModal(true);
		setVisible(true);
		return pressedOk;
	}
}

 