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
package de.uni_mannheim.informatik.wdi.usecase.music.datafusion.fusers;

import de.uni_mannheim.informatik.wdi.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.numeric.Maximum;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;

/**
 * {@link AttributeValueFuser} for the duration of {@link Release}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class DurationFuserMax extends AttributeValueFuser<Double, Release> {

	public DurationFuserMax() {
		super(new Maximum<Release>());
	}

	@Override
	public void fuse(RecordGroup<Release> group, Release fusedRecord) {

		// get the fused value
		FusedValue<Double, Release> fused = getFusedValue(group);

		// set the value for the fused record
		if (fused.getValue() != null) {
			fusedRecord.setDuration(fused.getValue().intValue());
		}

		// add provenance info
		fusedRecord.setAttributeProvenance(Release.DURATION, fused.getOriginalIds());
	}

	@Override
	public boolean hasValue(Release record) {
		return record.hasValue(Release.DURATION);
	}

	@Override
	protected Double getValue(Release record) {
		return (double) record.getDuration();
	}

}
