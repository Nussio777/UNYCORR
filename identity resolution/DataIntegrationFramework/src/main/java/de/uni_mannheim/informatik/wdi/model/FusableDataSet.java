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
package de.uni_mannheim.informatik.wdi.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

/**
 * {@link DataSet} class extended by functionalities for data fusion
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 * @param <RecordType>
 */
public class FusableDataSet<RecordType extends Matchable & Fusable> extends
		DataSet<RecordType> {

	private double score;
	private DateTime date;

	private Map<String, RecordType> originalIdIndex = new HashMap<>();

	/**
	 * Add an original ID to a fused record (can be called multiple times)
	 * 
	 * @param record
	 * @param id
	 */
	public void addOriginalId(RecordType record, String id) {
		originalIdIndex.put(id, record);
	}

	@Override
	public RecordType getRecord(String identifier) {
		RecordType record = super.getRecord(identifier);

		if (record == null) {
			record = originalIdIndex.get(identifier);
		}

		return record;
	}

	/**
	 * Returns the score of this dataset
	 * 
	 * @return
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Sets the score of this dataset
	 * 
	 * @param score
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Returns the date of this dataset
	 * 
	 * @return
	 */
	public DateTime getDate() {
		return date;
	}

	/**
	 * Sets the date of this dataset
	 * 
	 * @param date
	 */
	public void setDate(DateTime date) {
		this.date = date;
	}

	/**
	 * Calculates the overall density of this dataset
	 * 
	 * @return
	 */
	public double getDensity() {
		int values = 0;
		int attributes = 0;

		for (RecordType record : getRecords()) {
			values += getNumberOfValues(record);
			attributes += getNumberOfAttributes(record);
		}

		return (double) values / (double) attributes;
	}

	/**
	 * Returns the number of attributes that have a value for the given record
	 * 
	 * @param record
	 * @return
	 */
	protected int getNumberOfValues(RecordType record) {
		int cnt = 0;
		for (String att : record.getAttributeNames()) {
			cnt += record.hasValue(att) ? 1 : 0;
		}
		return cnt;
	}

	/**
	 * Returns the number of attributes for the given record
	 * 
	 * @param record
	 * @return
	 */
	protected int getNumberOfAttributes(RecordType record) {
		return record.getAttributeNames().size();
	}

	/**
	 * Calculates the density for all attributes of the records in this dataset
	 * 
	 * @return
	 */
	public Map<String, Double> getAttributeDensities() {
		// counts how often the attribute exists (should be equal to the number
		// of records
		Map<String, Integer> sizes = new HashMap<>();
		// counts how often the attribute has a value
		Map<String, Integer> values = new HashMap<>();

		for (RecordType record : getRecords()) {

			for (String att : record.getAttributeNames()) {

				Integer size = sizes.get(att);
				if (size == null) {
					size = 0;
				}
				sizes.put(att, size + 1);

				if (record.hasValue(att)) {
					Integer value = values.get(att);
					if (value == null) {
						value = 0;
					}
					values.put(att, value + 1);
				}
			}

		}

		Map<String, Double> result = new HashMap<>();

		for (String att : sizes.keySet()) {
			Integer valueCount = values.get(att);
			if (valueCount == null) {
				valueCount = 0;
			}
			double density = (double) valueCount / (double) sizes.get(att);
			result.put(att, density);
		}

		return result;
	}

	/**
	 * Calculates the density for all attributes of the records in this dataset
	 * and prints the result to the console
	 */
	public void printDataSetDensityReport() {
		System.out
				.println(String.format("DataSet density: %.2f", getDensity()));
		System.out.println("Attributes densities:");
		Map<String, Double> densities = getAttributeDensities();
		for (String att : densities.keySet()) {
			System.out.println(String.format("\t%s: %.2f", att,
					densities.get(att)));
		}
	}

}
