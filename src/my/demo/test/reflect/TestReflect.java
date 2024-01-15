package my.demo.test.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestReflect {
	private A2 a2;
	public static void main(String[] args) throws Exception {
		String name = "my.demo.test.reflect.A2";
		TestReflect test = new TestReflect();
		
//		name = "java.awt.Graphics2D";
//		name = "java.lang.Class";
		Class<?> cls = Class.forName(name); 
		
		test.print(cls); 
//		test.test();
	}
	
	void test() throws Exception {
		String name = "my.demo.test.reflect.A2";
		Class<?> cls = Class.forName(name); 
		
		Constructor<?> con = cls.getDeclaredConstructor(int.class, int.class);
		con.setAccessible(true);
		a2 = (A2)con.newInstance(3, 5);
		
		Method m = cls.getDeclaredMethod("jm2", int[].class);
		m.setAccessible(true);
		m.invoke(a2, new int[]{7, 3});
	}
	
	void print(Class<?> cls) throws Exception { 
		dispClass("this.class", cls);
 
		dspInterf(cls);
		print(cls, true); 
		print(cls, false);
	}
	
	void print(Class<?> cls, boolean declared) throws Exception { 
		System.out.println("\n==========" + (declared ? "Declared:" : "Public:"));
		
		dspClasses(cls, declared);
		dspConstructors(cls, declared);
		dspMethods(cls, declared);
		dspFields(cls, declared);
	}
	
	void dspInterf(Class<?> cls) { 
		dispClass("this.super", cls.getSuperclass());
		
		for (Class<?> c: cls.getInterfaces()) {
			dispClass("interface", c); 
		}
	}
	
	void dspClasses(Class<?> cls, boolean declared) throws Exception { 
		if (!declared) {
			for (Class<?> c: cls.getClasses()) {
				dispClass("member class", c); 
			}
		} else {
			for (Class<?> c: cls.getDeclaredClasses()) {
				dispClass("declared member class", c); 
			}
		} 
	}
	
	void dspConstructors(Class<?> cls, boolean declared) throws Exception { 
		if (!declared) {
			for (Constructor<?> c: cls.getConstructors()) {
				dispConstructor(c); 
			}
		} else {
			for (Constructor<?> c: cls.getDeclaredConstructors()) {
				dispConstructor(c);
			}
		} 
	}
	
	void dspMethods(Class<?> cls, boolean declared) throws Exception { 
		if (!declared) {
			for (Method c: cls.getMethods()) {
				dispMethod(c); 
			}
		} else {
			for (Method c: cls.getDeclaredMethods()) {
				dispMethod(c);
			}
		} 
	}
	
	void dspFields(Class<?> cls, boolean declared) throws Exception { 
		if (!declared) {
			for (Field c: cls.getFields()) {
				dispField(c); 
			} 
		} else {
			for (Field c: cls.getDeclaredFields()) {
				dispField(c);
			}
		}
	}
	
	void dispConstructor(Constructor<?> c) {
		boolean first = true;
		
		System.out.printf("Constructor: %s %s(", 
				Modifier.toString(c.getModifiers()),
				c.getName());
		
		for (Class<?> p : c.getParameterTypes()) {
			if (!first) {
				System.out.printf(", %s", p.getCanonicalName());
			} else {
				System.out.printf("%s", p.getCanonicalName());
				first = false;
			}
		} 
		
		System.out.printf(")");
		
		first = true;
		for (Class<?> p : c.getExceptionTypes()) {
			if (!first) {
				System.out.printf(", %s", p.getCanonicalName());
			} else {
				System.out.printf(" %s", p.getCanonicalName());
				first = false;
			}
		} 
		
		System.out.println();
	}
	
	void dispMethod(Method c) {
		boolean first = true;
		
		System.out.printf("Method: %s %s %s(", 
				Modifier.toString(c.getModifiers()),
				c.getReturnType().getCanonicalName(),
				c.getName());
		
		for (Class<?> p : c.getParameterTypes()) {
			if (!first) {
				System.out.printf(", %s", p.getCanonicalName());
			} else {
				System.out.printf("%s", p.getCanonicalName());
				first = false;
			}
		} 
		
		System.out.printf(")");
		
		first = true;
		for (Class<?> p : c.getExceptionTypes()) {
			if (!first) {
				System.out.printf(", %s", p.getCanonicalName());
			} else {
				System.out.printf(" %s", p.getCanonicalName());
				first = false;
			}
		} 
		
		System.out.println();
	}
	
	void dispField(Field c) {
		System.out.printf("Field: %s %s %s|\n", Modifier.toString(c.getModifiers()), 
				c.getType().getCanonicalName(), c.getName());
	}
	
	void dispClass(String promt, Class<?> c) {
		System.out.printf("%s: %s\n", promt, c.getCanonicalName());
	}
}

interface P {
	
}

class A1 implements P {
	private int i1;
	protected int i2;
	public int i3;
	int i4;
	
	static private int si1;
	static protected int si2;
	static public int si3;
	static int si4;
	
	private void m1() {
		
	}
	
	protected void m2() {
		
	}
	
	public void m3() {
		
	}
	
	void m4() {
		
	}
	
	static private void sm1() {
		
	}
	
	static protected void sm2() {
		
	}
	
	static public void sm3() {
		
	}
	
	static void sm4() {
		
	}
	
	private A1(int x) {
		
	}
	
	protected A1(int x, int y) {
		
	}
	
	public A1(int x, int y, int z) {
		
	}
	
	A1() {
		
	}
}

interface K {
	void run();
}

interface K2 {
	
}

class A2 extends A1 implements K, K2 {
	private int j1;
	protected int j2;
	public int j3;
	int j4;
	
	static private int sj1;
	static protected int sj2;
	static public int sj3;
	static int sj4;
	
	private void jm1(int... nums) {
		for (int x: nums) {
			System.out.println("IN A.jm1, x=" + x);
		} 
	}
	
	public void run() {
		
	}
	
	private void jm2(int[] nums) {
		for (int x: nums) {
			System.out.println("IN A.jm2, x=" + x);
		} 
	}
	
	static private void sjm1(Long l, String s) {
		
	}
	
	static public void sjm2() {
		
	}
	
	private A2(int x) {
		j1 = x;
	}
	
	private A2(final int x, int y) {
		j1 = x;
		j2 = y;
	}
	
	A2() {
		
	}
	
	@Override
	public void m2() {
		
	}
}