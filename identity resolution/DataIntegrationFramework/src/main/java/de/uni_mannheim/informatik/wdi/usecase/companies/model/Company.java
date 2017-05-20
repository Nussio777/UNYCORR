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
 * A {@link Record} representing a company.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Company extends Record {

	/*
	 * example entry <company> <id>random1</id> <name>SAP</name>
	 * <type>IPO</type> <website>http://www.sap.com</website>
	 * <founded>1972-01-01</founded> <country>DE</country> <city>Walldorf</city>
	 * <industry>Healthcare</industry> <assets>3730000123</assets>
	 * <revenue>312344221</revenue> <employees>10</employees> <keypeople>
	 * <name>Hasso Plattner</name> <name>Hans-Werner Hector</name> <name>Klaus
	 * Tschira</name> </keypeople> </company>
	 */

	private static final long serialVersionUID = 1L;

	public Company(String identifier, String provenance) {
		super(identifier, provenance);
		keyPersons = new LinkedList<>();
	}

	private String name;
	private DateTime founded;
	private String country;
	private String city;
	private String industry;
	private Double assets;
	private Double revenue;
	private List<String> keyPersons;

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
	 * @return the founded
	 */
	public DateTime getFounded() {
		return founded;
	}

	/**
	 * @param founded
	 *            the founded to set
	 */
	public void setFounded(DateTime founded) {
		this.founded = founded;
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * @param industry
	 *            the industry to set
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * @return the assets
	 */
	public Double getAssets() {
		return assets;
	}

	/**
	 * @param assets
	 *            the assets to set
	 */
	public void setAssets(Double assets) {
		this.assets = assets;
	}

	/**
	 * @return the revenue
	 */
	public Double getRevenue() {
		return revenue;
	}

	/**
	 * @param revenue
	 *            the revenue to set
	 */
	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	/**
	 * @return the keyPersons
	 */
	public List<String> getKeyPersons() {
		return keyPersons;
	}

	/**
	 * @param keyPersons
	 *            the keyPersons to set
	 */
	public void setKeyPersons(List<String> keyPersons) {
		this.keyPersons = keyPersons;
	}

	public static final String NAME = "Name";
	public static final String COUNTRY = "Country";
	public static final String CITY = "City";
	public static final String INDUSTRY = "Industry";
	public static final String ASSETS = "Assets";
	public static final String REVENUE = "Revenue";
	public static final String FOUNDED = "Founded";
	public static final String KEYPERSONS = "Keypersons";

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
		return Arrays.asList(new String[] { NAME, COUNTRY, CITY, INDUSTRY, ASSETS, REVENUE, FOUNDED, KEYPERSONS });
	}

	@Override
	public boolean hasValue(String attributeName) {
		switch (attributeName) {
		case NAME:
			return getName() != null && !getName().isEmpty();
		case COUNTRY:
			return getCountry() != null && !getCountry().isEmpty();
		case CITY:
			return getCity() != null && !getCity().isEmpty();
		case INDUSTRY:
			return getIndustry() != null && !getIndustry().isEmpty();
		case ASSETS:
			return getAssets() != null && getAssets() > 0;
		case REVENUE:
			return getRevenue() != null && getRevenue() > 0;
		case FOUNDED:
			return getFounded() != null;
		case KEYPERSONS:
			return getKeyPersons() != null && getKeyPersons().size() > 0;
		default:
			return false;
		}
	}

	// Solely translates the actors into a human readable string (names of
	// actors)
	protected String keyPersonsLists() {
		String keyP = "";
		if (getKeyPersons() != null) {
			for (String s : getKeyPersons()) {
				if (keyP.length() > 1) {
					keyP += "|";
				}
				keyP += s;
			}
		}
		return keyP;
	}

	@Override
	public String toString() {
		return String.format(
				"[Name: %s / Country: %s / City: %s / Industry: %s / Assets: %s / Revenue: %s / Founded: %s / Keyperson(s): %s]",
				getName(), getCountry(), getCity(), getIndustry(), getAssets(), getRevenue(), getFounded().toString(),
				keyPersonsLists());
	}
}
