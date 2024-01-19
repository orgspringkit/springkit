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

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * the column has support what SQL type( insert or update)
 */
public interface AutoFillColumnValueResolve {
	/**
	 * fill value
	 * @param sct
	 * @param table
	 * @return
	 */
	public Object resolve(SqlCommandType sct, String table);

	/**
	 * support the type and table
	 * @param sct
	 * @param table
	 * @return
	 */
	public boolean support(SqlCommandType sct, String table);

	/**
	 * define support what SQL type
	 * 
	 * @return
	 */
	public SqlCommandType[] supporSqlType();

	/**
	 * which column can be auto fill (no define in code in anything)
	 * @return
	 */
	public String columnName();
}
