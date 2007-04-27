/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/LibraryPanel.java $
 * $Id: LibraryPanel.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.saiko.loadorganizer.engine.Container;

/**
 * library panel which holds library table and statistic table
 *
 */
public class LibraryPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	DocumentFrame documentFrame;
	JTable libraryTable;
	JPanel infoPanel;
	Preferences preferences;
	JPanel panelBottom;
	JScrollPane libraryScrollPane;
	TableSorter libraryTableSorter;
	
	LibraryPanel(DocumentFrame parentFrame) {
		preferences = parentFrame.loadOrganizer.preferences;
		
		this.documentFrame=parentFrame;
		this.setLayout(new BorderLayout());

		JPanel panelTop=new JPanel();
		panelBottom=new JPanel();
		final JSplitPane splitter=new JSplitPane(JSplitPane.VERTICAL_SPLIT,panelBottom,panelTop);
	   	splitter.setContinuousLayout(true);
	   	splitter.setOneTouchExpandable(true);

		panelBottom.setLayout(new BorderLayout());

		JPanel toolbar=new JPanel();

		
		JButton btnAddToWorkplace=new JButton(Messages.getString("LibraryPanel.0")); //$NON-NLS-1$

		btnAddToWorkplace.setToolTipText(Messages.getString("LibraryPanel.1")); //$NON-NLS-1$
		
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    
		c.gridx=0;
		c.gridy=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		toolbar.add(new JLabel("<html>&nbsp;</html>"),c); //$NON-NLS-1$
		
		c.gridx=1;
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		toolbar.add(btnAddToWorkplace,c);		
		
		
		
		btnAddToWorkplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] ii=libraryTable.getSelectedRows();
				if(ii!=null) {
					addToWorkplace(ii);
				}
			}
		});

		JPanel toolbar2=new JPanel();
		panelTop.setLayout(new BorderLayout());
		toolbar2.setLayout(new GridBagLayout());
	    c = new GridBagConstraints();
	    

		JButton btnSettings=new JButton(Messages.getString("LibraryPanel.3")); //$NON-NLS-1$
		JButton btnCompute=new JButton(Messages.getString("LibraryPanel.4")); //$NON-NLS-1$

		btnSettings.setToolTipText(Messages.getString("LibraryPanel.5")); //$NON-NLS-1$
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				documentFrame.menuFile.doSettings();
			}
		});
		
		btnCompute.setToolTipText(Messages.getString("LibraryPanel.6")); //$NON-NLS-1$
		btnCompute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compute();
		}
		});
		
		
		c.gridx=0;
		c.gridy=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		toolbar2.add(new JLabel("<html>&nbsp;</html>"),c); //$NON-NLS-1$
		
		c.gridx=1;
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		toolbar2.add(btnSettings,c);
		c.gridx=2;
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		toolbar2.add(btnCompute,c);
		
		infoPanel=new JPanel();
		JScrollPane scrollInfo=new JScrollPane(infoPanel);
		createInfoPanel();
		createLibraryTable();
		panelBottom.add(toolbar,BorderLayout.SOUTH);

		panelTop.add(toolbar2,BorderLayout.SOUTH);
		panelTop.add(scrollInfo,BorderLayout.CENTER);
		
		splitter.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				splitter.setDividerLocation(0.59);
			}
		});
		
		this.add(splitter,BorderLayout.CENTER);
	}
	
	public void createLibraryTable() {

        // final
        final String[] names = {
        		Messages.getString("LibraryPanel.8"), //$NON-NLS-1$
        		Messages.getString("LibraryPanel.9"), //$NON-NLS-1$
        };

        // Create the dummy data (a few rows of names)
        final List<Container> data=Library.getContainers();

        // Create a model of the data.
        TableModel dataModel = new AbstractTableModel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public int getColumnCount() { return names.length; }
            public int getRowCount() { return data.size();}
            public Object getValueAt(int row, int col) {
            		Container item=data.get(row);
            		switch(col) {
            		case -1: return item;
            		case 0: return item.getName();
            		case 1: return documentFrame.panelWorkplace.getUssages(item);
            		default: return "?"+col+"?"; //$NON-NLS-1$ //$NON-NLS-2$
            		}
            	}
            @Override
			public String getColumnName(int column) {return names[column];}
            @SuppressWarnings("unchecked") //$NON-NLS-1$
			@Override
			public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            @Override
			public boolean isCellEditable(int row, int col) {return false;}
            @Override
			public void setValueAt(Object aValue, int row, int column) { /** nop **/ }
            
         };

         
         final String[] columnToolTips = {
        		    Messages.getString("LibraryPanel.12"), //$NON-NLS-1$
        		    Messages.getString("LibraryPanel.13"), //$NON-NLS-1$
        	};
                 
         libraryTableSorter = new TableSorter(dataModel);
         libraryTable = new JTable(libraryTableSorter) {
        	    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

				//Implement table header tool tips.
        	    @Override
				protected JTableHeader createDefaultTableHeader() {
        	        return new JTableHeader(columnModel) {
        	            /**
						 * 
						 */
						private static final long serialVersionUID = -4220435942564588579L;

						@Override
						public String getToolTipText(MouseEvent e) {
        	                java.awt.Point p = e.getPoint();
        	                int index = columnModel.getColumnIndexAtX(p.x);
        	                int realIndex = 
        	                        columnModel.getColumn(index).getModelIndex();
        	                return columnToolTips[realIndex];
        	            }
        	        };
        	    }    
         };
         
         libraryTableSorter.setTableHeader(libraryTable.getTableHeader());
         libraryTableSorter.setSortingStatus(0, TableSorter.ASCENDING);

	    
	    Font orgFont=libraryTable.getFont();
	    Font font=new Font(orgFont.getName(),orgFont.getStyle(),11);
	    libraryTable.setFont(font);
	    
	    libraryScrollPane = new JScrollPane(libraryTable);
		
	    libraryScrollPane.setPreferredSize(new Dimension(0,0));
	
	    // Disable auto resizing
	    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int c=0; c<libraryTable.getColumnCount(); c++) {
            packColumn(libraryTable, c, 2	, font);
        }
        
        
        libraryTable.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent e) {
            		if(e.getClickCount()>1) {
            			addToWorkplace(libraryTable.rowAtPoint(e.getPoint()));
            		}
            }
            @Override
			public void mouseReleased(MouseEvent e) {
        			selectInWorkpalce();
        		}
        });
        
        panelBottom.add(libraryScrollPane,BorderLayout.CENTER);
    }	
	    
    /** 
     * tries to pack column of the JTable according to the width of data 
     */
    protected void packColumn(JTable table, int vColIndex, int margin, Font font) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;
    
        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(
            table, col.getHeaderValue(), false, false, 0, 0);
        comp.setFont(font);
