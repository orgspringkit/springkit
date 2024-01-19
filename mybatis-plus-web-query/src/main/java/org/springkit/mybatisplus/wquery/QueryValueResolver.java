package org.springkit.mybatisplus.wquery;

/**
 * define a resolver what value of parameter from http-query
 */
public interface QueryValueResolver {
	public String resolve(String name, String original);
}
