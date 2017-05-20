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
package de.uni_mannheim.informatik.wdi.usecase.companies.datafusion.evaluation;

import de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.wdi.usecase.companies.model.Company;

/**
 * {@link EvaluationRule} for the actors of {@link Company}s. The rule simply
 * compares the full set of key persons of two {@link Company}s and returns
 * true, in case they are identical.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class KeyPersonEvaluationRule extends EvaluationRule<Company> {

	@Override
	public boolean isEqual(Company record1, Company record2) {

		return record1.getKeyPersons().containsAll(record2.getKeyPersons())
				&& record2.getKeyPersons().containsAll(record1.getKeyPersons());
	}

}
