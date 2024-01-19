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
