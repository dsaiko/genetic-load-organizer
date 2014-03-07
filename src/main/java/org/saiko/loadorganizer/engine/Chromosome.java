/**
 * $LastChangedDate: 2006-05-11 18:19:18 +0200 (čt, 11 V 2006) $
 * $LastChangedRevision: 14 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/engine/Chromosome.java $
 * $Id: Chromosome.java 14 2006-05-11 16:19:18Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer.engine;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Chromosome definition.
 * Chromosome is represented by list of positioned containers
 */
public class Chromosome  {

	private List<ContainerPosition> containers;

	/**
	 * bounds of the truck
	 */
	private Rectangle truckBounds;

	/**
	 * informational flags
	 */
	private boolean validChromosome;
	private int containerCount;
	private int totalWeight;
	private double weightFront;
	private double weightLeft;
	private double usedArea;
	

	private List<ContainerPosition> firstHalfContainers=new ArrayList<ContainerPosition>();
	private List<ContainerPosition> secondHalfContainers=new ArrayList<ContainerPosition>();
	private double firstHalfUsedArea=0;
	private double secondHalfUsedArea=0;

	private List<ContainerPosition> topHalfContainers=new ArrayList<ContainerPosition>();
	private List<ContainerPosition> bottomHalfContainers=new ArrayList<ContainerPosition>();
	private double topHalfUsedArea=0;
	private double bottomHalfUsedArea=0;
	
	private Point loadMin=new Point();
	private Point loadMax=new Point();
	private double occupiedSpace=0;
	
	public Chromosome(GeneticEngine engine) {
		this.containers=new ArrayList<ContainerPosition>();
		truckBounds=new Rectangle(0,0,engine.truckSizeX, engine.truckSizeY);
	}

	/**
	 * returns the fitness value of this chromosome
	 * takes into account the total used area 
	 * and the distribution of the weights
	 * Bigger returned value means better chromosome
	 * @return
	 */
	public double getFittnessValue() {
		if(totalWeight==0) return 0;
		return usedArea+(weightFront/totalWeight)*100+(1-Math.abs(0.5-weightLeft/totalWeight))*100;
	}

	/**
	 * Tries to add conatiner to chromosome on the specified position.
	 * If the position is occupied, funtion returns the conflicting rectangle bounds
	 * @param c
	 * @return conflicting rectangle bounds, null if sucessfull
	 */
	protected Rectangle addContainer(ContainerPosition c) {
		return computeChromosomeStats(c);
	}

