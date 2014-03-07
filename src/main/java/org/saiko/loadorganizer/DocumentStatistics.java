/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/DocumentStatistics.java $
 * $Id: DocumentStatistics.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

/**
 * small class with the statistics of the document workplace
 */
public class DocumentStatistics {
	int count;
	int totalWeight=0;
	double area=0;
	double weightFront=0;
	double weightLeft=0;
	
	public DocumentStatistics( int count, double area, int weight, double front, double left) {
		super();
		// TODO Auto-generated constructor stub
		this.area = area;
		this.count = count;
		totalWeight = weight;
		weightFront = front;
		weightLeft = left;
	}
	
	
}