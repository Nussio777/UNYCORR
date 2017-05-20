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
package de.uni_mannheim.informatik.wdi.usecase.movies;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uni_mannheim.informatik.wdi.identityresolution.Correspondence;
import de.uni_mannheim.informatik.wdi.identityresolution.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.wdi.identityresolution.MatchingEngine;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.StandardBlocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.StaticBlockingKeyGenerator;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution.MovieDateComparator10Years;
import de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution.MovieTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.MovieFactory;

/**
 * Class containing the standard setup to perform a duplicate detection task,
 * reading input data from the movie usecase.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Movies_DuplicateDetection_Main {

	public static void main(String[] args) throws Exception {

		// define the matching rule
		LinearCombinationMatchingRule<Movie> rule = new LinearCombinationMatchingRule<>(
				0, 1.0);
		rule.addComparator(new MovieTitleComparatorLevenshtein(), 1);
		rule.addComparator(new MovieDateComparator10Years(), 0.822);

		// create the matching engine
		Blocker<Movie> blocker = new StandardBlocker<Movie>(
				new StaticBlockingKeyGenerator<Movie>());
		MatchingEngine<Movie> engine = new MatchingEngine<>(rule, blocker);

		// load the data sets
		DataSet<Movie> ds1 = new DataSet<>();
		ds1.loadFromXML(new File("usecase/movie/input/actors.xml"),
				new MovieFactory(), "/movies/movie");

		// run the matching
		List<Correspondence<Movie>> correspondences = engine
				.runDuplicateDetection(ds1, true);

		// write the correspondences to the output file
		engine.writeCorrespondences(correspondences, new File(
				"usecase/movie/output/actors_duplicates.csv"));

		printCorrespondences(correspondences);
	}

	private static void printCorrespondences(
			List<Correspondence<Movie>> correspondences) {
		// sort the correspondences
		Collections.sort(correspondences,
				new Comparator<Correspondence<Movie>>() {

					@Override
					public int compare(Correspondence<Movie> o1,
							Correspondence<Movie> o2) {
						int score = Double.compare(o1.getSimilarityScore(),
								o2.getSimilarityScore());
						int title = o1.getFirstRecord().getTitle()
								.compareTo(o2.getFirstRecord().getTitle());

						if (score != 0) {
							return -score;
						} else {
							return title;
						}
					}

				});

		// print the correspondences
		for (Correspondence<Movie> correspondence : correspondences) {
			// if(correspondence.getSimilarityScore()<1.0) {
			System.out.println(String
					.format("%s,%s,|\t\t%.2f\t[%s] %s (%s) <--> [%s] %s (%s)",
							correspondence.getFirstRecord().getIdentifier(),
							correspondence.getSecondRecord().getIdentifier(),
							correspondence.getSimilarityScore(),
							correspondence.getFirstRecord().getIdentifier(),
							correspondence.getFirstRecord().getTitle(),
							correspondence.getFirstRecord().getDate()
									.toString("YYYY-MM-DD"), correspondence
									.getSecondRecord().getIdentifier(),
							correspondence.getSecondRecord().getTitle(),
							correspondence.getSecondRecord().getDate()
									.toString("YYYY-MM-DD")));
			// }
		}
	}

}
