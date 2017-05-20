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
package de.uni_mannheim.informatik.wdi.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A default model that represents a {@link Record} as a set of key/value pairs.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class DefaultRecord extends Record {

	private static final long serialVersionUID = 1L;
	private Map<String, String> values;
	private Map<String, List<String>> lists;

	public DefaultRecord(String identifier, String provenance) {
		super(identifier, provenance);
		values = new HashMap<>();
		lists = new HashMap<>();
	}

	public String getValue(String attributeName) {
		return values.get(attributeName);
	}

	public List<String> getList(String attributeName) {
		return lists.get(attributeName);
	}

	public void setValue(String attributeName, String value) {
		values.put(attributeName, value);
	}

	public void setList(String attributeName, List<String> list) {
		lists.put(attributeName, list);
	}

	public Collection<String> getAttributeNames() {
		HashSet<String> names = new HashSet<>(values.keySet());
		names.addAll(lists.keySet());
		return names;
	}

	@Override
	public boolean hasValue(String attributeName) {
		return values.containsKey(attributeName)
				|| lists.containsKey(attributeName);
	}

}
