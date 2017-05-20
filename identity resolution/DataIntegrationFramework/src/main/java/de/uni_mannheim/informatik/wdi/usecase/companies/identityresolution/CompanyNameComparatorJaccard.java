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
package de.uni_mannheim.informatik.wdi.usecase.companies.identityresolution;

import de.uni_mannheim.informatik.wdi.identityresolution.Comparator;
import de.uni_mannheim.informatik.wdi.identityresolution.similarity.string.TokenizingJaccardSimilarity;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;

/**
 * {@link Comparator} for {@link Company}s based on the
 * {@link Company#getName()} value and their {@link TokenizingJaccardSimilarity}
 * value.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyNameComparatorJaccard extends Comparator<Company> {

	private TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();

	@Override
	public double compare(Company entity1, Company entity2) {
		if (entity1.getName().startsWith("Bank of") && (entity2.getName().startsWith("Bank of"))){
			return sim.calculate(entity1.getName().substring(7), entity2.getName().substring(7));
		}
		double similarity = sim.calculate(entity1.getName(), entity2.getName());

		return similarity;
	}

}
