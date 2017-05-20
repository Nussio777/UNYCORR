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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.wdi.model.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Company}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyXMLFormatter extends XMLFormatter<Company> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("companies");
	}

	@Override
	public Element createElementFromRecord(Company record, Document doc) {
		Element company = doc.createElement("company");

		company.appendChild(createTextElement("id", record.getIdentifier(), doc));

		company.appendChild(createTextElementWithProvenance("name", record.getName(),
				record.getMergedAttributeProvenance(Company.NAME), doc));
		company.appendChild(createTextElementWithProvenance("country", record.getCountry(),
				record.getMergedAttributeProvenance(Company.COUNTRY), doc));
		company.appendChild(createTextElementWithProvenance("city", record.getCity(),
				record.getMergedAttributeProvenance(Company.CITY), doc));
		company.appendChild(createTextElementWithProvenance("industry", record.getIndustry(),
				record.getMergedAttributeProvenance(Company.INDUSTRY), doc));
		if (record.getAssets() != null) {
			company.appendChild(createTextElementWithProvenance("assets", record.getAssets().toString(),
					record.getMergedAttributeProvenance(Company.ASSETS), doc));
		}
		if (record.getRevenue() != null) {
			company.appendChild(createTextElementWithProvenance("revenue", record.getRevenue().toString(),
					record.getMergedAttributeProvenance(Company.REVENUE), doc));
		}
		if (record.getFounded() != null) {
			company.appendChild(createTextElementWithProvenance("founded", record.getFounded().toString(),
					record.getMergedAttributeProvenance(Company.FOUNDED), doc));
		}

		company.appendChild(createKeypersonList(record, doc));

		return company;
	}

	protected Element createTextElementWithProvenance(String name, String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createKeypersonList(Company record, Document doc) {
		Element keyPersons = doc.createElement("keypeople");

		for (String s : record.getKeyPersons()) {
			keyPersons.appendChild(createTextElement("name", s, doc));
		}

		return keyPersons;
	}

}
