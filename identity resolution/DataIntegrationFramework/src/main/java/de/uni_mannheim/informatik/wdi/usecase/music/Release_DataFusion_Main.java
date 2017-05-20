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

import org.joda.time.DateTime;

import de.uni_mannheim.informatik.wdi.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseArtistEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseCountryEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseDateEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseDurationEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseLabelEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseNameEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.datafusion.evaluation.ReleaseTracksEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;
import de.uni_mannheim.informatik.wdi.usecase.music.model.ReleaseFactory;
import de.uni_mannheim.informatik.wdi.usecase.music.model.ReleaseXMLFormatter;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Release_DataFusion_Main {

	// Before running this method please replace all the TODOs within the method
	public static void main(String[] args) throws Exception {

		// Read MusicBrainz
		System.out.println("Reading MusicBrainz file");
		FusableDataSet<Release> musicbrainz = new FusableDataSet<>();
		musicbrainz.loadFromXMLSax(new File("TODO: MusicBrainz file"), new ReleaseFactory(), "/releases/release");

		// Read Discogs File
		System.out.println("Reading DISCOGS file");
		FusableDataSet<Release> discogs = new FusableDataSet<>();
		discogs.loadFromXMLSax(new File("TODO: Discogs File"), new ReleaseFactory(), "/releases/release");

		// LastFM File
		System.out.println("Reading LastFM file");
		FusableDataSet<Release> lastfm = new FusableDataSet<>();
		lastfm.loadFromXMLSax(new File("TODO: LastFM file"), new ReleaseFactory(), "/releases/release");

		// Maintain Provenance
		// Scores (e.g. from rating)
		discogs.setScore(2.0);
		musicbrainz.setScore(1.0);
		lastfm.setScore(2.0);

		// Date (e.g. last update)
		discogs.setDate(DateTime.parse("2016-03-31"));
		musicbrainz.setDate(DateTime.parse("2016-06-03"));
		lastfm.setDate(DateTime.parse("2016-05-02"));

		// pring density reports
		musicbrainz.printDataSetDensityReport();
		discogs.printDataSetDensityReport();
		lastfm.printDataSetDensityReport();

		// load correspondences
		CorrespondenceSet<Release> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("TODO: File with correspondences from IR - Musicbrainz Discogs"),
				musicbrainz, discogs);
		correspondences.loadCorrespondences(new File("TODO: File with correspondences from IR - Musicbrainz LastFM"),
				musicbrainz, lastfm);
		
		// Print group distribution
		correspondences.printGroupSizeDistribution();

		// Define data fusion strategy
		DataFusionStrategy<Release> strategy = new DataFusionStrategy<>(new ReleaseFactory());
		// add strategies and evaluators
		// TODO Replace the null values with the corresponding fusers
		strategy.addAttributeFuser("name", null, new ReleaseNameEvaluationRule());
		strategy.addAttributeFuser("artist", null, new ReleaseArtistEvaluationRule());
		strategy.addAttributeFuser("release-date", null, new ReleaseDateEvaluationRule());
		strategy.addAttributeFuser("release-country", null,
				new ReleaseCountryEvaluationRule());
		strategy.addAttributeFuser("duration", null, new ReleaseDurationEvaluationRule());
		strategy.addAttributeFuser("label", null, new ReleaseLabelEvaluationRule());
		strategy.addAttributeFuser("tracks", null, new ReleaseTracksEvaluationRule());

		// create the fusion engine
		DataFusionEngine<Release> engine = new DataFusionEngine<>(strategy);

		// calculate cluster consistency
		engine.printClusterConsistencyReport(correspondences);

		// run the fusion
		FusableDataSet<Release> fusedDataSet = engine.run(correspondences);

		// write the result
		fusedDataSet.writeXML(new File("TODO: Fusionouput File"), new ReleaseXMLFormatter());

		// load the gold standard
		DataSet<Release> gs = new FusableDataSet<>();
		gs.loadFromXML(new File("TODO: Goldstandard for fused values"), new ReleaseFactory(), "/releases/release");

		// evaluate
		DataFusionEvaluator<Release> evaluator = new DataFusionEvaluator<>(strategy);
		evaluator.setVerbose(true);
		double accuracy = evaluator.evaluate(fusedDataSet, gs);

		System.out.println(String.format("Accuracy: %.2f", accuracy));

	}

}
