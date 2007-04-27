/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/MenuDocumentEdit.java $
 * $Id: MenuDocumentEdit.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * select, rotate and delete actions for Edit menu and popup menu of the document
 * @author dsaiko
 *
 */
public class MenuDocumentEdit {

	DocumentFrame parentFrame;

	MenuDocumentEdit(DocumentFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

	protected JMenu build() {
		JMenu menu = new JMenu(Messages.getString("MenuDocumentEdit.0")); //$NON-NLS-1$

		JMenuItem delete = new JMenuItem(Messages.getString("MenuDocumentEdit.1")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 1L;

		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)
					return parentFrame.panelWorkplace.selectedComponents.size()>0;
				return false;
			}
		};
		
		
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				deleteSelectedComponents();
			}			
		});		
		
		JMenuItem rotate = new JMenuItem(Messages.getString("MenuDocumentEdit.2")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 1L;

		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)
					return ( parentFrame.panelWorkplace.selectedComponents.size()>0 && parentFrame.panelWorkplace.checkRotation());
				return false;
			}
		};
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotateSelectedComponents();
			}
		});		
		
		JMenuItem selectAll = new JMenuItem(Messages.getString("MenuDocumentEdit.3")); //$NON-NLS-1$

		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.panelWorkplace.selectAllInWorkplace();
			}
		});
		
		JCheckBoxMenuItem mandatory=new JCheckBoxMenuItem(Messages.getString("MenuDocumentEdit.4")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 1L;
		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)				
					return ( parentFrame.panelWorkplace.selectedComponents.size()>0);
				return false;
			}
			@Override
			public boolean isSelected()  {
				if(parentFrame.panelWorkplace==null || parentFrame.panelWorkplace.selectedComponents==null || parentFrame.panelWorkplace.selectedComponents.size()==0) return false;
				boolean mandatoryAll=true;
				for(ContainerComponent c:parentFrame.panelWorkplace.selectedComponents) {
					if(!c.mandatory) {
						mandatoryAll=false;
						break;
					}
				}
				return mandatoryAll;
			}
		};
		mandatory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mandatoryContainers();
			}
		});		
		
		menu.add(mandatory);
		menu.add(rotate);
		menu.add(delete);
		menu.addSeparator();
		menu.add(selectAll);
		return menu;
	}
	

	protected JPopupMenu buildPopup() {
		JPopupMenu menu = new JPopupMenu(Messages.getString("MenuDocumentEdit.5")); //$NON-NLS-1$

		JMenuItem delete = new JMenuItem(Messages.getString("MenuDocumentEdit.6")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 5212800518385832858L;

		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)
					return parentFrame.panelWorkplace.selectedComponents.size()>0;
				return false;
			}
		};
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				deleteSelectedComponents();
			}			
		});			

				
		JMenuItem rotate = new JMenuItem(Messages.getString("MenuDocumentEdit.7")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 1L;

		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)
					return ( parentFrame.panelWorkplace.selectedComponents.size()>0 && parentFrame.panelWorkplace.checkRotation());
				return false;
			}
		};
		rotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotateSelectedComponents();
			}
		});		
		
		JMenuItem selectAll = new JMenuItem(Messages.getString("MenuDocumentEdit.8")); //$NON-NLS-1$

		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.panelWorkplace.selectAllInWorkplace();
			}
		});

		JCheckBoxMenuItem mandatory=new JCheckBoxMenuItem(Messages.getString("MenuDocumentEdit.9")) { /** //$NON-NLS-1$
			 * 
			 */
			private static final long serialVersionUID = 1L;
		//$NON-NLS-1$
			@Override
			public boolean isEnabled() {
				if(parentFrame!=null && parentFrame.panelWorkplace!=null)
					return ( parentFrame.panelWorkplace.selectedComponents.size()>0);
				return false;
			}
			@Override
			public boolean isSelected()  {
				if(parentFrame.panelWorkplace==null || parentFrame.panelWorkplace.selectedComponents==null || parentFrame.panelWorkplace.selectedComponents.size()==0) return false;
				boolean mandatoryAll=true;
				for(ContainerComponent c:parentFrame.panelWorkplace.selectedComponents) {
					if(!c.mandatory) {
						mandatoryAll=false;
						break;
					}
				}
				return mandatoryAll;
			}
		};
		mandatory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mandatoryContainers();
			}
		});
		
		menu.add(mandatory);
		menu.add(rotate);
		menu.add(delete);
		menu.addSeparator();
		menu.add(selectAll);
		return menu;
	}	
	
	
	void deleteSelectedComponents() {
		parentFrame.panelWorkplace.deleteSelected();
	}
	
	
	void rotateSelectedComponents() {
		parentFrame.panelWorkplace.rotateSelected();
	}
	
	void mandatoryContainers() {
		parentFrame.panelWorkplace.switchMandatoryForSelected();
	}
}
