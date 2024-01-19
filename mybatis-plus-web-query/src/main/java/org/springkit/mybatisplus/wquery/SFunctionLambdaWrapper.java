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
