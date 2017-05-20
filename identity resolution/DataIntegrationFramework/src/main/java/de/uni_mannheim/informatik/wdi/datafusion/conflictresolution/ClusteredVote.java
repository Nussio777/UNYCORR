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
package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import de.uni_mannheim.informatik.wdi.clustering.CentreClusterer;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Triple;

/**
 * Clustered Vote {@link ConflictResolutionFunction}: Clusters all values and returns the centroid of the largest cluster
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 * @param <ValueType>
 * @param <RecordType>
 */
public class ClusteredVote<ValueType, RecordType extends Matchable & Fusable> extends ConflictResolutionFunction<ValueType, RecordType> {

	private SimilarityMeasure<ValueType> similarityMeasure;
	private double threshold;
	
	public ClusteredVote(SimilarityMeasure<ValueType> similarityMeasure, double threshold) {
		this.similarityMeasure = similarityMeasure;
		this.threshold = threshold;
	}
	
	@Override
	public FusedValue<ValueType, RecordType> resolveConflict(
			Collection<FusableValue<ValueType, RecordType>> values) {
		
		// calculate similarities
		Collection<Triple<FusableValue<ValueType, RecordType>, FusableValue<ValueType, RecordType>, Double>> similarityGraph = new LinkedList<>();
		ArrayList<FusableValue<ValueType, RecordType>> valueList = new ArrayList<>(values);
		for(int i = 0; i < valueList.size(); i++) {
			FusableValue<ValueType, RecordType> v1 = valueList.get(i);
			for(int j = i + 1; j <valueList.size(); j++) {
				FusableValue<ValueType, RecordType> v2 = valueList.get(j);
				
				double similarity = similarityMeasure.calculate(v1.getValue(), v2.getValue());
				
				if(similarity>=threshold) {
					similarityGraph.add(new Triple<>(v1, v2, similarity));
				}
			}
		}
		
		// run clustering
		CentreClusterer<FusableValue<ValueType, RecordType>> clusterer = new CentreClusterer<>();
		Map<FusableValue<ValueType, RecordType>, Collection<FusableValue<ValueType, RecordType>>> clusters = clusterer.cluster(similarityGraph);
		
		// select largest cluster
		FusableValue<ValueType, RecordType> centroid = null;
		Collection<FusableValue<ValueType, RecordType>> largestCluster = null;
		for(FusableValue<ValueType, RecordType> key : clusters.keySet()) {
			Collection<FusableValue<ValueType, RecordType>> clu = clusters.get(key);
			if(largestCluster==null || clu.size()>largestCluster.size()) {
				largestCluster = clu;
				centroid = key;
			}
		}
		
		return new FusedValue<>(centroid);
	}

}
