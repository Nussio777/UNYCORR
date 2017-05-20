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

import de.uni_mannheim.informatik.wdi.model.Record;

/**
 * Represent a correspondence. Contains two {@link Record}s and their similarity
 * score.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 * @param <RecordType>
 */
public class Correspondence<RecordType> {

	private RecordType firstRecord;
	private RecordType secondRecord;
	private double similarityScore;

	/**
	 * returns the first record
	 * 
	 * @return
	 */
	public RecordType getFirstRecord() {
		return firstRecord;
	}

	/**
	 * sets the first record
	 * 
	 * @param firstRecord
	 */
	public void setFirstRecord(RecordType firstRecord) {
		this.firstRecord = firstRecord;
	}

	/**
	 * returns the second record
	 * 
	 * @return
	 */
	public RecordType getSecondRecord() {
		return secondRecord;
	}

	/**
	 * sets the second record
	 * 
	 * @param secondRecord
	 */
	public void setSecondRecord(RecordType secondRecord) {
		this.secondRecord = secondRecord;
	}

	/**
	 * returns the similarity score
	 * 
	 * @return
	 */
	public double getSimilarityScore() {
		return similarityScore;
	}

	/**
	 * sets the similarity score
	 * 
	 * @param similarityScore
	 */
	public void setsimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public Correspondence(RecordType first, RecordType second,
			double similarityScore) {
		firstRecord = first;
		secondRecord = second;
		this.similarityScore = similarityScore;
	}
}
