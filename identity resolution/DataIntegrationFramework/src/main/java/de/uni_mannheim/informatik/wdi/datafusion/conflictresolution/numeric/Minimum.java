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
package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.numeric;

import java.util.Collection;

import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.wdi.model.Fusable;
import de.uni_mannheim.informatik.wdi.model.FusableValue;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.Matchable;

/**
 * Average {@link ConflictResolutionFunction}: Returns the minimum of all values
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 * @param <RecordType>
 */
public class Minimum<RecordType extends Matchable & Fusable> extends ConflictResolutionFunction<Double, RecordType> {

	@Override
	public FusedValue<Double, RecordType> resolveConflict(Collection<FusableValue<Double, RecordType>> values) {

		if (values.size() == 0) {
			return new FusedValue<>((Double) null);
		} else {

			double min = Double.MAX_VALUE;

			for (FusableValue<Double, RecordType> value : values) {
				if (value.getValue() < min) {
					min = value.getValue();
				}
			}

			return new FusedValue<>(min);

		}
	}

}
