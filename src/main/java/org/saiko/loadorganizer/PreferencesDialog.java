/**
 * $LastChangedDate: 2006-05-11 18:31:17 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 15 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/PreferencesDialog.java $
 * $Id: PreferencesDialog.java 15 2006-05-11 16:31:17Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * global preferences dialog.
 * the preferences are written to the file LoadOrganizer.cfg.xml in the current classpath
 * if the file can not be created/read, no error message is displayed
 */
public class PreferencesDialog extends JDialog {


	private static final long serialVersionUID = 1L;
	
	LoadOrganizer loadOrganizer=null;
	Preferences preferences;
	
	public PreferencesDialog(LoadOrganizer owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		
		loadOrganizer=owner;
		preferences=loadOrganizer.preferences;
		initialize();
	}

	JTextField populationSize;
	JTextField populationGrowth;
	JTextField stabilityCount;
	JTextField threadCount;
	JTextField optimationDepth;
	JTextField conteinerMovement;
	JTextField randomCycles;
	
	JCheckBox autoNewDocument;
	JCheckBox autoDocumentMaximize;
	JTextField pixelsPerMeter;
	JTextField integerFormat;
	JTextField decimalFormat;
	JTextField dateFormat;
	JButton boxColor;
	JButton boxLabelColor;
	
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
		JPanel panelComputationSettings=new JPanel();
		GridBagConstraints c=new GridBagConstraints();

		setTitle(Messages.getString("PreferencesDialog.0")); //$NON-NLS-1$

