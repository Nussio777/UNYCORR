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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Pair;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;

/**
 * Abstract super class for all Fusers tailored to specific attributes (hence the ValueType)
 * @author Oliver Lehmberg (oli@dwslab.de)
 *
 * @param <ValueType>
 * @param <RecordType>
 */
public abstract class AttributeValueFuser<ValueType, RecordType extends Matchable & Fusable> extends AttributeFuser<RecordType> {

	/**
	 * Collects all fusable values from the group of records
	 * @param group
	 * @return
	 */
	protected List<FusableValue<ValueType, RecordType>> getFusableValues(RecordGroup<RecordType> group) {
		List<FusableValue<ValueType, RecordType>> values = new LinkedList<>();
		
		for(Pair<RecordType, FusableDataSet<RecordType>> p : group.getRecordsWithDataSets()) {
			// only consider existing values
			if(hasValue(p.getFirst())) {
				ValueType v = getValue(p.getFirst());
				FusableValue<ValueType, RecordType> value = new FusableValue<ValueType, RecordType>(v, p.getFirst(), p.getSecond());
				values.add(value);
			}
		}
		
		return values;
	}
	
	/**
	 * returns the value that is used by this fuser from the given record. Required for the collection of fusable values.
	 * @param record
	 * @return
	 */
	protected abstract ValueType getValue(RecordType record);
	
	@Override
	public boolean hasConflictingValues(RecordGroup<RecordType> group,
			EvaluationRule<RecordType> rule) {
		
		List<RecordType> records = new ArrayList<>(group.getRecords());

		// remove non-existing values
		Iterator<RecordType> it = records.iterator();
		while(it.hasNext()) {
			if(!hasValue(it.next())) {
				it.remove();
			}
		}
		
		for(int i=0; i<records.size();i++) {
			RecordType r1 = records.get(i);
			for(int j=i+1; j<records.size(); j++) {
				RecordType r2 = records.get(j);
				
				if(!rule.isEqual(r1, r2)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private ConflictResolutionFunction<ValueType, RecordType> conflictResolution;
	
	/**
	 * Creates an instance, specifies the conflic resolution function to use
	 * @param conflictResolution
	 */
	public AttributeValueFuser(ConflictResolutionFunction<ValueType, RecordType> conflictResolution) {
		this.conflictResolution = conflictResolution;
	}

	/**
	 * Returns the fused value by applying the conflict resolution function to the list of fusable values
	 * @param group
	 * @return
	 */
	protected FusedValue<ValueType, RecordType> getFusedValue(RecordGroup<RecordType> group) {
		return conflictResolution.resolveConflict(getFusableValues(group));
	}

}
