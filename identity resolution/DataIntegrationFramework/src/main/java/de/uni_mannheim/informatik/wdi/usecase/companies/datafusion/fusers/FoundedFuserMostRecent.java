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
package de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.fusers;

import org.joda.time.DateTime;

import de.uni_mannheim.informatik.wdi.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;

/**
 * {@link AttributeValueFuser} for the founded of {@link Company}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class FoundedFuserMostRecent extends AttributeValueFuser<DateTime, Company> {

	public FoundedFuserMostRecent() {
		super(new MostRecent<DateTime, Company>());
	}

	@Override
	public boolean hasValue(Company record) {
		return record.hasValue(Company.FOUNDED);
	}

	@Override
	protected DateTime getValue(Company record) {
		return record.getFounded();
	}

	@Override
	public void fuse(RecordGroup<Company> group, Company fusedRecord) {
		FusedValue<DateTime, Company> fused = getFusedValue(group);
		fusedRecord.setFounded(fused.getValue());
		fusedRecord.setAttributeProvenance(Company.FOUNDED, fused.getOriginalIds());
	}

}
