/** 
 *
 * Copyright (C) 2015 Data and Web Science Group, University of Mannheim, Germany (code@dwslab.de)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.uni_mannheim.informatik.wdi.identityresolution.blocking;

import java.util.List;

import de.uni_mannheim.informatik.wdi.identityresolution.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.Record;

/**
 * The super class for all blocking strategies. The generation of {@link Pair}s
 * based on the {@link Blocker} can be executed for one {@link DataSet}, where
 * it is used to determ the candidate pairs for duplicate detection of for two
 * {@link DataSet}s, where it is used to determ the candidate pairs for identity
 * resolution.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 * @param <RecordType>
 */
public abstract class Blocker<RecordType extends Matchable> {

	private double reductionRatio = 1.0;

	/**
	 * Returns the reduction ratio of the last blocking operation. Only
	 * available after calculatePerformance(...) has been called.
	 * 
	 * @return the reduction ratio
	 */
	public double getReductionRatio() {
		return reductionRatio;
	}

	/**
	 * Generates the pairs of {@link Record}s between two {@link DataSet}s that
	 * should be compared according to this blocking strategy.
	 * 
	 * @param dataset1
	 *            the first data set
	 * @param dataset2
	 *            the second data set
	 * @param symmetric
	 *            states if the similarity matrix is symmetric or not.
	 * @return the list of pairs that resulted from the blocking
	 */
	public abstract List<Pair<RecordType, RecordType>> generatePairs(DataSet<RecordType> dataset1,
			DataSet<RecordType> dataset2);

	/**
	 * Generates the pairs of {@link Record}s within a {@link DataSet} that
	 * should be compared according to this blocking strategy.
	 * 
	 * @param dataset
	 *            the dataset including the {@link Record}s which should be
	 *            compared.
	 * @param isSymmetric
	 *            states if it can be assumed that the later comparison of a and
	 *            b is equal to the comparison of b and a. In most cases (using
	 *            most {@link SimilarityMeasure}) this will be true.
	 * @return
	 */
	public abstract List<Pair<RecordType, RecordType>> generatePairs(DataSet<RecordType> dataset, boolean isSymmetric);

	/**
	 * Calculates the reduction ratio. Must be called by all sub classes in
	 * generatePairs(...).
	 * 
	 * @param dataset1
	 *            the first data set
	 * @param dataset2
	 *            the second data set
	 * @param blockedPairs
	 *            the list of pairs that resulted from the blocking
	 */
	protected void calculatePerformance(DataSet<RecordType> dataset1, DataSet<RecordType> dataset2,
			List<Pair<RecordType, RecordType>> blockedPairs) {
		long maxPairs = (long) dataset1.getSize() * (long) dataset2.getSize();

		// reductionRatio = (double) maxPairs / (double) blockedPairs.size();

		reductionRatio = 1.0 - ((double) blockedPairs.size() / (double) maxPairs);
	}
}
