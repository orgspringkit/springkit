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

interface JdbcStrictConnSchemeResolve {

	public String showConnSql();

	public StrictConnParamDefinition[] strictConnParamDefinitions();

	public static class StrictConnParamDefinition {

		final String name;
		final boolean override;
		final ParamValueResolve valueResolve;

		public StrictConnParamDefinition(String name, ParamValueResolve valueResolve, boolean override) {
			this.name = name;
			this.valueResolve = valueResolve;
			this.override = override;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public interface ParamValueResolve {
		public String resolve(String original);
	}

	public static class FixedParamValueResolve implements ParamValueResolve {
		private final String value;

		public FixedParamValueResolve(String value) {
			this.value = value;
		}

		@Override
		public String resolve(String original) {
			return value;
		}

	}

}
