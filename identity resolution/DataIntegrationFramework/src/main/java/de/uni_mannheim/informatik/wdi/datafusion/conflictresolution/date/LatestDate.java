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
package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.date;

import java.util.Collection;

import org.joda.time.DateTime;

import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.Matchable;

/**
 * Latest Date {@link ConflictResolutionFunction}: Returns the latest date.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class LatestDate<RecordType extends Matchable & Fusable>
		extends ConflictResolutionFunction<DateTime, RecordType> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.
	 * ConflictResolutionFunction#resolveConflict(java.util.Collection)
	 */
	@Override
	public FusedValue<DateTime, RecordType> resolveConflict(Collection<FusableValue<DateTime, RecordType>> values) {

		DateTime dateTime = null;

		for (FusableValue<DateTime, RecordType> value : values) {
			if (value != null) {
				if (dateTime == null) {
					dateTime = value.getValue();
				} else {
					if (dateTime.isBefore(value.getValue())) {
						dateTime = value.getValue();
					}
				}
			}
		}

		return new FusedValue<>(dateTime);

	}

}
