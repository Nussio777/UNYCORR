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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_mannheim.informatik.wdi.model.FusableFactory;
import de.uni_mannheim.informatik.wdi.model.MatchableFactory;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;

/**
 * A {@link MatchableFactory} for {@link Release}s.
 * 
 *  @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseFactory extends MatchableFactory<Release> implements FusableFactory<Release> {

	@Override
	public Release createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Release release = new Release(id, provenanceInfo);

		// fill the attributes
		release.setName(getValueFromChildElement(node, "name").intern());
		release.setArtist(getValueFromChildElement(node, "artist").intern());

		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "release-date");
			if (date != null && !date.isEmpty()) {
				DateTime dt = DateTime.parse(date);
				release.setDate(dt);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
		if (getValueFromChildElement(node, "country") != null
				&& getValueFromChildElement(node, "country").length() > 0) {
			release.setCountry(getValueFromChildElement(node, "country").intern());
		}
		if (getValueFromChildElement(node, "duration") != null
				&& getValueFromChildElement(node, "duration").length() > 0) {
			try {
				release.setDuration(Integer.parseInt(getValueFromChildElement(node, "duration")));
			} catch (Exception e) {
				System.out.println("Could not parse " + getValueFromChildElement(node, "duration")
						+ " to integer from release " + id);
			}
		}
		if (getValueFromChildElement(node, "label") != null && getValueFromChildElement(node, "label").length() > 0) {
			release.setLabel(getValueFromChildElement(node, "label").intern());
		}
		if (getValueFromChildElement(node, "genre") != null && getValueFromChildElement(node, "genre").length() > 0) {
			release.setGenre(getValueFromChildElement(node, "genre").intern());
		}

		Node tracksNode = getChildNode(node, "tracks");
		if (tracksNode != null) {
			List<Track> tracks = new ArrayList<>();
			NodeList trackNodeList = tracksNode.getChildNodes();
			for (int i = 1; i < trackNodeList.getLength(); i++) {
				Track track = new Track();
				Node trackNode = trackNodeList.item(i);
				if (getValueFromChildElement(trackNode, "name") != null
						&& getValueFromChildElement(trackNode, "name").length() > 0) {
					track.setName(getValueFromChildElement(trackNode, "name").intern());
				} else {
					// skip if its empty
					continue;
				}
				if (getValueFromChildElement(trackNode, "duration") != null
						&& getValueFromChildElement(trackNode, "duration").length() > 0) {
					track.setDuration(Integer.parseInt(getValueFromChildElement(trackNode, "duration")));
				}
				if (getValueFromChildElement(trackNode, "position") != null
						&& getValueFromChildElement(trackNode, "position").length() > 0) {
					track.setPosition(Integer.parseInt(getValueFromChildElement(trackNode, "position")));
				}
				tracks.add(track);
			}

			release.setTracks(tracks);
		}

		return release;
	}

	@Override
	public Release createInstanceForFusion(RecordGroup<Release> cluster) {

		List<String> ids = new LinkedList<>();

		for (Release m : cluster.getRecords()) {
			ids.add(m.getIdentifier());
		}

		Collections.sort(ids);

		String mergedId = StringUtils.join(ids, '+');

		return new Release(mergedId, "fused");
	}

}