		{
			panelComputationSettings.setLayout(new GridBagLayout());
			
			panelComputationSettings.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.BLACK),
					Messages.getString("PreferencesDialog.1") //$NON-NLS-1$
			));
			JLabel labels[] = new JLabel[] {
					new JLabel(Messages.getString("PreferencesDialog.2")), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.3")), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.4")), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.51")),  //$NON-NLS-1$
					new JLabel("<html>&nbsp;</html>"), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.6")), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.7")), //$NON-NLS-1$
					new JLabel(Messages.getString("PreferencesDialog.8")), //$NON-NLS-1$
			};
			
			String tips[] = new String[] {
					Messages.getString("PreferencesDialog.9"), //$NON-NLS-1$
					Messages.getString("PreferencesDialog.10"), //$NON-NLS-1$
					Messages.getString("PreferencesDialog.11"), //$NON-NLS-1$
					Messages.getString("PreferencesDialog.52"), //$NON-NLS-1$
					null,
					Messages.getString("PreferencesDialog.12"), //$NON-NLS-1$
					Messages.getString("PreferencesDialog.13"), //$NON-NLS-1$
					Messages.getString("PreferencesDialog.14"), //$NON-NLS-1$
			};
			
			JTextField inputs[] = new JTextField[] {
					populationSize=new JTextField(),
					populationGrowth=new JTextField(),
					stabilityCount=new JTextField(),
					threadCount=new JTextField(),
					null,
					optimationDepth=new JTextField(),
					conteinerMovement=new JTextField(),
					randomCycles=new JTextField(),
			};
			
			int values[] = new int[] {
					preferences.getEnginePopulationSize(),
					(int)(preferences.getEnginePopulationGrowth()),
					preferences.getEngineFinishStableCount(),
					preferences.getEngineThreadCount(),
					0,
					preferences.getEngineOptimizationDepth(),
					preferences.getEngineBoxMovementStep(),
					preferences.getEngineRandomizeLoop(),
					0
			};		
	
			for(int i=0; i<labels.length; i++) {
				JLabel label=labels[i];
				label.setHorizontalAlignment(SwingConstants.RIGHT);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx=0;
				c.gridy=i;
				c.insets=new Insets(3,3,3,3);
				panelComputationSettings.add(label,c);
				
				if(inputs[i]!=null) {
					JTextField input=inputs[i];
					
					c.gridx=1;
					c.fill=GridBagConstraints.NONE;
					panelComputationSettings.add(input,c);
		    			
					input.setPreferredSize(new Dimension(150,(input.getFont().getSize()*2)));
		    			input.setEditable(true);
		    			input.setHorizontalAlignment(SwingConstants.RIGHT);
		    			input.setText(String.valueOf(values[i]));
	
		    			label.setToolTipText(tips[i]);
					input.setToolTipText(tips[i]);
				}
			}
		}
		
		
		JPanel panelGuiSettings=new JPanel();		
		panelGuiSettings.setLayout(new GridBagLayout());
		
		panelGuiSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				Messages.getString("PreferencesDialog.15") //$NON-NLS-1$
		));
		JLabel[] labels = new JLabel[] {
				new JLabel(Messages.getString("PreferencesDialog.16")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.17")), //$NON-NLS-1$
				new JLabel("<html>&nbsp;</html>"), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.19")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.20")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.21")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.22")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.23")), //$NON-NLS-1$
				new JLabel(Messages.getString("PreferencesDialog.24")), //$NON-NLS-1$
		};
		
		final String[] tips = new String[] {
				Messages.getString("PreferencesDialog.25"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.26"), //$NON-NLS-1$
				null,
				Messages.getString("PreferencesDialog.27"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.28"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.29"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.30"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.31"), //$NON-NLS-1$
				Messages.getString("PreferencesDialog.32"), //$NON-NLS-1$
		};
		
		
		JComponent inputs[] = new JComponent[] {
				autoNewDocument=new JCheckBox(),
				autoDocumentMaximize=new JCheckBox(),
				null,
				pixelsPerMeter=new JTextField(String.valueOf(preferences.getGuiPixelsPerMeter())),
				integerFormat=new JTextField(String.valueOf(preferences.getIntegerFormatter().toPattern())),
				decimalFormat=new JTextField(String.valueOf(preferences.getDecimalFormatter().toPattern())),
				dateFormat=new JTextField(String.valueOf(preferences.getDateFormatter().toPattern())),
				boxColor=new JButton(preferences.getGuiBoxColor()),
				boxLabelColor=new JButton(preferences.getGuiBoxLabelColor()),
		};
		autoNewDocument.setSelected(preferences.isGuiStartupOpenNewWindow());
		autoDocumentMaximize.setSelected(preferences.isGuiNewWindowMaximize());
		
		for(int i=0; i<labels.length; i++) {
			JLabel label=labels[i];
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx=0;
			c.gridy=i;
			c.insets=new Insets(3,3,3,3);
			panelGuiSettings.add(label,c);
			
			if(inputs[i]!=null) {
				JComponent input=inputs[i];
				
				c.gridx=1;
				c.fill=GridBagConstraints.NONE;
				panelGuiSettings.add(input,c);
	    			
				input.setPreferredSize(new Dimension(150,(input.getFont().getSize()*2)));
	    			if(input instanceof JTextField) ((JTextField)input).setHorizontalAlignment(SwingConstants.RIGHT);

	    			label.setToolTipText(tips[i]);
				input.setToolTipText(tips[i]);
			}
		}	
		
		final JColorChooser chooser = new JColorChooser(Color.decode(boxColor.getText())); 

        final ActionListener okListener1 = new ActionListener() { 
            public void actionPerformed(ActionEvent ae) { 
           	 	String str = Integer.toHexString( chooser.getColor().getRGB() & 0xFFFFFF );
           	 	str= "#" + "000000".substring( str.length() ) + str.toUpperCase();  //$NON-NLS-1$ //$NON-NLS-2$
           	 	boxColor.setText(str); 
            } 
        }; 	
        final JDialog settingsDlg=this;
        
        boxColor.setIcon(new ColorSwatch(boxColor)); 
        boxColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JDialog dialog = JColorChooser.createDialog(
											  settingsDlg, 
					                          tips[6], 
					                          true, 
					                          chooser, 
					                          okListener1, 
					                          null); 		
					dialog.setVisible(true);
			}				
		});			
		
        
		final JColorChooser chooser2 = new JColorChooser(Color.decode(boxLabelColor.getText())); 

        final ActionListener okListener2 = new ActionListener() { 
            public void actionPerformed(ActionEvent ae) { 
           	 	String str = Integer.toHexString( chooser2.getColor().getRGB() & 0xFFFFFF );
           	 	str= "#" + "000000".substring( str.length() ) + str.toUpperCase();  //$NON-NLS-1$ //$NON-NLS-2$
           	 	boxLabelColor.setText(str); 
            } 
        }; 	
        
        boxLabelColor.setIcon(new ColorSwatch(boxLabelColor)); 
        boxLabelColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JDialog dialog = JColorChooser.createDialog(
											  settingsDlg, 
					                          tips[6], 
					                          true, 
					                          chooser2, 
					                          okListener2, 
					                          null); 		
					dialog.setVisible(true);
			}				
		});        
		
		JButton ok=new JButton(Messages.getString("PreferencesDialog.37")); //$NON-NLS-1$
		JButton cancel=new JButton(Messages.getString("PreferencesDialog.38")); //$NON-NLS-1$
		JPanel toolbar=new JPanel(new GridBagLayout());
		c.gridx=0;
		c.gridy=0;
		c.fill=GridBagConstraints.NONE;
		c.gridx=1;
		toolbar.add(ok,c);
		c.gridx=2;
		toolbar.add(cancel,c);
		
		
		
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
		settings.add(panelComputationSettings,c);
		c.gridy=1;
		settings.add(panelGuiSettings,c);
		c.gridy=2;
		settings.add(toolbar,c);

		pack();
		setLocationRelativeTo(loadOrganizer);
	}
	
	void okAction() {
		JTextField numbers[]=new JTextField[] {
				populationSize,
				populationGrowth,
				stabilityCount,
				optimationDepth,
				conteinerMovement,
				randomCycles,
				pixelsPerMeter,
				threadCount
		};
		for(int i=0; i<numbers.length; i++) {
			try {
				int v=Integer.valueOf(numbers[i].getText());
				if(v<0) throw new Exception();
			} catch(Throwable e) {
				JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.39"),Messages.getString("PreferencesDialog.40"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				numbers[i].requestFocus();
				return;
			}
		}
		
		int valuePopulationSize=Integer.parseInt(populationSize.getText());
		if(valuePopulationSize<2) {
			JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.41"),Messages.getString("PreferencesDialog.42"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			populationSize.requestFocus();
			return;			
		}
		

		try {
			new DecimalFormat(decimalFormat.getText()).format(1.11);
		} catch(Throwable e) {
			JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.43"),Messages.getString("PreferencesDialog.44"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			decimalFormat.requestFocus();
			return;
		}
		try {
			new DecimalFormat(integerFormat.getText()).format(1.11);
		} catch(Throwable e) {
			JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.45"),Messages.getString("PreferencesDialog.46"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			integerFormat.requestFocus();
			return;
		}
		try {
			new SimpleDateFormat(dateFormat.getText()).format(new java.util.Date());
		} catch(Throwable e) {
			JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.47"),Messages.getString("PreferencesDialog.48"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			dateFormat.requestFocus();
			return;
		}
		
		int ppm = new Integer(pixelsPerMeter.getText());
		if(ppm!=preferences.getGuiPixelsPerMeter() && loadOrganizer.openDocuments.size()>0) {
			JOptionPane.showMessageDialog(this,Messages.getString("PreferencesDialog.49"),Messages.getString("PreferencesDialog.50"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			pixelsPerMeter.requestFocus();
			return;
		}
		
		preferences.setEnginePopulationSize(valuePopulationSize);
		preferences.setEnginePopulationGrowth(Integer.valueOf(populationGrowth.getText()));
		preferences.setEngineFinishStableCount(Integer.parseInt(stabilityCount.getText()));
		preferences.setEngineOptimizationDepth(Integer.parseInt(optimationDepth.getText()));
		preferences.setEngineBoxMovementStep(Integer.parseInt(conteinerMovement.getText()));
		preferences.setEngineRandomizeLoop(Integer.parseInt(randomCycles.getText()));
		preferences.setEngineThreadCount(Integer.parseInt(threadCount.getText()));
		
		preferences.setGuiStartupOpenNewWindow(autoNewDocument.isSelected());
		preferences.setGuiNewWindowMaximize(autoDocumentMaximize.isSelected());
		preferences.setGuiPixelsPerMeter(Integer.valueOf(pixelsPerMeter.getText()));
		preferences.setDecimalFormatter(new DecimalFormat(decimalFormat.getText()));
		preferences.setIntegerFormatter(new DecimalFormat(integerFormat.getText()));
		preferences.setDateFormatter(new SimpleDateFormat(dateFormat.getText()));
		preferences.setGuiBoxColor(boxColor.getText());
		preferences.setGuiBoxLabelColor(boxLabelColor.getText());

		preferences.save();

		for(DocumentFrame doc:loadOrganizer.openDocuments) {
			doc.panelWorkplace.computeInfo();
		}
		loadOrganizer.repaint();
		dispose();
		
	}
}

class ColorSwatch implements Icon { 
 	JButton btn; 
  
 	public ColorSwatch(JButton btn) { 
 	    this.btn = btn; 
 	} 
  
 	public int getIconWidth() { 
 	    return 11; 
 	} 
  
 	public int getIconHeight() { 
 	    return 11; 
 	} 
  
 	public void paintIcon(Component c, Graphics g, int x, int y) { 
 	    g.setColor(Color.black); 
 	    g.fillRect(x, y, getIconWidth(), getIconHeight());
 	    g.setColor(Color.decode(btn.getText()));
 	    g.fillRect(x+2, y+2, getIconWidth()-4, getIconHeight()-4); 
 	} 
     } 