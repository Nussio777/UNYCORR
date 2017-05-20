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
package de.uni_mannheim.informatik.wdi.usecase.music.identityresolution;

import de.uni_mannheim.informatik.wdi.identityresolution.Comparator;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.EqualsSimilarity;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;

/**
 * {@link Comparator} for {@link Release}s based on the
 * {@link Release#getName()} value and their {@link EqualsSimilarity} value,
 * ignoring the case.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseNameComparatorEqualIgnoreCase extends Comparator<Release> {

	private EqualsSimilarity<String> sim = new EqualsSimilarity<String>();

	@Override
	public double compare(Release entity1, Release entity2) {
		double similarity = sim.calculate(entity1.getName().toLowerCase(), entity2.getName().toLowerCase());

		return similarity;
	}

}
