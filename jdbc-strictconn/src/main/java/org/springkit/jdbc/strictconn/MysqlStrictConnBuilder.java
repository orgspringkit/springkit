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
