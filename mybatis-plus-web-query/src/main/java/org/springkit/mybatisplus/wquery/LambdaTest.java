package org.springkit.mybatisplus.wquery;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public class LambdaTest {

	public static class Test {

		private int id;

		public int getId() {
			return id;
		}
	}

	public static void main(String[] args) {

		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		PrintStream pr1 = new PrintStream(out1);

		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		PrintStream pr2 = new PrintStream(out2);

		try {
			final SFunction<Test, ?> lambda = (SFunction<Test, ?>) (Test::getId);
			final Method method = lambda.getClass().getDeclaredMethod("writeReplace", (Class<?>[]) new Class[0]);
			method.setAccessible(true);
			final SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda, new Object[0]);
			System.out.println("Lambda方式:");
			printInfo(serializedLambda, pr1);
		} catch (final Exception e) {
			throw new RuntimeException("获取Lambda信息失败", e);
		}

		try {

			Field fid = null;

			for (Field f : Test.class.getDeclaredFields()) {
				if (f.getName().equals("id")) {
					fid = f;
					break;
				}
			}

			@SuppressWarnings("unchecked")
			final SFunction<Test, ?> lambda = (SFunction<Test, ?>) new SFunctionLambdaWrapper(fid);
			final Method method = lambda.getClass().getDeclaredMethod("writeReplace", (Class<?>[]) new Class[0]);
			method.setAccessible(true);
			final SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda, new Object[0]);
			System.out.println("Lambda方式:");
			printInfo(serializedLambda, pr2);
		} catch (final Exception e) {
			throw new RuntimeException("获取Lambda信息失败", e);
		}

		System.out.println("==============");
		System.out.println(new String(out1.toByteArray()));
		System.out.println("==============");
		System.out.println(new String(out2.toByteArray()));

	}

	private static void printInfo(final SerializedLambda serializedLambda, PrintStream print) {
		System.out.println("capturingClass: " + serializedLambda.getCapturingClass());
		print.println("functionalInterfaceClass: " + serializedLambda.getFunctionalInterfaceClass());
		print.println("functionalInterfaceMethodName: " + serializedLambda.getFunctionalInterfaceMethodName());
		print.println("functionalInterfaceMethodSignature: " + serializedLambda.getFunctionalInterfaceMethodSignature());
		print.println("implClass: " + serializedLambda.getImplClass());
		print.println("implMethodName: " + serializedLambda.getImplMethodName());
		print.println("implMethodSignature: " + serializedLambda.getImplMethodSignature());
		print.println("instantiatedMethodType: " + serializedLambda.getInstantiatedMethodType());
		print.println("implMethodKind: " + serializedLambda.getImplMethodKind());
	}

}
