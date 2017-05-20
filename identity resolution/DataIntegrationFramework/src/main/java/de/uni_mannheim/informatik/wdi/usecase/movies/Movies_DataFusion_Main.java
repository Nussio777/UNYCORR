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
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import de.uni_mannheim.informatik.wdi.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.evaluation.ActorsEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.evaluation.DateEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.evaluation.DirectorEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.evaluation.TitleEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.fusers.ActorsFuserMostRecent;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.fusers.DateFuserMostRecent;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.fusers.DirectorFuserLongestString;
import de.uni_mannheim.informatik.wdi.usecase.movies.datafusion.fusers.TitleFuserLongestString;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.MovieFactory;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.MovieXMLFormatter;

/**
 * Class containing the standard setup to perform a data fusion task, reading
 * input data from the movie usecase.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class Movies_DataFusion_Main {

	public static void main(String[] args) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			TransformerException {

		// Load the Data into FusableDataSet
		FusableDataSet<Movie> ds1 = new FusableDataSet<>();
		ds1.loadFromXML(new File("usecase/movie/input/academy_awards.xml"),
				new MovieFactory(), "/movies/movie");
		ds1.printDataSetDensityReport();

		FusableDataSet<Movie> ds2 = new FusableDataSet<>();
		ds2.loadFromXML(new File("usecase/movie/input/actors.xml"),
				new MovieFactory(), "/movies/movie");
		ds2.printDataSetDensityReport();

		FusableDataSet<Movie> ds3 = new FusableDataSet<>();
		ds3.loadFromXML(new File("usecase/movie/input/golden_globes.xml"),
				new MovieFactory(), "/movies/movie");
		ds3.printDataSetDensityReport();

		// Maintain Provenance
		// Scores (e.g. from rating)
		ds1.setScore(3.0);
		ds2.setScore(1.0);
		ds3.setScore(2.0);

		// Date (e.g. last update)
		ds1.setDate(DateTime.parse("2012-01-01"));
		ds2.setDate(DateTime.parse("2010-01-01"));
		ds3.setDate(DateTime.parse("2008-01-01"));

		// load correspondences
		CorrespondenceSet<Movie> correspondences = new CorrespondenceSet<>();
		correspondences
				.loadCorrespondences(
						new File(
								"usecase/movie/correspondences/academy_awards_2_actors_correspondences.csv"),
						ds1, ds2);
		correspondences
				.loadCorrespondences(
						new File(
								"usecase/movie/correspondences/actors_2_golden_globes_correspondences.csv"),
						ds2, ds3);

		// write group size distribution
		correspondences.printGroupSizeDistribution();

		// define the fusion strategy
		DataFusionStrategy<Movie> strategy = new DataFusionStrategy<>(
				new MovieFactory());
		// add attribute fusers
		// Note: The attribute name is only used for printing the reports
		 strategy.addAttributeFuser("Title", new TitleFuserLongestString(),
		 new TitleEvaluationRule());
		 strategy.addAttributeFuser("Director",
		 new DirectorFuserLongestString(), new DirectorEvaluationRule());
		 strategy.addAttributeFuser("Date", new DateFuserMostRecent(),
		 new DateEvaluationRule());
		 strategy.addAttributeFuser("Actors",
		 new ActorsFuserMostRecent(),
		 new ActorsEvaluationRule());

		// create the fusion engine
		DataFusionEngine<Movie> engine = new DataFusionEngine<>(strategy);

		// calculate cluster consistency
		engine.printClusterConsistencyReport(correspondences);

		// run the fusion
		FusableDataSet<Movie> fusedDataSet = engine.run(correspondences);

		// write the result
		fusedDataSet.writeXML(new File("usecase/movie/output/fused.xml"),
				new MovieXMLFormatter());

		// load the gold standard
		DataSet<Movie> gs = new FusableDataSet<>();
		gs.loadFromXML(new File("usecase/movie/goldstandard/fused.xml"),
				new MovieFactory(), "/movies/movie");

		// evaluate
		DataFusionEvaluator<Movie> evaluator = new DataFusionEvaluator<>(
				strategy);
		evaluator.setVerbose(true);
		double accuracy = evaluator.evaluate(fusedDataSet, gs);

		System.out.println(String.format("Accuracy: %.2f", accuracy));

	}

}
