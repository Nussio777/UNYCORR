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
package de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.fusers;

import java.util.List;

import de.uni_mannheim.informatik.wdi.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.wdi.model.FusedValue;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Actor;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

/**
 * {@link AttributeValueFuser} for the actors of {@link Movie}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ActorsFuserMostRecent extends
		AttributeValueFuser<List<Actor>, Movie> {

	public ActorsFuserMostRecent() {
		super(new MostRecent<List<Actor>, Movie>());
	}

	@Override
	public boolean hasValue(Movie record) {
		return record.hasValue(Movie.ACTORS);
	}

	@Override
	protected List<Actor> getValue(Movie record) {
		return record.getActors();
	}

	@Override
	public void fuse(RecordGroup<Movie> group, Movie fusedRecord) {
		FusedValue<List<Actor>, Movie> fused = getFusedValue(group);
		fusedRecord.setActors(fused.getValue());
		fusedRecord
				.setAttributeProvenance(Movie.ACTORS, fused.getOriginalIds());
	}

}
