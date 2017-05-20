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
package de.uni_mannheim.informatik.wdi.identityresolution.blocking;

import java.util.List;

import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.Matchable;
import de.uni_mannheim.informatik.wdi.model.Pair;

/**
 * Class which implements no blocking strategy but returns the cross product of
 * the input dataset(s).
 * 
 * Internally the {@link StandardBlocker} with the
 * {@link StaticBlockingKeyGenerator} is used.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class NoBlocker<RecordType extends Matchable> extends
		Blocker<RecordType> {

	private Blocker<RecordType> blocker = new StandardBlocker<RecordType>(
			new StaticBlockingKeyGenerator<RecordType>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker#
	 * generatePairs(de.uni_mannheim.informatik.wdi.model.DataSet,
	 * de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	public List<Pair<RecordType, RecordType>> generatePairs(
			DataSet<RecordType> dataset1, DataSet<RecordType> dataset2) {
		return blocker.generatePairs(dataset1, dataset2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker#
	 * generatePairs(de.uni_mannheim.informatik.wdi.model.DataSet, boolean)
	 */
	@Override
	public List<Pair<RecordType, RecordType>> generatePairs(
			DataSet<RecordType> dataset, boolean isSymmetric) {
		return blocker.generatePairs(dataset, isSymmetric);
	}

}
