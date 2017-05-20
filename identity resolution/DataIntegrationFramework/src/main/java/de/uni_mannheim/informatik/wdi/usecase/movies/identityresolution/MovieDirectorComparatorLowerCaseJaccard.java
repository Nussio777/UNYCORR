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
package de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution;

import de.uni_mannheim.informatik.wdi.identityresolution.Comparator;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.string.TokenizingJaccardSimilarity;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

/**
 * {@link Comparator} for {@link Movie}s based on the
 * {@link Movie#getDirector()} values, and their
 * {@link TokenizingJaccardSimilarity} similarity, with a lower casing
 * beforehand.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class MovieDirectorComparatorLowerCaseJaccard extends Comparator<Movie> {

	TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();

	@Override
	public double compare(Movie entity1, Movie entity2) {

		// preprocessing
		String s1 = entity1.getDirector();
		if (s1 != null) {
			s1 = s1.toLowerCase();
		} else {
			s1 = "";
		}
		String s2 = entity2.getDirector();
		if (s2 != null) {
			s2 = s2.toLowerCase();
		} else {
			s2 = "";
		}

		// calculate similarity
		double similarity = sim.calculate(s1, s2);

		// postprocessing
		if (similarity <= 0.3) {
			similarity = 0;
		}

		similarity *= similarity;

		return similarity;
	}

}
