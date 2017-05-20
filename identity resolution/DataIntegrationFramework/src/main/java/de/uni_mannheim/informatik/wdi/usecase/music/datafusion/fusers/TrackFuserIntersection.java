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

import java.util.List;

import de.uni_mannheim.informatik.wdi.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.list.Intersection;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Track;

/**
 * {@link AttributeValueFuser} for the tracks of {@link Release}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class TrackFuserIntersection extends AttributeValueFuser<List<Track>, Release> {

	public TrackFuserIntersection() {
		super(new Intersection<Track, Release>());
	}

	@Override
	public boolean hasValue(Release record) {
		return record.hasValue(Release.TRACKS);
	}

	@Override
	protected List<Track> getValue(Release record) {
		return record.getTracks();
	}

	@Override
	public void fuse(RecordGroup<Release> group, Release fusedRecord) {
		FusedValue<List<Track>, Release> fused = getFusedValue(group);
		fusedRecord.setTracks(fused.getValue());
		fusedRecord.setAttributeProvenance(Release.TRACKS, fused.getOriginalIds());
	}

}
