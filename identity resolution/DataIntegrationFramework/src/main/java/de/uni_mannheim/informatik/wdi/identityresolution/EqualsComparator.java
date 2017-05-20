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

import de.uni_mannheim.informatik.wdi.model.DefaultRecord;

/**
 * A comparator that check two attributes for equality
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class EqualsComparator extends Comparator<DefaultRecord> {

	private String attributeName;

	public EqualsComparator(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public double compare(DefaultRecord record1, DefaultRecord record2) {
		return record1.getValue(attributeName).equals(
				record2.getValue(attributeName)) ? 1.0 : 0.0;
	}

}
