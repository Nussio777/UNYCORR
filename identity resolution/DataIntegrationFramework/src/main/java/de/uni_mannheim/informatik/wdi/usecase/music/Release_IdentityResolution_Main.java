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
package de.uni_mannheim.informatik.wdi.usecase.music;

import java.io.File;
import java.util.List;

import de.uni_mannheim.informatik.wdi.identityresolution.Correspondence;
import de.uni_mannheim.informatik.wdi.identityresolution.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.wdi.identityresolution.MatchingEngine;
import de.uni_mannheim.informatik.wdi.identityresolution.MatchingEvaluator;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.StandardBlocker;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.MatchingEvaluationResult;
import de.uni_mannheim.informatik.wdi.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.wdi.model.Performance;
import de.uni_mannheim.informatik.wdi.usecase.music.identityresolution.ReleaseBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.wdi.usecase.music.identityresolution.ReleaseNameComparatorLevenshtein;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;
import de.uni_mannheim.informatik.wdi.usecase.music.model.ReleaseCSVFormatter;
import de.uni_mannheim.informatik.wdi.usecase.music.model.ReleaseFactory;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Release_IdentityResolution_Main {

	// Before running this method please replace all the TODOs within the method
	public static void main(String[] args) throws Exception {

		// Read DataSet1 (MusicBrainz)
		System.out.println("Reading MusicBrainz file");
		DataSet<Release> musicbrainz = new DataSet<>();
		musicbrainz.loadFromXMLSax(new File("TODO: MusicBrainz file"), new ReleaseFactory(), "/releases/release");

		// Read DataSet2 (LastFM or Discogs)
		System.out.println("Reading Dataset2 file");
		DataSet<Release> dataset2 = new DataSet<>();
		musicbrainz.loadFromXMLSax(new File("TODO: LastFM or Discogs file"), new ReleaseFactory(), "/releases/release");

		// create a matching Rule
		LinearCombinationMatchingRule<Release> matchingRule = new LinearCombinationMatchingRule<Release>(0.1);
		matchingRule.addComparator(new ReleaseNameComparatorLevenshtein(), 1.0);

		// add a blocker
		Blocker<Release> blocker = new StandardBlocker<>(new ReleaseBlockingKeyByNameGenerator());

		// Initialize Matching Engine
		MatchingEngine<Release> engine = new MatchingEngine<Release>(matchingRule, blocker);

		// Execute the matching
		List<Correspondence<Release>> correspondences_MB_Dataset2 = engine.runMatching(musicbrainz, dataset2);

		// write the correspondences to the output file
		engine.writeCorrespondences(correspondences_MB_Dataset2, new File("TODO: Correspondence File"));

		// load the gold standard
		MatchingGoldStandard mb_dataset2 = new MatchingGoldStandard();
		mb_dataset2.loadFromCSVFile(new File("TODO: Load Goldstandard between MusciBrainz und Dataset2"));

		// evaluate your result
		MatchingEvaluator<Release> evaluator = new MatchingEvaluator<Release>();
		MatchingEvaluationResult<Release> result = evaluator.calculateMatchingResult(correspondences_MB_Dataset2,
				mb_dataset2);

		// write matches (correct, wrong, missing) to a file
		result.writeToCSV(new File("TODO: Location for your result analysis"), new ReleaseCSVFormatter());

		// print the evaluation result
		System.out.println("MB <-> DataSet2");
		Performance perf = result.getPerformance();
		System.out.println(String.format("Precision: %.4f\nRecall: %.4f\nF1: %.4f", perf.getPrecision(),
				perf.getRecall(), perf.getF1()));
	}

}
