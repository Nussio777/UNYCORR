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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.Record;

/**
 * Implementation of the Sorted-Neighbourhood {@link Blocker}, which based on
 * the blocking key of the {@link BlockingKeyGenerator} compares only the
 * surrounding {@link Record}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 * @param <RecordType>
 */
public class SortedNeighbourhoodBlocker<RecordType extends Matchable> extends
		Blocker<RecordType> {

	private BlockingKeyGenerator<RecordType> blockingFunction;
	private int windowSize;

	public SortedNeighbourhoodBlocker(
			BlockingKeyGenerator<RecordType> blockingFunction, int windowSize) {
		this.blockingFunction = blockingFunction;
		this.windowSize = windowSize;
	}

	@Override
	public List<Pair<RecordType, RecordType>> generatePairs(
			DataSet<RecordType> dataset, boolean isSymmetric) {
		List<Pair<RecordType, RecordType>> result = new LinkedList<>();

		// add all instances to one list, and compute the keys
		ArrayList<Pair<String, RecordType>> keyIdentifierList = new ArrayList<Pair<String, RecordType>>();
		for (RecordType record : dataset.getRecords()) {
			keyIdentifierList.add(new Pair<String, RecordType>(blockingFunction
					.getBlockingKey(record), record));
		}
		// sort the list by the keys
		Comparator<Pair<String, RecordType>> pairComparator = new Comparator<Pair<String, RecordType>>() {

			@Override
			public int compare(Pair<String, RecordType> o1,
					Pair<String, RecordType> o2) {
				return o1.getFirst().compareTo(o2.getFirst());
			}

		};
		Collections.sort(keyIdentifierList, pairComparator);
		if (isSymmetric) {
			for (int i = 0; i < keyIdentifierList.size() - 1; i++) {
				for (int j = i + 1; ((j - i) < windowSize)
						&& (j < keyIdentifierList.size()); j++) {
					result.add(new Pair<RecordType, RecordType>(
							keyIdentifierList.get(i).getSecond(),
							keyIdentifierList.get(j).getSecond()));
				}
			}
		} else {
			for (int i = 0; i < keyIdentifierList.size() - 1; i++) {
				for (int j = Math.max(0, i - windowSize + 1); ((j - i) < windowSize)
						&& (j < keyIdentifierList.size()); j++) {
					result.add(new Pair<RecordType, RecordType>(
							keyIdentifierList.get(i).getSecond(),
							keyIdentifierList.get(j).getSecond()));
				}
			}
		}

		calculatePerformance(dataset, dataset, result);
		return result;
	}

	@Override
	public List<Pair<RecordType, RecordType>> generatePairs(
			DataSet<RecordType> dataset1, DataSet<RecordType> dataset2) {
		List<Pair<RecordType, RecordType>> result = new LinkedList<>();

		// add all instances to one list, and compute the keys
		ArrayList<Pair<String, RecordType>> keyIdentifierList = new ArrayList<Pair<String, RecordType>>();
		for (RecordType record : dataset1.getRecords()) {
			keyIdentifierList.add(new Pair<String, RecordType>(blockingFunction
					.getBlockingKey(record), record));
		}

		for (RecordType record : dataset2.getRecords()) {
			keyIdentifierList.add(new Pair<String, RecordType>(blockingFunction
					.getBlockingKey(record), record));
		}
		// sort the list by the keys
		Comparator<Pair<String, RecordType>> pairComparator = new Comparator<Pair<String, RecordType>>() {

			@Override
			public int compare(Pair<String, RecordType> o1,
					Pair<String, RecordType> o2) {
				return o1.getFirst().compareTo(o2.getFirst());
			}

		};
		Collections.sort(keyIdentifierList, pairComparator);

		for (int i = 0; i < keyIdentifierList.size() - 1; i++) {
			RecordType r1 = keyIdentifierList.get(i).getSecond();
			int counter = 1;
			int j = i;
			while ((counter < windowSize)
					&& (j < (keyIdentifierList.size() - 1))) {
				RecordType r2 = keyIdentifierList.get(++j).getSecond();
				// check if they belong *not* to the same dataset
				if (!r2.getProvenance().equals(r1.getProvenance())) {
					result.add(new Pair<RecordType, RecordType>(r1, r2));
					counter++;
				}
			}
		}

		calculatePerformance(dataset1, dataset2, result);
		return result;
	}
}