	/**
	 * go through the chromosome and compute its statistics.
	 * if newContainer is requested, it tries to add it.
	 * returns rectangle of conflicting container, null if sucesfull
	 * @param newContainer
	 * @return
	 */
	protected Rectangle computeChromosomeStats(ContainerPosition newContainer) {
		Rectangle rNew=null;

		boolean canAddNew=true;
		Rectangle intersection=null;
		
		if(newContainer!=null) {
			rNew=newContainer.getBounds();
			if(!truckBounds.contains(rNew)) {
				return truckBounds;
			}
		}
			
		totalWeight=0;
		weightFront=0;
		weightLeft=0;
		usedArea=0;
		validChromosome=true;
		containerCount=containers.size();
		
		firstHalfContainers.clear();
		secondHalfContainers.clear();
		firstHalfUsedArea=0;
		secondHalfUsedArea=0;

		topHalfContainers.clear();
		bottomHalfContainers.clear();
		topHalfUsedArea=0;
		bottomHalfUsedArea=0;
		
		loadMin.x=truckBounds.width;
		loadMin.y=truckBounds.height;
		loadMax.x=0;
		loadMax.y=0;
		occupiedSpace=0;
		
		for(ContainerPosition c: containers) {
			Rectangle r=c.getBounds();
			
			if(newContainer!=null && canAddNew) {
				if(r.intersects(newContainer.getBounds())) {
						canAddNew=false;
						intersection=r;
				}
			}

			if(r.x<loadMin.x) loadMin.x=r.x;
			if(r.y<loadMin.y) loadMin.y=r.y;
			if(r.x+r.width>loadMax.x) loadMax.x=r.x+r.width;
			if(r.y+r.height>loadMax.y) loadMax.y=r.y+r.height;
			
			totalWeight+=c.getContainer().getWeight();
			double boxArea=r.width*r.height;
			usedArea+=boxArea;
				
			int tx=r.x+r.width/2-truckBounds.x;
			int ty=r.y+r.height/2-truckBounds.y;
				
			double wf=c.getContainer().getWeight()*(truckBounds.width-tx)/truckBounds.width;
			double wl=c.getContainer().getWeight()*(ty)/truckBounds.height;
						
			weightFront+=wf;
			weightLeft+=wl;
			
			if(tx<truckBounds.width/2) {
				firstHalfContainers.add(c);
				firstHalfUsedArea+=boxArea;
			} else {
				secondHalfContainers.add(c);
				secondHalfUsedArea+=boxArea;
			}
			
			if(ty<truckBounds.height/2) {
				topHalfContainers.add(c);
				topHalfUsedArea+=boxArea;
			} else {
				bottomHalfContainers.add(c);
				bottomHalfUsedArea+=boxArea;
			}			
		}

		//add the new container to the statistics
		//todo: why this is so duplicated ?
		if(newContainer!=null && canAddNew) {
			containers.add(newContainer);
			totalWeight+=newContainer.getContainer().getWeight();
			double boxArea=rNew.width*rNew.height;
			usedArea+=boxArea;
				
			int tx=rNew.x+rNew.width/2-truckBounds.x;
			int ty=rNew.y+rNew.height/2-truckBounds.y;
				
			double wf=newContainer.getContainer().getWeight()*(truckBounds.width-tx)/truckBounds.width;
			double wl=newContainer.getContainer().getWeight()*(ty)/truckBounds.height;
						
			weightFront+=wf;
			weightLeft+=wl;
			
			if(tx<truckBounds.width/2) {
				firstHalfContainers.add(newContainer);
				firstHalfUsedArea+=boxArea;
			} else {
				secondHalfContainers.add(newContainer);
				secondHalfUsedArea+=boxArea;
			}
			
			if(ty<truckBounds.height/2) {
				topHalfContainers.add(newContainer);
				topHalfUsedArea+=boxArea;
			} else {
				bottomHalfContainers.add(newContainer);
				bottomHalfUsedArea+=boxArea;
			}		
			
			if(rNew.x<loadMin.x) loadMin.x=rNew.x;
			if(rNew.y<loadMin.y) loadMin.y=rNew.y;
			if(rNew.x+rNew.width>loadMax.x) loadMax.x=rNew.x+rNew.width;
			if(rNew.y+rNew.height>loadMax.y) loadMax.y=rNew.y+rNew.height;			
		}
		
		occupiedSpace=(loadMax.x-loadMin.x)*(loadMax.y-loadMin.y);

		return intersection;
	}


	public int getContainerCount() {
		return containerCount;
	}


	public int getTotalWeight() {
		return totalWeight;
	}


	public double getUsedArea() {
		return usedArea;
	}


	public boolean isValidChromosome() {
		return validChromosome;
	}


	public double getWeightFront() {
		return weightFront;
	}


	public double getWeightLeft() {
		return weightLeft;
	}

	public List<ContainerPosition> getContainerPositions() {
		return containers;
	}

	public double getOccupiedSpace() {
		return occupiedSpace;
	}
		
	protected double getFirstHalfUsedArea() {
		return firstHalfUsedArea;
	}

	protected List<ContainerPosition> getSecondHalfContainers() {
		return secondHalfContainers;
	}

	protected List<ContainerPosition> getFirstHalfContainers() {
		return firstHalfContainers;
	}

	protected double getSecondHalfUsedArea() {
		return secondHalfUsedArea;
	}
	
	protected double getTopHalfUsedArea() {
		return topHalfUsedArea;
	}

	protected List<ContainerPosition> getBottomHalfContainers() {
		return bottomHalfContainers;
	}

	protected List<ContainerPosition> getTopHalfContainers() {
		return topHalfContainers;
	}

	protected double getBottomHalfUsedArea() {
		return bottomHalfUsedArea;
	}
}
