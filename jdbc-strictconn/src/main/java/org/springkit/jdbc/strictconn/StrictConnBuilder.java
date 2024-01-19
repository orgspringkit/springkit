package org.springkit.jdbc.strictconn;

import org.springkit.jdbc.strictconn.JdbcStrictConnSchemeResolve.StrictConnParamDefinition;

interface StrictConnBuilder {

	public StrictConnParamDefinition[] buildScheme(int connScheme);

	public String printConfSql();

	public boolean support(String dbURL);

}
