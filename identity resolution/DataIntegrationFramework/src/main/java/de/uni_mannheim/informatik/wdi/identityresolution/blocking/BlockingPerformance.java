package de.uni_mannheim.informatik.wdi.identityresolution.blocking;

import de.uni_mannheim.informatik.wdi.model.Performance;

public class BlockingPerformance extends Performance{

	public BlockingPerformance(int correct, int created, int correct_total) {
		super(correct, created, correct_total);
	}
	
	public double getPairCompleteness() {
		return getRecall();
	}
	
	public double getPairQuality() {
		return getPrecision();
	}
}
