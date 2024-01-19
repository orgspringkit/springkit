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
package org.springkit.mybatis.autofill;

public class CurrentContextHolder {

	private static Supplier supplier;

	public static String get() {
		if (CurrentContextHolder.supplier != null) {
			return supplier.get();
		} else {
			return null;
		}
	}

	public static void setSupplier(Supplier supplier) {
		CurrentContextHolder.supplier = supplier;
	}

	public interface Supplier {
		public String get();
	}

}
