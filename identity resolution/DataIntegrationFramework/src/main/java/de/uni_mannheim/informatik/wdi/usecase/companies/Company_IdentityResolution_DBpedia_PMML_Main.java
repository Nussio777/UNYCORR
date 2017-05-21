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

import de.uni_mannheim.informatik.wdi.identityresolution.*;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.*;
import de.uni_mannheim.informatik.wdi.model.*;
import de.uni_mannheim.informatik.wdi.usecase.companies.identityresolution.*;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyCSVFormatter;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robert
 *
 */
public class Company_IdentityResolution_DBpedia_PMML_Main {

	// before running this class, replace the TODOs with the correct files
	public static void main(String[] args) throws Exception {
		// Load Forbes
		System.out.println("Loading Forbes");
		DataSet<Company> forbes = new DataSet<>();
		forbes.loadFromXML(new File("src/main/resources/Forbes/Forbes_SM_Results_01.xml"), new CompanyFactory(), "/companies/company");

		// Load Dataset2
		DataSet<Company> dataset2 = new DataSet<>();
		dataset2.loadFromXML(new File("src/main/resources/DBpedia/DBpedia_SM_Results_02.xml"), new CompanyFactory(),
				"/companies/company");

		// Matching Rule
		PMMLMatchingRule<Company> matchingRule = new PMMLMatchingRule<Company>(0.95, "src/main/resources/LogRegDBpedia.pmml");
		matchingRule.addComparator(new CompanyNameComparatorLevenshtein());
		matchingRule.addComparator(new CompanyIndustryComparatorJaccard());
		matchingRule.addComparator(new CompanyNameComparatorJaccard());
		matchingRule.addComparator(new CompanyRevenueComparatorRelative());

		//BlockingKeyGenerator<Company> blockingKeyGenerator = new CompanyBlockingKeyByFirstCharGenerator();
		BlockingKeyGenerator<Company> blockingKeyGenerator = new BlockingKeyGenerator<Company>() {
			@Override
			public String getBlockingKey(Company instance) {
				return "";
			}
		};

		Blocker<Company> blocker = new StandardBlocker<>(blockingKeyGenerator);

		// Initialize Matching Engine
		MatchingEngine<Company> engine = new MatchingEngine<Company>(matchingRule, blocker);

		// Execute the matching
		List<Correspondence<Company>> correspondences_Forbes_dataset2 = engine.runMatching(forbes, dataset2);

		// Restrict the correspondences
		/*Map<String, List<Correspondence<Company>>> correspondenceMap = new HashMap<>();
		for(Correspondence<Company> corr : correspondences_Forbes_dataset2) {
			String key = corr.getFirstRecord().getIdentifier() + ":" + corr.getSecondRecord().getIdentifier();
			if(!correspondenceMap.containsKey(key)) {
				correspondenceMap.put(key, new ArrayList<Correspondence<Company>>());
			}
			correspondenceMap.get(key).add(corr);
		}

		List<Correspondence<Company>> correspondences_Forbes_dataset2_cleaned = new ArrayList<>();
		for(Map.Entry<String, List<Correspondence<Company>>> mapEntry : correspondenceMap.entrySet()) {
			double maxSim = Double.NEGATIVE_INFINITY;
			Correspondence<Company> correspondence = null;
			for(Correspondence<Company> corr : mapEntry.getValue()) {
				if(maxSim < corr.getSimilarityScore()) {
					correspondence = corr;
				}
			}
			correspondences_Forbes_dataset2_cleaned.add(correspondence);
		}*/

		// write the correspondences to the output file
		engine.writeCorrespondences(correspondences_Forbes_dataset2, new File("src/main/resources/output/correspondences_Forbes_DBpedia.csv"));

		// load the gold standard (test set)
		MatchingGoldStandard forbes_dataset2 = new MatchingGoldStandard();
		forbes_dataset2.loadFromCSVFile(new File("src/main/resources/goldstandard/forbes_dbpedia_mapping.csv"));

		// create the data set for learning a matching rule (use this file in
		// RapidMiner)
		DataSet<DefaultRecord> features = engine
				.generateTrainingDataForLearning(forbes, dataset2, forbes_dataset2);
		features.writeCSV(
				new File("src/main/resources/output/optimisation/forbes_dbpedia_features.csv"),
				new DefaultRecordCSVFormatter());

		// evaluate your result
		MatchingEvaluator<Company> evaluator = new MatchingEvaluator<Company>();

		// evaluate your result
		MatchingEvaluationResult<Company> mResult = evaluator.calculateMatchingResult(correspondences_Forbes_dataset2,
				forbes_dataset2);
		Performance perfTest2 = mResult.getPerformance();
		mResult.writeToCSV(new File("src/main/resources/output/matching_result_Forbes_DBpedia.csv"),
				new CompanyCSVFormatter());

		// Evaluate blocking
		BlockingEvaluator<Company> blockingEvaluator = new BlockingEvaluator<>(blockingKeyGenerator, true);
		BlockingPerformance blockingPerformance = blockingEvaluator.evaluateBlocking(engine.getAllPairs(), forbes_dataset2);

		// print the evaluation result
		System.out.println("Forbes <-> DBpedia");
		System.out.println(String.format(
				"Precision: %.4f\nRecall: %.4f\nF1: %.4f\nReductionRatio: %.4f\nPairCompleteness: %.4f",
				perfTest2.getPrecision(),
				perfTest2.getRecall(),
				perfTest2.getF1(),
				blocker.getReductionRatio(),
				blockingPerformance.getPairCompleteness()));

	}

}
