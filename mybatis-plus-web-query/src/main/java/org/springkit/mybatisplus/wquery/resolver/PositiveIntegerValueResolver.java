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
package org.springkit.mybatisplus.wquery.resolver;

import org.springkit.mybatisplus.wquery.QueryValueResolver;

public class PositiveIntegerValueResolver implements QueryValueResolver {

	@Override
	public String resolve(String name, String original) {
		boolean isNumber = false;
		if (original != null) {
			isNumber = original.matches("[\\-\\d]+");
		}
		if (isNumber) {
			Long v = null;
			try {
				v = Long.parseLong(original);
			} catch (Exception e) {
			}
			if (v != null && v.longValue() > 0) {
				return String.valueOf(v);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
