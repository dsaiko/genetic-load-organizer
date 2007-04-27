/**
 * $LastChangedDate: 2006-06-01 13:19:00 +0200 (čt, 01 VI 2006) $
 * $LastChangedRevision: 17 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/engine/GeneticEngine.java $
 * $Id: GeneticEngine.java 17 2006-06-01 11:19:00Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer.engine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.saiko.loadorganizer.Messages;
import org.saiko.loadorganizer.Preferences;
import org.saiko.loadorganizer.WorkplacePanel;


/**
 * Genetic engine class.
 * This class is quite idependent on the GUI classes of the application,
 * except it uses its messaging functionality and preferences.
 */
public class GeneticEngine {

	/**
	 * truck size in mm
	 */
	int truckSizeX;
	/**
	 * truck size in mm
	 */
	int truckSizeY;
	/**
	 * truck tonnage in kg
	 */
	int truckTonnage;
	
	/**
	 * list of available warehouse containers
	 */
	List<Container> warehouseList;
	
	
	/**
	 * list of mandatory containers
	 */
	List<Container> mandatoryList;
	
	/** 
	 * global random wariable 
	 */
	Random rnd=new Random();
	
	
	/**
	 * workplace panel reference
	 * the panel is used for messaging system
	 */
	WorkplacePanel workplacePanel;
	
	/**
	 * reference to preferences
	 */
	Preferences preferences;
	
	int populationSize;
	
	int threadCount;
	

