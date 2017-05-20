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
package de.uni_mannheim.informatik.wdi.identityresolution.similarity.list;

import java.util.List;

import de.uni_mannheim.informatik.wdi.identityresolution.similarity.SimilarityMeasure;

/**
 * {@link SimilarityMeasure} which calculates the overlap similarity between two
 * lists of Object .
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @param <E>
 * 
 */
public class OverlapSimilarity<E> extends SimilarityMeasure<List<E>> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public double calculate(List<E> first, List<E> second) {

		int min = Math.min(first.size(), second.size());
		int matches = 0;

		for (Object s1 : first) {
			for (Object s2 : second) {
				if (s1.equals(s2)) {
					matches++;
					continue;
				}
			}
		}

		return (double) matches / (double) min;
	}

}
