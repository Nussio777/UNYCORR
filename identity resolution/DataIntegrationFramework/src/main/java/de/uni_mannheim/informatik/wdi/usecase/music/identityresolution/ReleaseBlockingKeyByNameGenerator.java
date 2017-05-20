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

package de.uni_mannheim.informatik.wdi.usecase.music.identityresolution;

import de.uni_mannheim.informatik.wdi.identityresolution.blocking.BlockingKeyGenerator;
import de.uni_mannheim.informatik.wdi.usecase.music.model.Release;

/**
 * {@link BlockingKeyGenerator} for {@link Release}s, which generates a blocking
 * key based on the name, ignoring the case
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseBlockingKeyByNameGenerator extends BlockingKeyGenerator<Release> {

	@Override
	public String getBlockingKey(Release instance) {
		return instance.getName();
	}

}