	/**
	 * stop request holder, if .size()>0, computation will stop
	 */
	List<Object> stopRequests=Collections.synchronizedList(new ArrayList<Object>());

	
	/**
	 * @param parent - Workplace to be used for messaging system
	 * @param truckSizeX 
	 * @param truckSizeY
	 * @param truckTonnage
	 * @param warehouseList - list of all warehouse containers, mandatory list will be later removed from it
	 * @param mandatoryList - mandatory entries, have to be also in the warehouse
	 * @throws Exception
	 */
	public GeneticEngine(WorkplacePanel parent, int truckSizeX, int truckSizeY, int truckTonnage, List<Container> warehouseList, List<Container> mandatoryList)
	throws Exception
	{
		workplacePanel=parent;
		preferences=parent.getPreferences();
		populationSize=preferences.getEnginePopulationSize();
		
		this.truckSizeX=truckSizeX;
		this.truckSizeY=truckSizeY;
		this.truckTonnage=truckTonnage;
		threadCount=preferences.getEngineThreadCount();
		
		//copy warehouse and mandatory entries, we will modify them later on, 
		//so we need copies, not just references
		this.warehouseList=new ArrayList<Container>(warehouseList);
		this.mandatoryList=new ArrayList<Container>();
		if(mandatoryList!=null && mandatoryList.size()>0) {
			this.mandatoryList.addAll(mandatoryList);
		}
	}

	
	/**
	 * compute method
	 * method finds and returns the optimalized truck load
	 * with untis in millimeters relative to the truck corner 
	 */
	public List<ContainerPosition> compute() throws Exception {
		final List<Chromosome> population=Collections.synchronizedList(new ArrayList<Chromosome>());
		
		try { Thread.sleep(500); } catch(Throwable e) {/*nop*/} //waiting for console initialization
		
		//welcome console message
		sysout(Messages.getString("GeneticEngine.0")); //$NON-NLS-1$

		//create initial population
		//multi thread logic
		final List<Object> waitList=Collections.synchronizedList(new ArrayList<Object>());
		final Thread computationThread=Thread.currentThread();
		for(int i=0; i<threadCount; i++) {
			new Thread() {
				@Override
				public void run() {
					waitList.add(this);
					while(population.size()<populationSize) {
						population.add(createRandomChromosome(null,null)); //step
					}
					waitList.remove(this);
					computationThread.interrupt();
				}
			}.start();
		}
		do {
			try { Thread.sleep(10000); } catch(InterruptedException e) {}
		} while(waitList.size()>0);
		
		//stable situation counter
		int stableSituationCount=0;
		
		//preview fitness value
		double prevFitness=0;
		
		//generation counter
		int generation=0;
		
		double truckArea=truckSizeX*truckSizeY;
		
		//clear stop requests
		stopRequests.clear();
		
		while(true) {
			//sort population and get the best chromosome
			sortPopulation(population);
			Chromosome c1=population.get(0);
			
			double bestResult=c1.getUsedArea();
			
			double fitness=c1.getFittnessValue();
			
			boolean stable=true;
			
			if(fitness!=prevFitness) {
				stable=false;
				prevFitness=fitness;
			}
			
			if(!stable) {
				stableSituationCount=0;
			} else {
				stableSituationCount++;
			}
			
			String line=Messages.getString("GeneticEngine.1")+(generation+1)+Messages.getString("GeneticEngine.2")+preferences.getDecimalFormatter().format(bestResult*100/truckArea)+Messages.getString("GeneticEngine.3")+population.size()+Messages.getString("GeneticEngine.4")+stableSituationCount; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sysout(line);
			
			if(stableSituationCount>=preferences.getEngineFinishStableCount()) break;
			
			//create new population
			createNewGeneration(generation,population);
			generation++;
			
			//check for stop requests
			if(stopRequests.size()>0) break;
		}

		stopRequests.clear();
		sysout(Messages.getString("GeneticEngine.5")); //$NON-NLS-1$

		if(workplacePanel!=null) {
			workplacePanel.progressFinish();
		}
		
		return population.get(0).getContainerPositions();
	}
	
	
	private void sysout(String line) {
		if(workplacePanel!=null) {
			workplacePanel.progressWrite(line);
		} else {
			System.out.println(line);
		}
	}
	/**
	 * Function to create random chromosome
	 * @return
	 */
	protected Chromosome createRandomChromosome(List<ContainerPosition> existingGens, List<ContainerPosition> warehouse2) {
		
		int step=preferences.getEngineBoxMovementStep();
		
		//we first fill chromosome with mandatory containers
		//so we will add the mandatory containers every time as first, 
		//but thiss will be randomized later on in the evolution process
		ArrayList<Container> mandatory=new ArrayList<Container>(mandatoryList);
		ArrayList<Container> warehouse=new ArrayList<Container>(warehouseList);
		
		//if there are already some existing containers placed, remove them from mandatory list even from warehouse
		if(warehouse2==null && existingGens!=null) {
			for(ContainerPosition existing: existingGens) {
				boolean b1=mandatory.remove(existing.getContainer());
				boolean b2=warehouse.remove(existing.getContainer());
				try {
					if(b1==false && b2==false) {
						throw new Exception(Messages.getString("GeneticEngine.6")); //$NON-NLS-1$
					}
					if(b1==true && b2==true) {
						throw new Exception(Messages.getString("GeneticEngine.7")); //$NON-NLS-1$
					}
				} catch(Throwable e) {
					sysout(e.toString());
					e.printStackTrace();
				}
			}
		}
		if(warehouse2!=null) {
			mandatory.clear();
			warehouse.clear();
			for(ContainerPosition pos: warehouse2) {				
				warehouse.add(pos.getContainer());
			} 
		}
		
		//at least randomize order of mandatory containers first
		int size=mandatory.size();
		for(int i=0; i<size*2; i++) {
			int n=rnd.nextInt(size);
			//remove it from its position
			Container c=mandatory.remove(n);
			//ann add it to the end
			mandatory.add(c);
		}
		
		Chromosome chromosome = new Chromosome(this);

		if(existingGens!=null) {
			chromosome.getContainerPositions().addAll(existingGens);
		}
		
		//now place the mandatory containers, try randomly
		for(int i=0; i<preferences.getEngineRandomizeLoop()*size && mandatory.size()>0; i++) {
			boolean rotated=rnd.nextBoolean();
			int index=rnd.nextInt(mandatory.size());
			Container c=mandatory.get(index);
			if(placeContainer(chromosome,c,rotated,step)) {
				mandatory.remove(index);
			}
		}
		//if some mandatory containers are left, we have to try to place them
		for(int i=mandatory.size()-1; i>=0; i--) {
			Container c=mandatory.get(i);
			boolean placed=false;
			for(int n=0; n<1 && !placed; n++) {
				placed=placeContainer(chromosome,c,false,step);
				if(!placed) placed=placeContainer(chromosome,c,true,step);
			}
			if(placed) { 
				mandatory.remove(i);
			} else {
				//throw new GeneticEngineCantFullfillMandatoryrequestsException();
			}
		}
				
		//all mandatory containers are placed now
		//fill the chromosome with some more components from warehouse
		size=warehouse.size();		
		for(int i=0; i<preferences.getEngineRandomizeLoop()*size && warehouse.size()>0; i++) {
			boolean rotated=rnd.nextBoolean();
			int index=rnd.nextInt(warehouse.size());
			Container c=warehouse.get(index);
			if(placeContainer(chromosome,c,rotated,step)) {
				warehouse.remove(index);
			}
		}		
		
		return chromosome;
	}
	
