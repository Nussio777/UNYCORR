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
package de.uni_mannheim.informatik.wdi.usecase.companies.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.wdi.model.FusableFactory;
import de.uni_mannheim.informatik.wdi.model.MatchableFactory;
import de.uni_mannheim.informatik.wdi.model.RecordGroup;

/**
 * A {@link MatchableFactory} for {@link Company}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyFactory extends MatchableFactory<Company> implements FusableFactory<Company> {

	@Override
	public Company createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Company company = new Company(id, provenanceInfo);

		// fill the attributes
		company.setName(getValueFromChildElement(node, "name"));
		company.setCountry(getValueFromChildElement(node, "country"));
		company.setCity(getValueFromChildElement(node, "city"));
		company.setIndustry(getValueFromChildElement(node, "industry"));

		try {
			String assetString = getValueFromChildElement(node, "assets");
			if (assetString != null && assetString.length() > 0) {
				company.setAssets(Double.parseDouble(assetString));
			}

		} catch (NumberFormatException ne) {
			ne.printStackTrace();
		}
		try {
			String revenueString = getValueFromChildElement(node, "revenue");
			if (revenueString != null && revenueString.length() > 0) {
				company.setRevenue(Double.parseDouble(revenueString));
			}
		} catch (NumberFormatException ne) {
			ne.printStackTrace();
		}
		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "founded");
			if (date != null && !date.isEmpty()) {
				DateTime dt = DateTime.parse(date);
				company.setFounded(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// load the list of actors
		List<String> keyPersons = getListFromChildElement(node, "keypeople");
		company.setKeyPersons(keyPersons);

		return company;
	}

	@Override
	public Company createInstanceForFusion(RecordGroup<Company> cluster) {

		List<String> ids = new LinkedList<>();

		for (Company m : cluster.getRecords()) {
			ids.add(m.getIdentifier());
		}

		Collections.sort(ids);

		String mergedId = StringUtils.join(ids, '+');

		return new Company(mergedId, "fused");
	}

}
