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
import java.util.Iterator;
import java.util.List;

import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.Performance;

/**
 * Evaluates a set of {@link Correspondence}s against a
 * {@link MatchingGoldStandard}.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Simon Geisler (simon-geisler@t-online.de)
 * 
 * @param <RecordType>
 */
public class BlockingEvaluator<RecordType extends Matchable> {

	private boolean verbose = false;
	private BlockingKeyGenerator<RecordType> blockingFunction = null;

	public BlockingEvaluator() {
	}

	public BlockingEvaluator(BlockingKeyGenerator<RecordType> blockingFunction, boolean isVerbose) {
		this.blockingFunction = blockingFunction;
		verbose = isVerbose;
	}

	/**
	 * Evaluates the blocking against the gold standard
	 * 
	 * @param blockingPairs
	 *            the blocking pairs to evaluate
	 * @param goldStandard
	 *            the gold standard
	 * @return the result of the evaluation
	 */
	public BlockingPerformance evaluateBlocking(
			List<Pair<RecordType, RecordType>> blockingPairs,
			MatchingGoldStandard goldStandard) {
		int correct = 0;
		int matched = 0;
		int correct_max = goldStandard.getPositiveExamples().size();

		// keep a list of all unmatched positives for later output
		List<Pair<String, String>> positives = new ArrayList<>(goldStandard.getPositiveExamples());

		for (Pair<RecordType, RecordType> pair : blockingPairs) {
			if (goldStandard.containsPositive(pair.getFirst(), pair.getSecond())) {
				correct++;
				matched++;

				if (verbose) {
					System.out.println(String
							.format("[correct] %s:%s,%s:%s", pair.getFirst().getIdentifier(),
									blockingFunction.getBlockingKey(pair.getFirst()),
									pair.getSecond().getIdentifier(), 
									blockingFunction.getBlockingKey(pair.getSecond())));

					// remove pair from positives
					Iterator<Pair<String, String>> it = positives.iterator();
					while (it.hasNext()) {
						Pair<String, String> p = it.next();
						String id1 = pair.getFirst().getIdentifier();
						String id2 = pair.getSecond().getIdentifier();

						if (p.getFirst().equals(id1)
								&& p.getSecond().equals(id2)
								|| p.getFirst().equals(id2)
								&& p.getSecond().equals(id1)) {
							it.remove();
						}
					}
				}
			} else if (goldStandard.containsNegative(pair.getFirst(), pair.getSecond())) {
				matched++;

				if (verbose) {
					System.out.println(String
							.format("[correct] %s:%s,%s:%s", pair.getFirst().getIdentifier(),
									blockingFunction.getBlockingKey(pair.getFirst()),
									pair.getSecond().getIdentifier(), 
									blockingFunction.getBlockingKey(pair.getSecond())));
				}
			}
		}

		if (verbose) {
			// print all missing positive examples
			for (Pair<String, String> p : positives) {
				System.out.println(String.format("[missing] %s,%s",
						p.getFirst(), p.getSecond()));
			}
		}

		return new BlockingPerformance(correct, matched, correct_max);
	}

}