	private boolean placeContainer(Chromosome chromosome, Container container, boolean rotated, int step) {
		// choose one border of the truck and try to find the space
		int method=rnd.nextInt(8);

		ContainerPosition position=new ContainerPosition(0,0,rotated,container);

		int x=0;
		int y=0;
		int signx=0;
		int signy=0;
		boolean startx=true;
		int x0=0;
		int y0=0;

		Rectangle r=position.getBounds();
		
		switch(method) {
		case 0: x0=x=0; 						y0=y=0; 						signx=1; 	signy=1; 		startx=true; 	break;
		case 1: x0=x=0; 						y0=y=0; 						signx=1; 	signy=1; 		startx=false; 	break;
		case 2: x0=x=0; 						y0=y=truckSizeY-r.height+1; 	signx=1; 	signy=-1; 		startx=false; 	break;
		case 3: x0=x=0; 						y0=y=truckSizeY-r.height+1; 	signx=1; 	signy=-1; 		startx=true; 	break;
		case 4: x0=x=truckSizeX-r.width; 	y0=y=0; 						signx=-1; 	signy=1; 		startx=true; 	break;
		case 5: x0=x=truckSizeX-r.width; 	y0=y=0; 						signx=-1; 	signy=1; 		startx=false; 	break;
		case 6: x0=x=truckSizeX-r.width; 	y0=y=truckSizeY-r.height; 	signx=-1; 	signy=-1; 		startx=false; 	break;
		case 7: x0=x=truckSizeX-r.width; 	y0=y=truckSizeY-r.height; 	signx=-1; 	signy=-1; 		startx=true; 	break;
		}
		while(true) {
			position.setX(x);
			position.setY(y);
			
			Rectangle intersection=chromosome.addContainer(position);
			if(intersection==null) {
				return true;
			}

			if(startx) {
				if(signx>0) {
					x=intersection.x+intersection.width+1;
				} else {
					x=intersection.x-r.width-1;
				}
//				x+=step*signx; 
				if(x>truckSizeX || x<0) {
					if(signx>0) {
						x=0;
					} else {
						x=x0;
					}
					y+=step*signy; 
				} 
				if(y>truckSizeY || y<0) { return false; } 
			} else {
				if(signy>0) {
					y=intersection.y+intersection.height+1;
				} else {
					y=intersection.y-r.height-1;
				}
//				y+=step*signy; 
				if(y>truckSizeY || y<0) {
					if(signy>0) {
						y=0;
					} else {
						y=y0;
					}
					x+=step*signx; 
				} 
				if(x>truckSizeX || x<0) { return false; } 				
			}
		}
	}
	
