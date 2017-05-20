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
package de.uni_mannheim.informatik.wdi.usecase.music.model;

import de.uni_mannheim.informatik.wdi.model.CSVFormatter;

/**
 *  @author Robert Meusel (robert@dwslab.de)
 *
 */
public class ReleaseCSVFormatter extends CSVFormatter<Release> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_mannheim.informatik.wdi.model.CSVFormatter#getHeader(java.lang.
	 * Object)
	 */
	@Override
	public String[] getHeader(Release exampleRecord) {
		String[] headers = { "record-id", "name", "artist", "release-date", "release-country", "duration", "label",
				"genre", "tracks" };
		return headers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_mannheim.informatik.wdi.model.CSVFormatter#format(java.lang.
	 * Object)
	 */
	@Override
	public String[] format(Release record) {
		String[] values = { record.getIdentifier(), record.getName(), record.getArtist(),
				record.getDate() != null ? record.getDate().toString() : null, record.getCountry(),
				record.getDuration() != null ? record.getDuration().toString() : null, record.getLabel(),
				record.getGenre(), record.tracksAsList() };
		return values;
	}

}
