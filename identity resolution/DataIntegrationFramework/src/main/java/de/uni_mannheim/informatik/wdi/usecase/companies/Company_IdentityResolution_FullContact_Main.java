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
import de.uni_mannheim.informatik.wdi.usecase.companies.identityresolution.CompanyBlockingKeyByDecadeGenerator;
import de.uni_mannheim.informatik.wdi.usecase.companies.identityresolution.CompanyNameComparatorLevenshtein;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyCSVFormatter;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.CompanyFactory;

import java.io.File;
import java.util.List;

/**
 * @author Robert
 *
 */
public class Company_IdentityResolution_FullContact_Main {

	// before running this class, replace the TODOs with the correct files
	public static void main(String[] args) throws Exception {
		// Load Forbes
		System.out.println("Loading Forbes");
		DataSet<Company> forbes = new DataSet<>();
		forbes.loadFromXML(new File("src/main/resources/Forbes/Forbes_SM_Results_01.xml"), new CompanyFactory(), "/companies/company");

		// Load Dataset2
		DataSet<Company> dataset2 = new DataSet<>();
		dataset2.loadFromXML(new File("src/main/resources/FullContact/FullContact_SM_Result_01.xml"), new CompanyFactory(),
				"/companies/company");

		// Matching Rule
		LinearCombinationMatchingRule<Company> matchingRule = new LinearCombinationMatchingRule<Company>(0.1);
		matchingRule.addComparator(new CompanyNameComparatorLevenshtein(), 1.0);

		Blocker<Company> blocker = new StandardBlocker<Company>(new CompanyBlockingKeyByDecadeGenerator());

		// Initialize Matching Engine
		MatchingEngine<Company> engine = new MatchingEngine<Company>(matchingRule, blocker);

		// Execute the matching
		List<Correspondence<Company>> correspondences_Forbes_dataset2 = engine.runMatching(forbes, dataset2);

		// write the correspondences to the output file
		engine.writeCorrespondences(correspondences_Forbes_dataset2, new File("src/main/resources/output/correspondences_Forbes_FullContact.csv"));

		// load the gold standard (test set)
		MatchingGoldStandard forbes_dataset2 = new MatchingGoldStandard();
		forbes_dataset2.loadFromCSVFile(new File("src/main/resources/goldstandard/forbes_fullcontact_mapping.csv"));

		// evaluate your result
		MatchingEvaluator<Company> evaluator = new MatchingEvaluator<Company>();

		// // evaluate your result
		MatchingEvaluationResult<Company> mResult = evaluator.calculateMatchingResult(correspondences_Forbes_dataset2,
				forbes_dataset2);
		Performance perfTest2 = mResult.getPerformance();
		mResult.writeToCSV(new File("src/main/resources/output/matching_result_Forbes_FullContact.csv"),
				new CompanyCSVFormatter());

		// print the evaluation result
		System.out.println("Forbes <-> FullContact");
		System.out.println(String.format("Precision: %.4f\nRecall: %.4f\nF1:  %.4f", perfTest2.getPrecision(),
				perfTest2.getRecall(), perfTest2.getF1()));

	}

}
