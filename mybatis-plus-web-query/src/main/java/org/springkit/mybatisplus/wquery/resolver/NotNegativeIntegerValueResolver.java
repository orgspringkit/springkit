package org.springkit.mybatisplus.wquery.resolver;

import org.springkit.mybatisplus.wquery.QueryValueResolver;

public class NotNegativeIntegerValueResolver implements QueryValueResolver {

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
			if (v != null && v.longValue() >= 0) {
				return String.valueOf(v);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
