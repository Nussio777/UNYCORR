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
package de.uni_mannheim.informatik.wdi.usecase.music.model;

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
 * A {@link Record} representing a release.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Release extends Record {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	private static final long serialVersionUID = 1L;

	public Release(String identifier, String provenance) {
		super(identifier, provenance);
		tracks = new LinkedList<>();
	}

	private String name;
	private String artist;
	private DateTime date;
	private String country;
	private Integer duration;
	private String label;
	private String genre;
	private List<Track> tracks;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre
	 *            the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public static final String NAME = "Name";
	public static final String ARTIST = "artist";
	public static final String DATE = "Date";
	public static final String COUNTRY = "Country";
	public static final String LABEL = "Label";
	public static final String GENRE = "Genre";
	public static final String DURATION = "Duration";
	public static final String TRACKS = "Tracks";

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
		return Arrays.asList(new String[] { NAME, ARTIST, DATE, COUNTRY, LABEL, GENRE, DURATION, TRACKS });
	}

	@Override
	public boolean hasValue(String attributeName) {
		switch (attributeName) {
		case NAME:
			return getName() != null && !getName().isEmpty();
		case ARTIST:
			return getArtist() != null && !getArtist().isEmpty();
		case DATE:
			return getDate() != null;
		case COUNTRY:
			return getCountry() != null && !getCountry().isEmpty();
		case LABEL:
			return getLabel() != null && !getLabel().isEmpty();
		case GENRE:
			return getGenre() != null && !getGenre().isEmpty();
		case DURATION:
			return getDuration() != null && getDuration() > 0;
		case TRACKS:
			return getTracks() != null && getTracks().size() > 0;
		default:
			return false;
		}
	}

	// Solely translates the actors into a human readable string (names of
	// actors)
	protected String tracksAsList() {
		String tracks = "";
		for (Track track : getTracks()) {
			if (tracks.length() > 1) {
				tracks += "|";
			}
			tracks += track.getName();
		}
		return tracks;
	}

	@Override
	public String toString() {
		return String.format(
				"[Name: %s / Artist: %s / Country: %s / Date: %s / Label: %s / Genre: %s / Duration: %s / Track(s): %s]",
				getName(), getArtist(), getCountry(), getDate().toString(), getLabel(), getGenre(), getDuration(),
				tracksAsList());
	}
}
