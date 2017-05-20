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
package de.uni_mannheim.informatik.wdi.usecase.movies.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import de.uni_mannheim.informatik.wdi.model.Record;

/**
 * A {@link Record} representing a movie.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class Movie extends Record {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	public Movie(String identifier, String provenance) {
		super(identifier, provenance);
		actors = new LinkedList<>();
	}

	private String title;
	private String director;
	private DateTime date;
	private List<Actor> actors;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	public static final String TITLE = "Title";
	public static final String DIRECTOR = "Director";
	public static final String DATE = "Date";
	public static final String ACTORS = "Actors";

	private Map<String, Collection<String>> provenance = new HashMap<>();

	public void setRecordProvenance(Collection<String> provenance) {
		this.provenance.put("RECORD", provenance);
	}

	public Collection<String> getRecordProvenance() {
		return provenance.get("RECORD");
	}

	public void setAttributeProvenance(String attribute, Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(String attribute) {
		return provenance.get(attribute);
	}

	public String getMergedAttributeProvenance(String attribute) {
		Collection<String> prov = provenance.get(attribute);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}

	@Override
	public Collection<String> getAttributeNames() {
		return Arrays.asList(new String[] { TITLE, DIRECTOR, DATE, ACTORS });
	}

	@Override
	public boolean hasValue(String attributeName) {
		switch (attributeName) {
		case TITLE:
			return getTitle() != null && !getTitle().isEmpty();
		case DIRECTOR:
			return getDirector() != null && !getDirector().isEmpty();
		case DATE:
			return getDate() != null;
		case ACTORS:
			return getActors() != null && getActors().size() > 0;
		default:
			return false;
		}
	}

	// Solely translates the actors into a human readable string (names of
	// actors)
	private String actorsAsList() {
		String actors = "";
		for (Actor a : getActors()) {
			if (actors.length() > 1) {
				actors += "|";
			}
			actors += a.getName();
		}

		return actors;
	}

	@Override
	public String toString() {
		return String.format("[Movie: %s / Director: %s / Date: %s / Actor(s): %s]", getTitle(), getDirector(), getDate().toString(),
				actorsAsList());
	}
}
