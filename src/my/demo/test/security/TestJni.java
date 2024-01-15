package my.demo.test.security;

import java.awt.Point;

public class TestJni {
	public static native String shello();
	public native String hello(Point p);
	public native String hello(int n);
	
	static {
		System.loadLibrary("test");
	}
	
	public static void main(String[] args) {
		System.out.println("start now.");
		TestJni t = new TestJni();
		
		String s =  t.hello(new java.awt.Point()) + ":" + t.hello(3) + ":"+ TestJni.shello();
		System.out.println(s + ". start end."); 
	}
}