	/**
	 * sorts the population in the way, that the best chroosomes will be first
	 * @param population
	 */
	private void sortPopulation(List<Chromosome> population) {
		Collections.sort(population,new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) {
				double f1=o1.getFittnessValue();
				double f2=o2.getFittnessValue();
				if(f1>f2) return -1;
				if(f1==f2) return 0;
				return 1;
			}
		});		
	}

	
	static abstract class GenerationModifier  {
		String label;
		int size;
		
		GenerationModifier(String label, int size) {
			this.label=label;
			this.size=size;
			if(size<1) size=1;
		}
		abstract void modify(final List<Chromosome> population);
	}
	
	/**
	 * create new generation from the sorted population
	 * @param population
	 */
	private void createNewGeneration(int generation, final List<Chromosome> population)
	throws Exception
	{
		double ussage1=population.get(0).getUsedArea()*100/(truckSizeX*truckSizeY);
		double fittness1=population.get(0).getFittnessValue();
		double ussage2=0;
		double fittness2=0;
		

		GenerationModifier modifiers[]=new GenerationModifier[] {
			//*********************** add some new random individuals
			new GenerationModifier(Messages.getString("GeneticEngine.8"), (populationSize/10)/threadCount) { //$NON-NLS-1$
				@Override void modify(final List<Chromosome> population) {
					population.add(createRandomChromosome(null,null));
				}
			},
//			*********************** mate some of the individuals
			new GenerationModifier(Messages.getString("GeneticEngine.13"), (populationSize*4/3)/threadCount) { //$NON-NLS-1$
				@Override void modify(final List<Chromosome> population) {
					int parent1=rnd.nextInt(populationSize);
					int parent2=rnd.nextInt(populationSize-1);
					if(parent2==parent1) {
						parent2++;
					}
					population.addAll(mate(population.get(parent1),population.get(parent2)));
				}
			},
//			*********************** optimize some of the individuals
			new GenerationModifier(Messages.getString("GeneticEngine.18"), (populationSize*4/3)/threadCount) { //$NON-NLS-1$
				@Override void modify(final List<Chromosome> population) {
					int parent1=rnd.nextInt(populationSize);
					population.addAll(optimize(population.get(parent1),preferences.getEngineOptimizationDepth()));
				}
			},
//			*********************** randomize some of the individuals
			new GenerationModifier(Messages.getString("GeneticEngine.23"), (populationSize*4/3)/threadCount) { //$NON-NLS-1$
				@Override void modify(final List<Chromosome> population) {
					int parent1=rnd.nextInt(populationSize);
					population.addAll(randomize(population.get(parent1)));
				}
			},			
		};
		
		final List<Object> waitList=Collections.synchronizedList(new ArrayList<Object>());
		final Thread computationThread=Thread.currentThread();


		for(int m=0; m<modifiers.length; m++) {
			final GenerationModifier modifier=modifiers[m];
			Thread.interrupted();
			sysout(modifier.label);
			for(int i=0; i<threadCount; i++) {
				new Thread() {
					@Override
					public void run() {
						waitList.add(this);
						for(int n=0; n<modifier.size; n++) {
							modifier.modify(population);
						}
						waitList.remove(this);
						computationThread.interrupt();
					}
				}.start();
			}
			do {
				try { Thread.sleep(10000); } catch(InterruptedException e) {/*nop*/}
			} while(waitList.size()>0);
			sortPopulation(population);
			ussage2=population.get(0).getUsedArea()*100/(truckSizeX*truckSizeY);
			fittness2=population.get(0).getFittnessValue();
			if(ussage2>ussage1) {
				sysout(Messages.getString("GeneticEngine.9")+preferences.getDecimalFormatter().format(ussage1)+Messages.getString("GeneticEngine.10")+preferences.getDecimalFormatter().format(ussage2)+Messages.getString("GeneticEngine.11")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if(fittness2>fittness1) {
				sysout(Messages.getString("GeneticEngine.12")); //$NON-NLS-1$
			}
			ussage1=ussage2;
			fittness1=fittness2;			
			if(stopRequests.size()>0) return;
		}
			
		populationSize=(int)(preferences.getEnginePopulationSize()*Math.pow((1+preferences.getEnginePopulationGrowth()/100.0),generation));
		
		//if the population has grown up, cut the loosers to remain on the stable population size
		while(population.size()>populationSize) {
			population.remove(population.size()-1);
		}
		
	}
	
	/**
	 * Tries to optimize one chromosome
	 * @param c1 - chromosome to optimise
	 * @param depth - dept of optimization, 2 means optimization of the chromosome even its childern
	 * @return
	 * @throws Exception
	 */
	protected List<Chromosome> optimize(Chromosome c1, int depth) {
		List<Chromosome> childern=new ArrayList<Chromosome>();
		if(stopRequests.size()>0) return childern;

		Chromosome k1, k2, k3;
		
		//get better half and recreate the worse one
		if(c1.getFirstHalfUsedArea()>c1.getSecondHalfUsedArea()) {
			k1=createRandomChromosome(c1.getFirstHalfContainers(),c1.getSecondHalfContainers());
		} else {
			k1=createRandomChromosome(c1.getSecondHalfContainers(),c1.getFirstHalfContainers());
		}
		
		if(c1.getTopHalfUsedArea()>c1.getBottomHalfUsedArea()) {
			k2=createRandomChromosome(c1.getTopHalfContainers(),c1.getBottomHalfContainers());
		} else {
			k2=createRandomChromosome(c1.getBottomHalfContainers(),c1.getTopHalfContainers());
		}		

		//try to optimize the whole truck
		//just try to reorganize existing boxes
		k3=createRandomChromosome(null,c1.getContainerPositions());
		
		childern.add(k1);
		childern.add(k2);
		//childern.add(k3);
		if(depth>0) {
			childern.addAll(optimize(k1,depth-1));
			childern.addAll(optimize(k2,depth-1));
			childern.addAll(optimize(k3,depth-1));
		}
		return childern;
	}
	
	/**
	 * create childern from parents
	 * @param c1
	 * @param c2
	 * @return
	 * @throws Exception
	 */
	protected List<Chromosome> mate(Chromosome c1, Chromosome c2) {
		List<Chromosome> childern=new ArrayList<Chromosome>();
		if(stopRequests.size()>0) return childern;
		
		{
			//select better first half
			List<ContainerPosition> firstHalf = c1.getFirstHalfUsedArea() > c2.getFirstHalfUsedArea() ? c1.getFirstHalfContainers() : c2.getFirstHalfContainers();
			List<ContainerPosition> secondHalf = c1.getSecondHalfUsedArea() > c2.getSecondHalfUsedArea() ? c1.getSecondHalfContainers() : c2.getSecondHalfContainers();
			
			childern.add(createRandomChromosome(firstHalf,null));
			childern.add(createRandomChromosome(secondHalf,null));
		}

		{
			List<ContainerPosition> topHalf = c1.getTopHalfUsedArea() > c2.getTopHalfUsedArea() ? c1.getTopHalfContainers() : c2.getTopHalfContainers();
			List<ContainerPosition> bottomHalf = c1.getBottomHalfUsedArea() > c2.getBottomHalfUsedArea() ? c1.getBottomHalfContainers() : c2.getBottomHalfContainers();
			
				childern.add(createRandomChromosome(topHalf,null));
				childern.add(createRandomChromosome(bottomHalf,null));
		}
		
		childern.addAll(optimize(c1,preferences.getEngineOptimizationDepth()));
		childern.addAll(optimize(c2,preferences.getEngineOptimizationDepth()));
		return childern;
	}
	
	/**
	 * randomize the chromosome
	 * @param c1
	 * @return
	 * @throws Exception
	 */
	protected List<Chromosome> randomize(Chromosome c1){
		List<Chromosome> childern=new ArrayList<Chromosome>();
		
		if(stopRequests.size()>0) return childern;

		//select better first half
		List<ContainerPosition> allItems = new ArrayList<ContainerPosition>(c1.getContainerPositions());
		
		//remove random nmber of boxes and than fill 
		//removes up to one third of containers
		int size=allItems.size()/3;
		if(size==0) size=1;
		size=rnd.nextInt(size)+1;
		for(int i=0; i<size && allItems.size()>0; i++) {
			allItems.remove(rnd.nextInt(allItems.size()));
		}
		childern.add(createRandomChromosome(allItems,null));
		
		return childern;
	}

	/**
	 * set stop request to the que
	 *
	 */
	public void stop() {
		stopRequests.add(new Object());
	}
}
