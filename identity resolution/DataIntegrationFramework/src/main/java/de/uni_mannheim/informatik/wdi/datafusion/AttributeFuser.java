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

import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;

/**
 * Abstract super class for all Fusers used by a fusion strategy
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 * @param <RecordType>
 */
public abstract class AttributeFuser<RecordType extends Matchable & Fusable> {

	/**
	 * fuses the group of records and assigns values to the fused Record
	 * @param group the group of values to be fused (input)
	 * @param fusedRecord the fused record (output)
	 */
	public abstract void fuse(RecordGroup<RecordType> group, RecordType fusedRecord);	
	
	/**
	 * Determines whether the record has a value for the attribute that is used by this fuser. Required for the collection of fusable values.
	 * @param record
	 * @return
	 */
	public abstract boolean hasValue(RecordType record);
	
	/**
	 * Determines if the given group of records has conflicting values
	 * @param group
	 * @param rule
	 * @return
	 */
	public abstract boolean hasConflictingValues(RecordGroup<RecordType> group, EvaluationRule<RecordType> rule);
}
