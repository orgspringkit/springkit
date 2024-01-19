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
package org.springkit.mybatisplus.wquery;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * hack SerializedLambda
 */
@SuppressWarnings("rawtypes")
public class SFunctionLambdaWrapper implements com.baomidou.mybatisplus.core.toolkit.support.SFunction {
	private static final long serialVersionUID = 747656220330979536L;
	private final Class<?> entityClazz;
	private final Field f;
	private final Method fm;

	SFunctionLambdaWrapper(Field entityField) {
		super();
		this.entityClazz = entityField.getDeclaringClass();
		this.f = entityField;
		this.fm = getFieldMethod();
	}

	private Method getFieldMethod() {
		String mname = formatGetMethodName("get", f);

		Method m = null;
		try {
			m = entityClazz.getMethod(mname);
		} catch (NoSuchMethodException | SecurityException e) {

		}

		mname = formatGetMethodName("is", f);
		try {
			m = entityClazz.getMethod(mname);
		} catch (NoSuchMethodException | SecurityException e) {

		}

		if (m == null) {
			throw new RuntimeException(new NoSuchMethodException(f.getName() + "is not find method"));
		}

		return m;
	}

	public Object apply(final Object o) {
		try {
			return fm.invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private final Object writeReplace() {

		String entityClzSig = genTypeSignature(entityClazz);
		String returnTypeSig = genTypeSignature(fm.getReturnType());
		String lambdaFuncSig = com.baomidou.mybatisplus.core.toolkit.support.SFunction.class.getName().replaceAll("\\.", "/");

		SerializedLambda lambda = new SerializedLambda(//
				SFunctionLambdaWrapper.class, //
				lambdaFuncSig, //
				"apply", //
				"(Ljava/lang/Object;)Ljava/lang/Object;", //
				5, //
				entityClzSig, //
				fm.getName(), //
				"()" + returnTypeSig + ";", //
				"(" + entityClzSig + ";)" + returnTypeSig + ";", //
				new Object[0]);

		return lambda;
	}

	private String genTypeSignature(Class<?> type) {
		if ("boolean".equals(type.getName())) {
			return "Z";
		} else {
			return "L" + type.getName().replaceAll("\\.", "/");
		}
	}

	private String formatGetMethodName(String prefix, Field f) {
		String name = f.getName();
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(name.substring(0, 1).toUpperCase());
		if (name.length() > 1) {
			sb.append(name.substring(1));
		}

		return sb.toString();
	}

}
