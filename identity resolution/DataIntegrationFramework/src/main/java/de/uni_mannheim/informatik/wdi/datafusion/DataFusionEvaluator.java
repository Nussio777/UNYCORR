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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusionEvaluationResult;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Triple;

/**
 * Evaluates a data fusion result based on a given {@link DataFusionStrategy}
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 * @param <RecordType>
 */
public class DataFusionEvaluator<RecordType extends Matchable & Fusable> {

	private DataFusionStrategy<RecordType> strategy;
	private boolean verbose = false;

	/**
	 * Returns whether additional information will be written to the console
	 * 
	 * @return
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * Sets whether additional information will be written to the console
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Creates a new instance with the provided strategy
	 * 
	 * @param strategy
	 */
	public DataFusionEvaluator(DataFusionStrategy<RecordType> strategy) {
		this.strategy = strategy;
	}

	public FusionEvaluationResult<RecordType> calculateFusionResult(DataSet<RecordType> dataset,
			DataSet<RecordType> goldStandard) {

		int correctValues = 0;
		int totalValues = goldStandard.getSize() * strategy.getEvaluationRules().size();
		HashMap<String, Integer> attributeCount = new HashMap<String, Integer>();
		for (String attribute : strategy.getEvaluationRules().keySet()) {
			attributeCount.put(attribute, 0);
		}

		List<Triple<String, RecordType, RecordType>> errors = new ArrayList<>();

		for (RecordType record : goldStandard.getRecords()) {
			RecordType fused = dataset.getRecord(record.getIdentifier());

			if (fused != null) {
				for (String attribute : strategy.getEvaluationRules().keySet()) {
					EvaluationRule<RecordType> r = strategy.getEvaluationRules().get(attribute);
					AttributeFuser<RecordType> f = strategy.getAttributeFusers().get(attribute);

					if ((!f.hasValue(record) && !f.hasValue(fused)) // neither
																	// gs nor
																	// fused
																	// record
																	// has a
																	// value
							|| (f.hasValue(record) && f.hasValue(fused) && r.isEqual(fused, record))) { // both
																										// have
																										// a
																										// value
																										// and
																										// it's
																										// the
																										// same
																										// value
						correctValues++;
						attributeCount.put(attribute, attributeCount.get(attribute) + 1);
					} else {
						errors.add(new Triple<String, RecordType, RecordType>(r.getClass().getSimpleName(), fused,
								record));
						if (verbose) {
							System.out.println(String.format("[error] %s: %s <> %s", r.getClass().getSimpleName(),
									fused.toString(), record.toString()));
						}
					}
				}
			}
		}
		double oacc = (double) correctValues / (double) totalValues;
		HashMap<String, Double> attributeSpec = new HashMap<>();
		for (String attribute : strategy.getEvaluationRules().keySet()) {
			attributeSpec.put(attribute, (double) attributeCount.get(attribute) / (double) goldStandard.getSize());
		}
		FusionEvaluationResult<RecordType> result = new FusionEvaluationResult<>(oacc, attributeSpec, errors);
		if (verbose) {
			result.printPerformance();
		}

		return result;
	}

	/**
	 * Evaluates the the data fusion result against a gold standard
	 * 
	 * @param dataset
	 * @param goldStandard
	 * @return the accuracy of the data fusion result
	 */
	public double evaluate(DataSet<RecordType> dataset, DataSet<RecordType> goldStandard) {

		return calculateFusionResult(dataset, goldStandard).getAccuracy();
	}
}
