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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springkit.jdbc.strictconn.JdbcStrictConnSchemeResolve.FixedParamValueResolve;
import org.springkit.jdbc.strictconn.JdbcStrictConnSchemeResolve.ParamValueResolve;
import org.springkit.jdbc.strictconn.JdbcStrictConnSchemeResolve.StrictConnParamDefinition;

/**
 * define in com.mysql.jdbc.ConnectionProperties
 */
class Mysql5StrictConnBuilder extends MysqlStrictConnBuilder {

	@Override
	protected List<StrictConnParamDefinition> build(StrictConnSchemeEnum scheme) {
		List<StrictConnParamDefinition> connParamDef = new ArrayList<>();
		
		if (scheme == StrictConnSchemeEnum.CHARSET) {
			connParamDef.add(new StrictConnParamDefinition("useUnicode", new FixedParamValueResolve("true"), false));
			connParamDef.add(new StrictConnParamDefinition("characterEncoding", new FixedParamValueResolve("UTF-8"), false));
		}
		if (scheme == StrictConnSchemeEnum.OPTIMIZE_CONN) {
			connParamDef.add(new StrictConnParamDefinition("autoReconnect", new FixedParamValueResolve("true"), false));
			connParamDef.add(new StrictConnParamDefinition("autoReconnectForPools", new FixedParamValueResolve("true"), false));
			connParamDef.add(new StrictConnParamDefinition("maxReconnects", new FixedParamValueResolve("3"), false));
			connParamDef.add(new StrictConnParamDefinition("initialTimeout", new FixedParamValueResolve("5"), false));

			connParamDef.add(new StrictConnParamDefinition("failOverReadOnly", new FixedParamValueResolve("false"), false));
		}
		if (scheme == StrictConnSchemeEnum.TIMEOUT) {
			connParamDef.add(new StrictConnParamDefinition("connectTimeout", new FixedParamValueResolve("3000"), false));
			connParamDef.add(new StrictConnParamDefinition("socketTimeout", new FixedParamValueResolve("3000"), false));
		}
		if (scheme == StrictConnSchemeEnum.TIMEZONE) {
			connParamDef.add(new StrictConnParamDefinition("serverTimezone", new ParamValueResolve() {
				@Override
				public String resolve(String original) {
					TimeZone tz = TimeZone.getDefault();
					int offset = tz.getOffset(new Date().getTime());
					boolean neg = offset >= 0 ? false : true;
					int tzOffsetH = offset / 1000 / 60 / 60;
					int tzOffsetM = (offset / 1000 / 60) % 60;
					return String.format("GMT%s%02d:%02d", neg ? "-" : "+", Math.abs(tzOffsetH), Math.abs(tzOffsetM));
				}

			}, true));
		}
		return connParamDef;
	}

}
