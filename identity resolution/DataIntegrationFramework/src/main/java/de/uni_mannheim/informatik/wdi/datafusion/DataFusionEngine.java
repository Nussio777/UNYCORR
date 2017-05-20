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
package de.uni_mannheim.informatik.wdi.datafusion;

import java.util.HashMap;
import java.util.Map;

import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;

/**
 * Executer class to run the data fusion based on a selected
 * {@link DataFusionStrategy}.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 * @param <RecordType>
 */
public class DataFusionEngine<RecordType extends Matchable & Fusable> {

	private DataFusionStrategy<RecordType> strategy;

	/**
	 * Creates a new instance that uses the specified data fusion strategy.
	 * 
	 * @param strategy
	 */
	public DataFusionEngine(DataFusionStrategy<RecordType> strategy) {
		this.strategy = strategy;
	}

	/**
	 * Runs the data fusion process on the provided set of correspondences
	 * 
	 * @param correspondences
	 * @return a {@link FusableDataSet} based on the RecordType of the
	 *         {@link CorrespondenceSet}
	 */
	public FusableDataSet<RecordType> run(
			CorrespondenceSet<RecordType> correspondences) {
		FusableDataSet<RecordType> fusedDataSet = new FusableDataSet<>();

		for (RecordGroup<RecordType> clu : correspondences.getRecordGroups()) {
			RecordType fusedRecord = strategy.apply(clu);
			fusedDataSet.addRecord(fusedRecord);

			for (RecordType record : clu.getRecords()) {
				fusedDataSet.addOriginalId(fusedRecord, record.getIdentifier());
			}
		}

		return fusedDataSet;
	}

	/**
	 * Calculates the consistencies of the attributes of the records in the
	 * given correspondence set according to the data fusion strategy
	 * 
	 * @param correspondences
	 * @return
	 */
	public Map<String, Double> getAttributeConsistencies(
			CorrespondenceSet<RecordType> correspondences) {
		Map<String, Integer> nonConflictingValues = new HashMap<>();

		for (RecordGroup<RecordType> clu : correspondences.getRecordGroups()) {

			Map<String, Integer> values = strategy
					.getNumberOfNonConflictingValues(clu);

			for (String att : values.keySet()) {
				Integer cnt = nonConflictingValues.get(att);
				if (cnt == null) {
					cnt = 0;
				}
				nonConflictingValues.put(att, cnt + values.get(att));
			}

		}

		Map<String, Double> result = new HashMap<>();
		for (String att : nonConflictingValues.keySet()) {
			double consistency = (double) nonConflictingValues.get(att)
					/ (double) correspondences.getRecordGroups().size();
			result.put(att, consistency);
		}

		return result;
	}

	/**
	 * Calculates the consistencies of the attributes of the records in the
	 * given correspondence set according to the data fusion strategy and prints
	 * the results to the console
	 * 
	 * @param correspondences
	 */
	public void printClusterConsistencyReport(
			CorrespondenceSet<RecordType> correspondences) {
		System.out.println("Attribute Consistencies:");
		Map<String, Double> consistencies = getAttributeConsistencies(correspondences);
		for (String att : consistencies.keySet()) {
			System.out.println(String.format("\t%s: %.2f", att,
					consistencies.get(att)));
		}
	}
}
