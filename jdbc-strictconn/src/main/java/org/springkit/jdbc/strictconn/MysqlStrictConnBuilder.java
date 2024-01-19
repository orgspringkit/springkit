/*
 * Copyright (c) 2011-2024, springkit (orgspringkit@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springkit.jdbc.strictconn;

import java.util.ArrayList;
import java.util.List;

import org.springkit.jdbc.strictconn.JdbcStrictConnSchemeResolve.StrictConnParamDefinition;

abstract class MysqlStrictConnBuilder implements StrictConnBuilder {

	@Override
	public final StrictConnParamDefinition[] buildScheme(int connScheme) {

		List<StrictConnParamDefinition> strictConnParams = new ArrayList<>();

		for (StrictConnSchemeEnum scheme : StrictConnSchemeEnum.separate(connScheme)) {
			strictConnParams.addAll(build(scheme));
		}

		return strictConnParams.toArray(new StrictConnParamDefinition[strictConnParams.size()]);
	}

	@Override
	public final String printConfSql() {
		return "show variable like 'time_zone'";
	}

	@Override
	public boolean support(String dbURL) {
		if (dbURL == null) {
			return false;
		} else {
			return dbURL.toLowerCase().trim().startsWith("jdbc:mysql:");
		}
	}

	protected abstract List<StrictConnParamDefinition> build(StrictConnSchemeEnum scheme);

}
