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
package de.uni_mannheim.informatik.wdi.identityresolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.string.TokenizingJaccardSimilarity;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.DefaultRecord;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.Record;
import de.uni_mannheim.informatik.wdi.utils.ProgressReporter;

/**
 * The matching engine that executes a given {@link MatchingRule} on one or two
 * {@link DataSet}s. In the first case, duplicate detection is performed. In the
 * second identity resolution is performed.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 * @param <RecordType>
 */
public class MatchingEngine<RecordType extends Matchable> {

	protected MatchingRule<RecordType> rule;
	protected Blocker<RecordType> blocker;
	private List<Pair<RecordType, RecordType>> allPairs = null;

	/**
	 * Creates a matching engine with the specified rule and blocker
	 * 
	 * @param rule
	 * @param blocker
	 */
	public MatchingEngine(MatchingRule<RecordType> rule, Blocker<RecordType> blocker) {
		this.rule = rule;
		this.blocker = blocker;
	}

	public List<Pair<RecordType, RecordType>> getAllPairs() {
		return allPairs;
	}

	/**
	 * Runs the Duplicate Detection on a given {@link DataSet}. In order to
	 * reduce the number of internally compared {@link Record}s the functions
	 * can be executed in a <i>symmetric</i>-mode. Here it will be assumed, that
	 * that the {@link MatchingRule} is symmetric, meaning that the score(a,b) =
	 * score(b,a). Therefore the pair (b,a) can be left out. Normally, this
	 * option can be set to <b>true</b> in most of the cases, as most of the
	 * common similarity functions (e.g. {@link LevenshteinSimilarity}, and
	 * {@link TokenizingJaccardSimilarity}) are symmetric, meaning sim(a,b) =
	 * sim(b,a).
	 * 
	 * @param dataset
	 *            The data set
	 * @param symmetric
	 *            indicates of the used {@link MatchingRule} is symmetric,
	 *            meaning that the order of elements does not matter.
	 * @return A list of correspondences
	 */
	public List<Correspondence<RecordType>> runDuplicateDetection(DataSet<RecordType> dataset, boolean symmetric) {
		long start = System.currentTimeMillis();

		System.out.println(String.format("[%s] Starting Duplicate Detection", new DateTime(start).toString()));

		List<Correspondence<RecordType>> result = new LinkedList<>();

		// use the blocker to generate pairs
		allPairs = blocker.generatePairs(dataset, symmetric);

		System.out.println(
				String.format("Duplicate Detection %,d x %,d elements; %,d blocked pairs (reduction ratio: %.2f)",
						dataset.getSize(), dataset.getSize(), allPairs.size(), blocker.getReductionRatio()));

		// compare the pairs using the Duplicate Detection rule
		ProgressReporter progress = new ProgressReporter(allPairs.size(), "Duplicate Detection");
		for (Pair<RecordType, RecordType> pair : allPairs) {

			// apply the Duplicate Detection rule
			Correspondence<RecordType> cor = rule.apply(pair.getFirst(), pair.getSecond());
			if (cor != null) {

				// add the correspondences to the result
				result.add(cor);
			}

			// increment and report status
			progress.incrementProgress();
			progress.report();
		}

		// report total Duplicate Detection time
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println(String.format("[%s] Duplicate Detection finished after %s; found %,d correspondences.",
				new DateTime(end).toString(), DurationFormatUtils.formatDurationHMS(delta), result.size()));

		return result;
	}

	public List<Correspondence<RecordType>> runMatching(DataSet<RecordType> dataset1, DataSet<RecordType> dataset2) {
		return runMatching(dataset1, dataset2, false);
	}

	/**
	 * Runs the matching on the given data sets
	 * 
	 * @param dataset1
	 *            The first data set
	 * @param dataset2
	 *            The second data set
	 * @return A list of correspondences
	 */
	public List<Correspondence<RecordType>> runMatching(DataSet<RecordType> dataset1, DataSet<RecordType> dataset2,
			boolean applyTop1) {
		long start = System.currentTimeMillis();

		System.out.println(String.format("[%s] Starting Matching", new DateTime(start).toString()));

		List<Correspondence<RecordType>> result = new LinkedList<>();

		// use the blocker to generate pairs
		allPairs = blocker.generatePairs(dataset1, dataset2);

		System.out.println(String.format("Matching %,d x %,d elements; %,d blocked pairs (reduction ratio: %s)",
				dataset1.getSize(), dataset2.getSize(), allPairs.size(), Double.toString(blocker.getReductionRatio())));

		// compare the pairs using the matching rule
		ProgressReporter progress = new ProgressReporter(allPairs.size(), "Matching");
		for (Pair<RecordType, RecordType> pair : allPairs) {

			// apply the matching rule
			Correspondence<RecordType> cor = rule.apply(pair.getFirst(), pair.getSecond());
			if (cor != null) {

				// add the correspondences to the result
				result.add(cor);
			}

			// increment and report status
			progress.incrementProgress();
			progress.report();
		}

		if (applyTop1) {
			result = applyTop1(result);
		}

		// report total matching time
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println(String.format("[%s] Matching finished after %s; found %,d correspondences.",
				new DateTime(end).toString(), DurationFormatUtils.formatDurationHMS(delta), result.size()));

		return result;
	}

