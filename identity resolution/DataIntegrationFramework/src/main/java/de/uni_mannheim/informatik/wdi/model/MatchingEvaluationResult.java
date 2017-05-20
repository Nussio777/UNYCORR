/**
* 
* Copyright (C) 2015 Data and Web Science Group, University of Mannheim (code@dwslab.de)
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
package de.uni_mannheim.informatik.wdi.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.wdi.identityresolution.Correspondence;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class MatchingEvaluationResult<RecordType extends Matchable> {

	private Performance performance;
	private HashSet<Correspondence<RecordType>> correctMatches;
	private HashSet<Correspondence<RecordType>> wrongMatches;
	private HashSet<Correspondence<RecordType>> notContained;

	public MatchingEvaluationResult(Performance perf, HashSet<Correspondence<RecordType>> correctMatches,
			HashSet<Correspondence<RecordType>> wrongMatches, HashSet<Correspondence<RecordType>> notContained) {
		this.performance = perf;
		this.correctMatches = correctMatches;
		this.wrongMatches = wrongMatches;
		this.notContained = notContained;
	}

	/**
	 * @return the performance
	 */
	public Performance getPerformance() {
		return performance;
	}

	/**
	 * @return the correctMatches
	 */
	public HashSet<Correspondence<RecordType>> getCorrectMatches() {
		return correctMatches;
	}

	/**
	 * @return the wrongMatches
	 */
	public HashSet<Correspondence<RecordType>> getWrongMatches() {
		return wrongMatches;
	}

	public void writeToCSV(File file, CSVFormatter<RecordType> formatter) throws IOException {

		CSVWriter writer = new CSVWriter(new FileWriter(file));

		String[] typeHeader = { "MatchingResult" };
		String[] correct = { "correct" };
		String[] wrong = { "wrong" };
		String[] not = { "notcontained" };

		int lineCounter = 0;
		// write correct
		for (Correspondence<RecordType> correspondence : correctMatches) {
			if (lineCounter == 0) {
				// write header
				writer.writeNext(ArrayUtils.addAll(typeHeader,
						ArrayUtils.addAll(formatter.getHeader(correspondence.getFirstRecord()),
								formatter.getHeader(correspondence.getSecondRecord()))));
			}
			writer.writeNext((String[]) ArrayUtils.addAll(correct,
					ArrayUtils.addAll(formatter.format(correspondence.getFirstRecord()),
							formatter.format(correspondence.getSecondRecord()))));
			lineCounter++;
		}

		// write wrong
		for (Correspondence<RecordType> correspondence : wrongMatches) {
			if (lineCounter == 0) {
				// write header
				writer.writeNext(
						(String[]) ArrayUtils.addAll(typeHeader, formatter.getHeader(correspondence.getFirstRecord()),
								formatter.getHeader(correspondence.getSecondRecord())));
			}
			writer.writeNext((String[]) ArrayUtils.addAll(wrong,
					ArrayUtils.addAll(formatter.format(correspondence.getFirstRecord()),
							formatter.format(correspondence.getSecondRecord()))));
			lineCounter++;
		}

		// write wrong
		for (Correspondence<RecordType> correspondence : notContained) {
			if (lineCounter == 0) {
				// write header
				writer.writeNext(
						(String[]) ArrayUtils.addAll(typeHeader, formatter.getHeader(correspondence.getFirstRecord()),
								formatter.getHeader(correspondence.getSecondRecord())));
			}
			writer.writeNext((String[]) ArrayUtils.addAll(not,
					ArrayUtils.addAll(formatter.format(correspondence.getFirstRecord()),
							formatter.format(correspondence.getSecondRecord()))));
			lineCounter++;
		}

		writer.close();
		System.out.println(String.format("[%s] Writting %d correspondences to file.",
				new DateTime(System.currentTimeMillis()).toString(), lineCounter));

	}

}
