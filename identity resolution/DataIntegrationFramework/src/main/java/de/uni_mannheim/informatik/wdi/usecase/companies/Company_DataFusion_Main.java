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
package de.uni_mannheim.informatik.wdi.usecase.companies;

import java.io.File;

import org.joda.time.DateTime;

import de.uni_mannheim.informatik.wdi.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.wdi.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.FusableDataSet;
import de.uni_mannheim.informatik.wdi.model.FusionEvaluationResult;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.AssetsEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.CityEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.CountryEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.FoundedEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.KeyPersonEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.NameEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation.RevenueEvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyCSVFormatter;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyFactory;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyXMLFormatter;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Company_DataFusion_Main {

	public static void main(String[] args) throws Exception {

		// Forbes
		System.out.println("FORBES");
		FusableDataSet<Company> forbes = new FusableDataSet<>();
		forbes.loadFromXML(new File("TODO forbes file"), new CompanyFactory(), "/companies/company");
		forbes.printDataSetDensityReport();

		// DBpedia
		System.out.println("DBPEDIA");
		FusableDataSet<Company> dbpedia = new FusableDataSet<>();
		dbpedia.loadFromXML(new File("TODO dbpedia file"), new CompanyFactory(), "/companies/company");
		dbpedia.printDataSetDensityReport();

		// Fullcontact
		System.out.println("FULLCONTACT");
		FusableDataSet<Company> fullcontact = new FusableDataSet<>();
		fullcontact.loadFromXML(new File("TODO fullcontact file"), new CompanyFactory(), "/companies/company");
		fullcontact.printDataSetDensityReport();

		// Maintain Provenance
		forbes.setScore(1.0);
		dbpedia.setScore(3.0);
		fullcontact.setScore(1.0);

		// Date (e.g. last update)
		forbes.setDate(DateTime.parse("2013-01-01"));
		dbpedia.setDate(DateTime.parse("2015-01-01"));
		fullcontact.setDate(DateTime.parse("2016-06-01"));

		// load correspondences
		CorrespondenceSet<Company> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("TODO correspondence files between forbes and dbpedia from IR"),
				forbes, dbpedia);
		correspondences.loadCorrespondences(
				new File("TODO correspondence filse between forbes and fullcontact from IR"), forbes, fullcontact);
		correspondences.printGroupSizeDistribution();

		// Define data fusion strategy
		// TODO replace NULL with fusion strategies
		DataFusionStrategy<Company> strategy = new DataFusionStrategy<>(new CompanyFactory());

		strategy.addAttributeFuser("name", null, new NameEvaluationRule());
		strategy.addAttributeFuser("assets", null, new AssetsEvaluationRule());
		strategy.addAttributeFuser("revenue", null, new RevenueEvaluationRule());
		strategy.addAttributeFuser("founder", null, new KeyPersonEvaluationRule());
		strategy.addAttributeFuser("founded", null, new FoundedEvaluationRule());
		strategy.addAttributeFuser("country", null, new CountryEvaluationRule());
		strategy.addAttributeFuser("city", null, new CityEvaluationRule());

		// create the fusion engine
		DataFusionEngine<Company> engine = new DataFusionEngine<>(strategy);

		// calculate cluster consistency
		engine.printClusterConsistencyReport(correspondences);

		// run the fusion
		FusableDataSet<Company> fusedDataSet = engine.run(correspondences);

		// write the result
		fusedDataSet.writeXML(new File("TODO fused output file"), new CompanyXMLFormatter());

		// load the gold standard
		DataSet<Company> gs = new FusableDataSet<>();
		gs.loadFromXML(new File("TODO goldstandard with fused data"), new CompanyFactory(), "/companies/company");

		// evaluate
		DataFusionEvaluator<Company> evaluator = new DataFusionEvaluator<>(strategy);
		// evaluator.setVerbose(true);
		FusionEvaluationResult<Company> result = evaluator.calculateFusionResult(fusedDataSet, gs);
		result.printPerformance();

		result.writeToCSV(new File("TODO error file"), new CompanyCSVFormatter());

	}

}