	/**
	 * Generates a data set containing features that can be used to learn
	 * matching rules.
	 * 
	 * @param dataset1
	 *            The first data set
	 * @param dataset2
	 *            The second data set
	 * @param goldStandard
	 *            The gold standard containing the labels for the generated data
	 *            set
	 * @return
	 */
	public DataSet<DefaultRecord> generateTrainingDataForLearning(DataSet<RecordType> dataset1,
			DataSet<RecordType> dataset2, MatchingGoldStandard goldStandard) {
		long start = System.currentTimeMillis();

		goldStandard.printBalanceReport();

		System.out.println(String.format("[%s] Starting GenerateFeatures", new DateTime(start).toString()));

		DataSet<DefaultRecord> result = new DataSet<>();

		ProgressReporter progress = new ProgressReporter(
				goldStandard.getPositiveExamples().size() + goldStandard.getNegativeExamples().size(),
				"GenerateFeatures");

		// create positive examples
		for (Pair<String, String> correspondence : goldStandard.getPositiveExamples()) {
			RecordType record1 = dataset1.getRecord(correspondence.getFirst());
			RecordType record2 = dataset2.getRecord(correspondence.getSecond());

			// we don't know which id is from which data set
			if (record1 == null || record2 == null) {
				// so if we didn't find anything, we probably had it wrong ...
				record1 = dataset2.getRecord(correspondence.getFirst());
				record2 = dataset1.getRecord(correspondence.getSecond());
			} else {

				DefaultRecord features = rule.generateFeatures(record1, record2);
				features.setValue("label", "1");
				result.addRecord(features);
			}

			// increment and report status
			progress.incrementProgress();
			progress.report();
		}

		// create negative examples
		for (Pair<String, String> correspondence : goldStandard.getNegativeExamples()) {
			RecordType record1 = dataset1.getRecord(correspondence.getFirst());
			RecordType record2 = dataset2.getRecord(correspondence.getSecond());

			// we don't know which id is from which data set
			if (record1 == null || record2 == null) {
				// so if we didn't find anything, we probably had it wrong ...
				record1 = dataset2.getRecord(correspondence.getFirst());
				record2 = dataset1.getRecord(correspondence.getSecond());
			} else {

				DefaultRecord features = rule.generateFeatures(record1, record2);
				features.setValue("label", "0");
				result.addRecord(features);
			}

			// increment and report status
			progress.incrementProgress();
			progress.report();
		}

		// report total time
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println(String.format("[%s] GenerateFeatures finished after %s; created %,d examples.",
				new DateTime(end).toString(), DurationFormatUtils.formatDurationHMS(delta), result.getSize()));

		return result;
	}

	public void writeCorrespondences(List<Correspondence<RecordType>> correspondences, File file) throws IOException {
		CSVWriter w = new CSVWriter(new FileWriter(file));

		for (Correspondence<RecordType> c : correspondences) {
			w.writeNext(new String[] { c.getFirstRecord().getIdentifier(), c.getSecondRecord().getIdentifier(),
					Double.toString(c.getSimilarityScore()) });
		}

		w.close();
	}

	private List<Correspondence<RecordType>> applyTop1(List<Correspondence<RecordType>> correspondences) {
		System.out.println(String.format("Applying Top1 on %,d correspondences", correspondences.size()));

		List<Correspondence<RecordType>> result = new LinkedList<>();
		// sort the stuff
		Collections.sort(correspondences, new java.util.Comparator<Correspondence<RecordType>>() {

			@Override
			public int compare(Correspondence<RecordType> o1, Correspondence<RecordType> o2) {
				if (o1.getSimilarityScore() != o2.getSimilarityScore()) {
					return Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore());
				} else {
					if (!o1.getFirstRecord().getIdentifier().equals(o2.getFirstRecord().getIdentifier())) {
						return o1.getFirstRecord().getIdentifier().compareTo(o2.getFirstRecord().getIdentifier());
					} else {
						if (!o1.getSecondRecord().getIdentifier().equals(o2.getSecondRecord().getIdentifier())) {
							return o1.getSecondRecord().getIdentifier().compareTo(o2.getSecondRecord().getIdentifier());
						} else {
							return 0;
						}
					}
				}
			}
		});
		ProgressReporter reporter = new ProgressReporter(correspondences.size(), "Applying Top1");
		List<String> recordsFirst = new LinkedList<>();
		List<String> recordsSecond = new LinkedList<>();
		for (Correspondence<RecordType> correspondence : correspondences) {
			// if either the first nor the second record is already in an other
			// correspondence, we can add this.
			if ((!recordsFirst.contains(correspondence.getFirstRecord().getIdentifier()))
					&& (!recordsSecond.contains(correspondence.getSecondRecord().getIdentifier()))) {
				result.add(correspondence);
				recordsFirst.add(correspondence.getFirstRecord().getIdentifier());
				recordsSecond.add(correspondence.getSecondRecord().getIdentifier());
			}
			reporter.incrementProgress();
			reporter.report();
		}

		return result;
	}
}
