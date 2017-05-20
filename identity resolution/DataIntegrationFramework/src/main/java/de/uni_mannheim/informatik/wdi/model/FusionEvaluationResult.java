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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class FusionEvaluationResult<RecordType extends Matchable> {

	private double accuracy;
	private HashMap<String, Double> attributeSpecificAccuracy;
	private List<Triple<String, RecordType, RecordType>> errors;

	public double getAccuracy() {
		return accuracy;
	}

	public void printPerformance() {
		System.out.println(String.format("Overall Accuracy: %.2f", accuracy));
		System.out.println("Attribute-specific Accuracy:");
		for (String attribute : attributeSpecificAccuracy.keySet()) {
			System.out.println(String.format("	%s: %.2f", attribute, attributeSpecificAccuracy.get(attribute)));

		}
	}

	public FusionEvaluationResult(double accuracy, HashMap<String, Double> attributeSpecificAccuracy,
			List<Triple<String, RecordType, RecordType>> errors) {
		this.accuracy = accuracy;
		this.attributeSpecificAccuracy = attributeSpecificAccuracy;
		this.errors = errors;
	}

	public void writeToCSV(File file, CSVFormatter<RecordType> formatter) throws IOException {

		CSVWriter writer = new CSVWriter(new FileWriter(file));

		String[] typeHeader = { "ErrorType" };

		int lineCounter = 0;
		// write correct
		for (Triple<String, RecordType, RecordType> triple : errors) {
			if (lineCounter == 0) {
				// write header
				writer.writeNext(ArrayUtils.addAll(typeHeader, ArrayUtils
						.addAll(formatter.getHeader(triple.getSecond()), formatter.getHeader(triple.getThird()))));
			}
			String[] errorType = { triple.getFirst() };
			writer.writeNext((String[]) ArrayUtils.addAll(errorType,
					ArrayUtils.addAll(formatter.format(triple.getSecond()), formatter.format(triple.getThird()))));
			lineCounter++;
		}
		writer.close();

	}

}
