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

package de.uni_mannheim.informatik.wdi.usecase.movies.identityresolution;

import de.uni_mannheim.informatik.wdi.identityresolution.blocking.BlockingKeyGenerator;
import de.uni_mannheim.informatik.wdi.usecase.movies.model.Movie;

/**
 * {@link BlockingKeyGenerator} for {@link Movie}s, which generates a blocking
 * key based on the decade. E.g. 1999-->199, 2014 --> 201
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class MovieBlockingKeyByDecadeGenerator extends
		BlockingKeyGenerator<Movie> {

	@Override
	public String getBlockingKey(Movie instance) {
		return Integer.toString(instance.getDate().getYear() / 10);
	}

}