//        width = comp.getPreferredSize().width;
    
        // Get maximum width of column data
        for (int r=0; r<table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            comp.setFont(font);
            width = Math.max(width, comp.getPreferredSize().width);
        }
    
        // Add margin
        width += 2*margin;
    
        // Set the width
        col.setPreferredWidth(width);
        
    }
    
    /**
     * adds to workplace item from the library identified by tableRow
     * @param tableRow
     */
    void addToWorkplace(int tableRow) {
    		documentFrame.panelWorkplace.addContainer((Container)libraryTable.getValueAt(tableRow,-1),true);
    }
    
    /**
     * adds to workplace item from the library identified by tableRow
     * @param tableRow
     */
    void addToWorkplace(int[] tableRows) {
    		if(tableRows==null)return;
    		documentFrame.panelWorkplace.switchSelected(null,true,false);
    		for(int i=0; i<tableRows.length; i++) {
        		documentFrame.panelWorkplace.addContainer((Container)libraryTable.getValueAt(tableRows[i],-1),false);
    		}
    }
    
    /**
     * select the components in the workplace, which represents the same container
     * @param tableRow
     */
    void selectInWorkpalce() {
    		int rows[]=libraryTable.getSelectedRows();
    		if(rows==null) return;

    		documentFrame.panelWorkplace.switchSelected(null,true,false);
    		for(int i=0; i<rows.length; i++) {
    			documentFrame.panelWorkplace.selectAll((Container)libraryTable.getValueAt(rows[i],-1));
    		}
    }
    
    /**
     * selects container in the library menu
     * @param containerComponent
     */
    void selectRow(ContainerComponent containerComponent) {
    		
    		for(int i=0; i<libraryTable.getRowCount(); i++) {
    			Container rowData=(Container) libraryTable.getValueAt(i,-1);
    			if(rowData==containerComponent.container) {
    				libraryTable.setRowSelectionInterval(i,i);
    				Rectangle rect = libraryTable.getCellRect(i, 0, true);
    				libraryTable.scrollRectToVisible(rect);
    				break;
    			}
    		}
    }
    
    void removeSelection() {
    		if(libraryTable==null) return;
    		libraryTable.clearSelection();
    }
    
    /**
     * selects container in the library menu - add to the current selection
     * @param containerComponent
     */
    void selectMoreRow(ContainerComponent containerComponent) {
    		if(libraryTable==null) return;
    		
    		for(int i=0; i<libraryTable.getRowCount(); i++) {
    			Container rowData=(Container) libraryTable.getValueAt(i,-1);
    			if(rowData==containerComponent.container) {
    				libraryTable.addRowSelectionInterval(i,i);
    				Rectangle rect = libraryTable.getCellRect(i, 0, true);
    				libraryTable.scrollRectToVisible(rect);
    				break;
    			}
    		}
    }    
    
    
    void createInfoPanel() {
	    Font orgFont=infoPanel.getFont();
	    Font font=new Font(orgFont.getName(),orgFont.getStyle(),11);
	    infoPanel.setFont(font);
	    
	    documentName=new JLabel(documentFrame.documentName);
	    authorInfo=new JLabel(documentFrame.documentAuthor+", "); //$NON-NLS-1$
	    dateInfo=new JLabel(preferences.getDateFormatter().format(documentFrame.documentDate));
	    
    		infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
	    JPanel line2=new JPanel();
	    	line2.add(authorInfo);
	    line2.add(dateInfo);
	    
	    JPanel line1=new JPanel();
	    line1.add(documentName);
	    
	    List<JTextField> fields=new ArrayList<JTextField>();

	    JPanel values=new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.weightx=0.5;
	    int row=0;
	    JLabel label;

	    label=new JLabel(Messages.getString("LibraryPanel.15")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    JTextField trucksize=new JTextField(documentFrame.documentTruckSize.width+"x"+documentFrame.documentTruckSize.height); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(trucksize,c);
	    fields.add(trucksize);
	    row++;

	    double areaSize=(((double)documentFrame.documentTruckSize.width*documentFrame.documentTruckSize.height)/1000000.0);
	    
	    label=new JLabel(Messages.getString("LibraryPanel.17")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    JTextField totalArea=new JTextField(preferences.getDecimalFormatter().format(areaSize));
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(totalArea,c);
	    fields.add(totalArea);	    
	    row++;
	    
	    label=new JLabel(Messages.getString("LibraryPanel.18")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    JTextField weightCapacity=new JTextField(preferences.getDecimalFormatter().format(documentFrame.documentTruckTonnage));
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(weightCapacity,c);
	    fields.add(weightCapacity);	
	    row++;
	    
	    label=new JLabel("<html>&nbsp;</html>"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    c.gridwidth=2;
	    values.add(label,c);
	    row++;
	    c.gridwidth=1;
	    
	    
	    label=new JLabel(Messages.getString("LibraryPanel.20")); //$NON-NLS-1$
	    numberOfBoxes=new JTextField("0"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(numberOfBoxes,c);
	    fields.add(numberOfBoxes);
	    row++;
	    
	    label=new JLabel(Messages.getString("LibraryPanel.22")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    usedArea1=new JTextField("0"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(usedArea1,c);
	    fields.add(usedArea1);	
	    row++;
	    
	    label=new JLabel(Messages.getString("LibraryPanel.24")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    usedArea2=new JTextField("0"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(usedArea2,c);
	    fields.add(usedArea2);		    
	    row++;
	    	    	       
	    label=new JLabel(Messages.getString("LibraryPanel.26")); //$NON-NLS-1$
	    totalWeight=new JTextField("0"); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(totalWeight,c);
	    fields.add(totalWeight);
	    row++;
	    
	
	    label=new JLabel(Messages.getString("LibraryPanel.28")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    weightFront=new JTextField("0"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(weightFront,c);
	    fields.add(weightFront);	
	    row++;

	    label=new JLabel(Messages.getString("LibraryPanel.30")); //$NON-NLS-1$
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    weightLeft=new JTextField("0"); //$NON-NLS-1$
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx=0;
	    c.gridy=row;
	    values.add(label,c);
	    c.gridx=1;
	    c.fill=GridBagConstraints.NONE;
	    values.add(weightLeft,c);
	    fields.add(weightLeft);	
	    row++;
	    
	    	    
	    for(JTextField input:fields) {
	    		input.setPreferredSize(new Dimension(100,(font.getSize()*2)));
	    		input.setEditable(false);
	    		input.setHorizontalAlignment(SwingConstants.RIGHT);
	    }
	    

	    row++;
	    c.fill = GridBagConstraints.BOTH;
	    c.gridx=0;
	    c.gridy=row;
	    c.weightx=1;
	    c.weighty=1;
	    c.gridwidth=2;
	    values.add(new JLabel(),c);
	    
	    infoPanel.add(line1);
	    infoPanel.add(line2);
	    infoPanel.add(new JLabel(Messages.getString("LibraryPanel.32"))); //$NON-NLS-1$
	    infoPanel.add(values);
	    
	    updateInfo(0,0,0,0,0);
    }
    
    JTextField numberOfBoxes;
    JTextField usedArea1;
    JTextField usedArea2;
    JTextField totalWeight;
    JTextField weightFront;
    JTextField weightLeft;
    JLabel dateInfo;
    JLabel authorInfo;
    JLabel documentName;
    
    
    public void updateInfo(int boxes, double usedArea, double weight, double frontWeight, double leftWeight) {
		String textBoxes=preferences.getIntegerFormatter().format(boxes);
		String textUsedArea1=preferences.getDecimalFormatter().format(usedArea);

		double areaSize=(((double)documentFrame.documentTruckSize.width*documentFrame.documentTruckSize.height)/1000000.0);
		String textUsedArea2=preferences.getDecimalFormatter().format(usedArea*100.0/areaSize);
		String textTotalWeight=preferences.getDecimalFormatter().format(weight);
		double percentageFront=frontWeight*100.0/weight;
		double percentageLeft=leftWeight*100.0/weight;
		String textWeightFront=preferences.getDecimalFormatter().format(percentageFront);
		String textWeightLeft=preferences.getDecimalFormatter().format(percentageLeft);
		
		numberOfBoxes.setText(textBoxes);
	    usedArea1.setText(textUsedArea1);
	    usedArea2.setText(textUsedArea2);
	    totalWeight.setText(textTotalWeight);
	    weightFront.setText(weight==0 ? "" : textWeightFront); //$NON-NLS-1$
	    weightLeft.setText(weight==0 ? "" : textWeightLeft); //$NON-NLS-1$
	    
	    if(weight==0) {
	    		percentageFront=50;
	    		percentageLeft=50;
	    }
    		documentFrame.panelWorkplace.sliderX.setValue(100-(int)percentageFront);
    		documentFrame.panelWorkplace.sliderY.setValue(100-(int)percentageLeft);
    		
    		dateInfo.setText(preferences.getDateFormatter().format(documentFrame.documentDate));
    		String author=documentFrame.documentAuthor;
    		if(author!=null && author.trim().length()>0) author+=", "; //$NON-NLS-1$
    	    authorInfo.setText(author);
    	    documentName.setText(documentFrame.documentName);	
    }
    
    public void compute() {
    		documentFrame.panelWorkplace.computeOptimization();
    }

    public void destroyLiraryTable() {
    		panelBottom.remove(libraryScrollPane);
    		libraryTable=null;
    		libraryScrollPane=null;
    		libraryTableSorter=null;
    }
}
