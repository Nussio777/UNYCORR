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
package de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation;

import de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.EqualsSimilarity;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;

/**
 * {@link EvaluationRule} for the coutnries of {@link Releases}s. The rule
 * simply compares the genre of two {@link Release}s and returns true, in case
 * their similarity based on {@link EqualsSimilarity}.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseCountryEvaluationRule extends EvaluationRule<Release> {

	SimilarityMeasure<String> sim = new EqualsSimilarity<String>();

	@Override
	public boolean isEqual(Release record1, Release record2) {

		return sim.calculate(record1.getCountry(), record2.getCountry()) == 1.0;
	}

}
