/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/WorkplacePanel.java $
 * $Id: WorkplacePanel.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.StyledDocument;

import org.saiko.loadorganizer.engine.Container;
import org.saiko.loadorganizer.engine.ContainerPosition;
import org.saiko.loadorganizer.engine.GeneticEngine;

/**
 * Workplace panel and its funcionality
 */
public class WorkplacePanel extends JScrollPane {
	
	
	private static final long serialVersionUID = 1L;

	DocumentFrame documentFrame;
	JPanel workplace;

	/** all components on the workplace **/
	List<ContainerComponent> workplaceComponents = new ArrayList<ContainerComponent>();

	/** components, which are currently selected **/
	List<ContainerComponent> selectedComponents = new ArrayList<ContainerComponent>();

	/** last mouse click for dragging **/
	Point lastClick;

	/** selector - empty JLabel with order only shown when selecting items with mouse **/
	JLabel selector = null;

	/** truck panel **/
	TruckPanel truck = null;

	/** components that show the load distribution grapgically **/
	JSlider sliderY;
	JSlider sliderX;
	
	Preferences preferences;
	
	public WorkplacePanel(DocumentFrame parentFrame) {
		this.documentFrame = parentFrame;
		preferences=documentFrame.loadOrganizer.preferences;

		workplace = new JPanel();

		getVerticalScrollBar().setUnitIncrement(preferences.getGuiPixelsPerMeter() / 4);
		getVerticalScrollBar()
				.setBlockIncrement(preferences.getGuiPixelsPerMeter() / 4);
		workplace.setOpaque(true);
		workplace.setLayout(new BoxLayout(workplace, BoxLayout.Y_AXIS));
		workplace.setBorder(new WorkplaceBorder(documentFrame.loadOrganizer));

		workplace.setSize(500, 500);

		workplace.setLayout(null);
		getViewport().add(workplace);

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point origin = getViewport().getViewPosition();
				Point point = e.getPoint();
				point.x += origin.x;
				point.y += origin.y;

				int x = lastClick.x;
				int y = lastClick.y;
				int cx = point.x - lastClick.x;
				int cy = point.y - lastClick.y;

				if (cx < 0) {
					cx = -cx;
					x = x - cx;
				}
				if (cy < 0) {
					cy = -cy;
					y = y - cy;
				}

				selector.setLocation(x, y);
				selector.setSize(cx, cy);

			}
		});

		selector = new JLabel();
		selector.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		addPopupMenu();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				popupGettingInvisible=false;
				Point origin = getViewport().getViewPosition();
				Point point = e.getPoint();
				point.x += origin.x;
				point.y += origin.y;

				lastClick = point;
				if(e.getButton()!=1) return;
				workplace.add(selector);
				selector.setLocation(point);
				selector.setSize(0, 0);
				workplace.setComponentZOrder(selector, 0);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(popupGettingInvisible) {
					popupGettingInvisible=false;
					return;
				}
				if(e.getButton()!=1) return; 
				if (e.isShiftDown() || e.isControlDown()) {
					// nop
				} else {
					// clear selection
					switchSelected(null, true, true);
				}
				for (ContainerComponent c : workplaceComponents) {
					if (c.getBounds().intersects(selector.getBounds())) {
						switchSelected(c, false, true);
					}
				}

				lastClick = null;
				workplace.remove(selector);
				workplace.repaint();
			}

		});

		// add the truck
		truck = new TruckPanel();		
		truck.setSize(parentFrame.documentTruckSize.width * preferences.getGuiPixelsPerMeter()
				/ 1000, parentFrame.documentTruckSize.height
				* preferences.getGuiPixelsPerMeter() / 1000);
		truck.setLocation(preferences.getGuiPixelsPerMeter()/2,
				preferences.getGuiPixelsPerMeter()*1/2); 
		workplace.add(truck);	

		UIManager.put("Slider.paintValue", Boolean.FALSE); //$NON-NLS-1$
		sliderX=new JSlider(SwingConstants.HORIZONTAL,0,100,50);
		sliderX.setSize(truck.getWidth(),sliderX.getPreferredSize().height);
		sliderX.setLocation(truck.getX(),truck.getY()-sliderX.getHeight());
		sliderX.setToolTipText(Messages.getString("WorkplacePanel.1")); //$NON-NLS-1$
		workplace.add(sliderX);

		sliderY=new JSlider(SwingConstants.VERTICAL,0,100,50);
		sliderY.setSize(sliderY.getPreferredSize().width,truck.getHeight());
		sliderY.setLocation(truck.getX()-sliderY.getWidth(),truck.getY());
		sliderY.setToolTipText(Messages.getString("WorkplacePanel.2")); //$NON-NLS-1$
		workplace.add(sliderY);
		sliderX.setOpaque(false);
		
		// do not allow the sliders to be changed by mouse
		sliderX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				computeInfo();
			}
		});
		sliderX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				computeInfo();
			}
		});		
		sliderY.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				computeInfo();
			}
		});
		sliderY.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				computeInfo();
			}
		});		
	}

	/**
	 * add a new container to the workplace
	 * 
	 * @param c
	 */
	public ContainerComponent addContainer(Container c, boolean unselectOthers) {
		// create component
		ContainerComponent component = new ContainerComponent(this,c);
		workplace.add(component);
		workplaceComponents.add(component);
		workplace.setComponentZOrder(component, 0);

		// switch it to selected
		switchSelected(component, unselectOthers, true);

		replaceToWorkplace(component,true);
		return component;
	}

	/**
	 * reposition component to free space at workplace
	 * @param c
	 * @param scroll
	 */
	void replaceToWorkplace(ContainerComponent c, boolean scroll) {
		// compute position
		int x0 = 10;
		int y0 = (int) ((preferences.getGuiPixelsPerMeter()*1.5)+documentFrame.documentTruckSize.height*preferences.getGuiPixelsPerMeter()/1000);
		
		int x, y;
		
		do {
			x = x0;
			y = y0;
			x0 +=preferences.getGuiPixelsPerMeter()/2;
			if(x0>900) {
				x0=10;
				y0+=preferences.getGuiPixelsPerMeter()/2;
			}
		} while (isConflict(c, x, y, null));

		c.setLocation(x, y);
		if(scroll) {
			getViewport().scrollRectToVisible(c.getBounds());
		}

		// check if scrollar should not be resized
		checkScrollBounds();
		repaint();
	}
	
	/**
	 * automatically checks and extends/shrinks the scroll functionality of
	 * workplace window according to the components' size
	 */
	void checkScrollBounds() {
		int x = 0;
		int y = 0;

		Dimension currentSize = workplace.getPreferredSize();

		for (ContainerComponent c : workplaceComponents) {
			Rectangle r = c.getBounds();
			if (x < r.x + r.width)
				x = r.x + r.width;
			if (y < r.y + r.height)
				y = r.y + r.height;
		}
		Dimension maxSize = new Dimension(x, y);
		if (!maxSize.equals(currentSize)) {
			workplace.setPreferredSize(maxSize);
			revalidate();
		}
	}

	/**
	 * switch selected component, on the given component switches the sate of selection
	 * @param c
	 * @param unselectOthers
	 * @param setLibrarySelection
	 */
	void switchSelected(ContainerComponent c, boolean unselectOthers, boolean setLibrarySelection) {
		if (unselectOthers) {
			for (ContainerComponent selectedComponent : selectedComponents) {
				selectedComponent.setSelected(false);
			}
			selectedComponents.clear();
		}
		if (c != null) {
			if(c.getSelected()) {
				selectedComponents.remove(c);
				c.setSelected(false);
			} else {
				selectedComponents.add(c);
				c.setSelected(true);
				c.requestFocus();
			}
		}
		if(setLibrarySelection) refreshLibrarySelection();
	}

	/**
	 * set the component to be selected
	 * @param c
	 * @param unselectOthers
	 * @param setLibrarySelection
	 */
	void setSelected(ContainerComponent c, boolean unselectOthers, boolean setLibrarySelection) {
		if (unselectOthers) {
			for (ContainerComponent selectedComponent : selectedComponents) {
				selectedComponent.setSelected(false);
			}
			selectedComponents.clear();
		}
		if (c != null) {
			selectedComponents.add(c);
			c.setSelected(true);
		}
		if(setLibrarySelection) refreshLibrarySelection();
	}	
	
	/**
	 * set on library selection according to selection in workplace
	 */
	void refreshLibrarySelection() {
		documentFrame.panelLibrary.removeSelection();
		for(ContainerComponent s: selectedComponents) {
			documentFrame.panelLibrary.selectMoreRow(s);
		}		
	}

	/**
	 * checks if the component can be moved to requested location.
	 * 
	 * @param sourceComponent - component, which is dragged
	 * @param requestedX - requested x position
	 * @param requestedY - requested y position
	 */
	void moveSelectedComponents(ContainerComponent sourceComponent,	int requestedX, int requestedY) {


		Rectangle orgRect = sourceComponent.getBounds();
		Rectangle rNew = new Rectangle(requestedX, requestedY, orgRect.width,
				orgRect.height);
		
		// dx is positive vector of motion in the x direction
		int dx = rNew.x - orgRect.x;
		// dy is positive vector of motion in the y direction
		int dy = rNew.y - orgRect.y;

		// signs for motion vectors
		int signx = 1;
		if (dx < 0) { signx = -1;	dx = -dx; }
		
		int signy = 1;
		if (dy < 0) {signy = -1; 	dy = -dy; }
				
		// does this move conflicts with other components ?
		// this check speeds up the algorithm by skipping the next steps,
		// but this check next is also necessary - sometimes there is no path between the two places  
		boolean conflict = false;
		if(conflict==false) {
			for (ContainerComponent selectedComponent : selectedComponents) {
				Rectangle bounds = selectedComponent.getBounds();
				if (isConflict(selectedComponent, bounds.x + signx * dx, bounds.y + signy * dy,	selectedComponents)) {
					conflict = true;
					break;
				}
			}
		}
		
		int dxM=dx;
		int dyM=dy;
		
		// if conflict, find the maximum dx and dy how we can move
		if(conflict) {
			dxM=0;
			dyM=0;

			List<Rectangle> rects=new ArrayList<Rectangle>();
			for (ContainerComponent selectedComponent : selectedComponents) {
				rects.add(selectedComponent.getBounds());
			}

			//move all components in the steps in x direction and check validity
			for(int i=1; i<=dx; i++) {
				for(Rectangle r : rects) {
					r.x+=signx;
				}
				if(!isConflict(rects,selectedComponents)) {
					dxM=i;
				}
			}				
	
			//restore the rectangles
			rects.clear();
			for (ContainerComponent selectedComponent : selectedComponents) {
				Rectangle r=selectedComponent.getBounds();
				//use newly computed X
				r.x = r.x + signx * dxM;				
				rects.add(r);
			}
			
			//move all components in the steps in x direction and check validity
			for(int i=1; i<=dy; i++) {
				for(Rectangle r : rects) {
					r.y+=signy;
				}
				if(!isConflict(rects,selectedComponents)) {
					dyM=i;
				}
			}
		}
		
		// the result position is defined with the size of vector dx and dy
		// does this move conflicts with other components ?
		for (ContainerComponent selectedComponent : selectedComponents) {
			orgRect = selectedComponent.getBounds();
			requestedX = orgRect.x + signx * dxM;				
			requestedY = orgRect.y + signy * dyM;
			selectedComponent.setLocation(requestedX, requestedY);
		}
	}

	boolean notIn(ContainerComponent c,
			List<ContainerComponent> selectedComponents) {
		for (ContainerComponent c2 : selectedComponents) {
			if (c2 == c)
				return false;
		}
		return true;
	}

	/**
	 * Check if position of component conflict with any other
	 * 
	 * @param c
	 * @param requestedX
	 * @param requestedY
	 * @return
	 */
	private boolean isConflict(ContainerComponent c, int requestedX, 	int requestedY, List<ContainerComponent> selectedComponents) {
		
		List<Rectangle> bounds=new ArrayList<Rectangle>();
		bounds.add(new Rectangle(requestedX, requestedY, c.getWidth(), c.getHeight()));
		
		return isConflict(bounds,selectedComponents);
	}

	/**
	 * Check if the rectangles from the list do conflict with existing
	 * workplace components, excluding the checks for the selectedComponents
	 * Also checks the x and y borders of the workplace as well
	 * as the borders of the truck panel
	 * @param bounds
	 * @param selectedComponents - can be null
	 * @return t
	 */
	private boolean isConflict(List<Rectangle> bounds,  List<ContainerComponent> selectedComponents) {
		for(Rectangle r: bounds) {
			if(r.x<0) return true;
			if(r.y<0) return true;
			
			for (ContainerComponent c2 : workplaceComponents) {			
				if (selectedComponents != null && !notIn(c2, selectedComponents))
					continue;
	
				Rectangle r2 = c2.getBounds();
				if (r2.intersects(r)) {
					return true;
				}			
			}
			Rectangle truckBounds=truck.getBounds();
			if(truckBounds.intersects(r) && !truckBounds.contains(r)) {
				return true;
			}		
		}
		return false;
	}

	/**
	 * checks, if there exist conflict at the workplace
	 * @return
	 */
	private boolean isConflict() {
		for(ContainerComponent c1 : workplaceComponents) {
			Rectangle r=c1.getBounds();
			
			for (ContainerComponent c2 : workplaceComponents) {			
				if (c2==c1) continue;
	
				Rectangle r2 = c2.getBounds();
				if (r2.intersects(r)) {
					return true;
				}			
			}
			Rectangle truckBounds=truck.getBounds();
			if(truckBounds.intersects(r) && !truckBounds.contains(r)) {
				return true;
			}		
		}
		return false;
	}
	
	/**
	 * select all components of the container
	 * 
	 * @param c
	 */
	void selectAll(Container c) {
		for (ContainerComponent component : workplaceComponents) {
			if (component.container == c) {
				selectedComponents.add(component);
				component.setSelected(true);
			}
		}
		repaint();
		//refreshLibrarySelection();
	}
	
	
	/** select all components from workplace **/
	void selectAllInWorkplace() {
		switchSelected(null,true,false);
		for (ContainerComponent component : workplaceComponents) {
				selectedComponents.add(component);
				component.setSelected(true);
		}
		repaint();
	}

	JPopupMenu popup;
	boolean popupGettingInvisible=false;
	
	void addPopupMenu() {
		popup=documentFrame.menuEdit.buildPopup();

		popup.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				/*nop*/
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				popupGettingInvisible=true;
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				/*nop*/
			}
			
		});
		
		this.addMouseListener(new MouseAdapter() {
	        @Override
			public void mousePressed(MouseEvent evt) {
	        		popupGettingInvisible=false;
	            if (evt.isPopupTrigger()) {
	            		popup.show(evt.getComponent(), evt.getX(), evt.getY());
	            }
	        }
	        @Override
			public void mouseReleased(MouseEvent evt) {
	            if (evt.isPopupTrigger()) {
	            		popup.show(evt.getComponent(), evt.getX(), evt.getY());
	            }
	        }
	    });
	}
	
	void deleteSelected() {
		for (ContainerComponent selectedComponent : selectedComponents) {
			workplaceComponents.remove(selectedComponent);
			workplace.remove(selectedComponent);
		}
		switchSelected(null,true,true);
		repaint();
		computeInfo();
	}
	
	void rotateSelected() {
		for (ContainerComponent selectedComponent : selectedComponents) {
			selectedComponent.rotate();
		}
		if(isConflict()) {
			for (ContainerComponent selectedComponent : selectedComponents) {
				selectedComponent.rotate();
			}
			JOptionPane.showMessageDialog(documentFrame.loadOrganizer,Messages.getString("WorkplacePanel.3"),Messages.getString("WorkplacePanel.4"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			computeInfo();
		}
	}
	
	/**
	 * checks, if rotation is possible
	 * @return
	 */
	boolean checkRotation() {
		for (ContainerComponent selectedComponent : selectedComponents) {
			selectedComponent.rotate();
		}
		boolean rotation=!isConflict();
		for (ContainerComponent selectedComponent : selectedComponents) {
			selectedComponent.rotate();
		}
		return rotation;
	}	
	
	
	int getUssages(Container item) {
		int ussages=0;
		for (ContainerComponent c : workplaceComponents) {
			if(c.container==item) ussages++;
		}
		return ussages;
	}
	

	/**
	 * compute statistics of the workplace
	 * used area, number of coponents in the truck etc.
	 * @return
	 */
	public DocumentStatistics computeInfo() {
		Rectangle truckBounds=truck.getBounds();
		
		int count=0;
		int totalWeight=0;
		double area=0;
		double weightFront=0;
		double weightLeft=0;
		
		for(ContainerComponent c: workplaceComponents) {
			Rectangle r=c.getBounds();
			if(truckBounds.contains(r)) {
				count++;
				totalWeight+=c.container.getWeight();
				area+=c.container.getSize().width*c.container.getSize().height;
				
				int tx=r.x+r.width/2-truckBounds.x;
				int ty=r.y+r.height/2-truckBounds.y;
				
				double wf=c.container.getWeight()*(truckBounds.width-tx)/truckBounds.width;
				double wl=c.container.getWeight()*(ty)/truckBounds.height;
						
				weightFront+=wf;
				weightLeft+=wl;
				
			}
		}
		area/=1000000.0; //to square meters
		
		documentFrame.panelLibrary.updateInfo(count, area, totalWeight, weightFront, weightLeft);
		return new DocumentStatistics(count, area, totalWeight, weightFront, weightLeft);
	}
	
	/**
	 * set selected containers mandatory or remove the mandatory flag rom them
	 */
	void switchMandatoryForSelected() {
		boolean mandatoryAll=true;
		for(ContainerComponent c:selectedComponents) {
			if(!c.mandatory) {
				mandatoryAll=false;
				break;
			}
		}
		boolean mandatory=!mandatoryAll;
		for(ContainerComponent c:selectedComponents) {
			c.mandatory=mandatory;
		}
		repaint();
	}
	
	/**
	 * start computing of the optimization
	 */
	void computeOptimization() {

		try {
			boolean truckClear=true;
			final Rectangle truckBounds=truck.getBounds();
			List<Container> mandatoryList=new ArrayList<Container>();
			List<Container> warehouseList=new ArrayList<Container>();
	
			final Map<Container, ContainerComponent> componentMapping = new HashMap<Container, ContainerComponent>();
			
			for(ContainerComponent c: workplaceComponents) {
				if(truckBounds.contains(c.getBounds())) {
					truckClear=false;
				}
				Container container=(Container)c.container.clone();
				componentMapping.put(container,c);
				if(c.mandatory) {
					mandatoryList.add(container);
				} else {
					warehouseList.add(container);
				}
			}
			//check some components on the workplace
			if(workplaceComponents.size()==0) {
				JOptionPane.showMessageDialog(documentFrame.loadOrganizer,Messages.getString("WorkplacePanel.5"),Messages.getString("WorkplacePanel.6"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			//truck is not clear - ask
			if(!truckClear && JOptionPane.showConfirmDialog(this.documentFrame.loadOrganizer,Messages.getString("WorkplacePanel.7"),Messages.getString("WorkplacePanel.8"),JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION ) { //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			
			//empty the truck - reorganize all containers to the workplace
			if(!truckClear) {
				switchSelected(null,true,true);
				for(ContainerComponent c: workplaceComponents) {
					if(truckBounds.contains(c.getBounds())) {
						replaceToWorkplace(c,false);
					}
				}
			}

		
			final GeneticEngine engine=new GeneticEngine(
					this,
					documentFrame.documentTruckSize.width,
					documentFrame.documentTruckSize.height,
					documentFrame.documentTruckTonnage,
					warehouseList,
					mandatoryList
			);
			
			showInfoDialog(engine);
			
			new Thread() {
				@Override
				public void run() {
					try {
							startTime=System.currentTimeMillis();
							List<ContainerPosition> result=engine.compute();
							
							//result contains relative coordinates of containers in mm
							for(ContainerPosition p: result) {
								//find the workplace component and set it's position and rotation
								ContainerComponent c=componentMapping.get(p.getContainer());
								Rectangle r=c.getBounds();
								if(c.rotated) {
									c.setSize(r.height,r.width);
									c.rotated=false;
								}
								r=c.getBounds();
								c.rotated=p.isRotated();
								int x=(int)(truckBounds.getX()+p.getX()*preferences.getGuiPixelsPerMeter()/1000);
								int y=(int)(truckBounds.getY()+p.getY()*preferences.getGuiPixelsPerMeter()/1000);
								c.setLocation(x,y);
								if(c.rotated) {
									c.setSize(r.height,r.width);
								}
							}
							repaint();
							computeInfo();
					}catch(Throwable e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(documentFrame.loadOrganizer,Messages.getString("WorkplacePanel.11")+e.toString(),Messages.getString("WorkplacePanel.12"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
						progressWrite(e.toString());
						progressFinish();
					} 
				}
			}.start();
		} catch(Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(documentFrame.loadOrganizer,Messages.getString("WorkplacePanel.13")+e.toString(),Messages.getString("WorkplacePanel.14"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * progress writing funcionalities
	 */
	JTextPane progressInfoBoard=null;
	final static Object lock = new Object();
	JDialog progressDialog;
	JScrollPane progressInfoBoardScroll;
	volatile long startTime;
	
	public void progressWrite(String line) {
		progressWrite(line,true, 3);
	}

	public void progressWrite(String line, boolean appendTime, int retry) {
		synchronized (lock) {
			Thread.interrupted();
			if(progressInfoBoard!=null) {
				if(appendTime) {
					long t=System.currentTimeMillis()-startTime;
					t/=1000;
					String m=String.valueOf(t/60);
					if(m.length()<2) m="0"+m; //$NON-NLS-1$
					String s=String.valueOf(t%60);
					if(s.length()<2) s="0"+s; //$NON-NLS-1$
					
					line=Messages.getString("WorkplacePanel.17")+m+":"+s+" "+line; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				StyledDocument doc=progressInfoBoard.getStyledDocument();
				try {
					if(doc.getLength()>1024*16) {
						doc.remove(0,1024*8);
					}
					doc.insertString(doc.getLength(),line+"\n",null); //$NON-NLS-1$
					progressInfoBoard.setCaretPosition(doc.getLength());
				} catch(Throwable e) {
					if(retry>0) {
						try { Thread.sleep(10); } catch(Throwable ee) {/*nop*/}
						progressWrite(line,false, retry-1);
					} else {
						e.printStackTrace();
						System.out.println(line);
					}
				}
			}
		}
	}
	
	public void progressFinish() {
		synchronized (lock) {
			if(progressInfoBoard!=null) {
				progressWrite(""); //$NON-NLS-1$
				progressWrite(""); //$NON-NLS-1$
				btnCloseComputationInfo.setEnabled(true);
				btnStopComputation.setEnabled(false);
				btnCloseComputationInfo.requestFocus();
				progressDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		}
		System.gc();
		System.runFinalization();
	}
	
	JButton btnCloseComputationInfo;
	JButton btnStopComputation;
	
	/**
	 * opens info dialog for the text output during computation
	 * @param engine
	 */
	void showInfoDialog(final GeneticEngine engine) {
		new Thread() {
			@Override
			public void run() {
				progressDialog=new JDialog(documentFrame.loadOrganizer,true);
				progressDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				progressDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
							progressInfoBoard=null;
					}
				});
				
				progressDialog.getContentPane().setLayout(new BorderLayout());
				progressDialog.setTitle(Messages.getString("WorkplacePanel.23")); //$NON-NLS-1$
				progressInfoBoard=new JTextPane();
				progressInfoBoard.setPreferredSize(new Dimension(640,320));
				progressInfoBoard.setEditable(false);
				progressInfoBoardScroll=new JScrollPane(progressInfoBoard);
				progressDialog.getContentPane().add(progressInfoBoardScroll,BorderLayout.CENTER);
				
				JPanel toolbar=new JPanel();
				toolbar.setLayout(new BoxLayout(toolbar,BoxLayout.LINE_AXIS));
				
				progressDialog.getContentPane().add(toolbar,BorderLayout.SOUTH);

				btnCloseComputationInfo=new JButton(Messages.getString("WorkplacePanel.24")); //$NON-NLS-1$
				btnCloseComputationInfo.setEnabled(false);
				btnCloseComputationInfo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						progressDialog.dispose();
						progressInfoBoard=null;
					}
				});

				btnStopComputation=new JButton(Messages.getString("WorkplacePanel.25")); //$NON-NLS-1$
				btnStopComputation.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						engine.stop();
					}
				});

				toolbar.add(btnStopComputation,BorderLayout.SOUTH);
				toolbar.add(btnCloseComputationInfo,BorderLayout.SOUTH);
				
				progressDialog.pack();
				progressDialog.setLocationRelativeTo(documentFrame.loadOrganizer);
				progressDialog.setVisible(true);
			}
		}.start();
		
	}
	
	public Preferences getPreferences() {
		return preferences;
	}	
}
