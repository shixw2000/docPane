package my.demo.test.security;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import my.demo.test.clone.TestClone;

public class TestClassLoader {
	public static void main(String[] args) throws Exception {
		ClassLoader cloader= new MyClassLoader();
		
		Class<?> cl = cloader.loadClass("my.demo.test.clone.TestClone");
		Method m = cl.getMethod("test");
		
		Object o = cl.newInstance();
		m.invoke(o);
		
		TestClone tc = new TestClone();
		tc.test();
	}
}

class MyClassLoader extends ClassLoader {
	MyClassLoader() {
		super(null);
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] buff = new byte[1024];
		InputStream is = null;
		ByteArrayOutputStream bo = null;
		int len = 0;
		
		String path = name.replace('.', '/').concat(".class");
		
		
		try {
			is = ClassLoader.getSystemResourceAsStream(path);
			bo = new ByteArrayOutputStream();
			
			len = is.read(buff);
			while (-1 != len) {
				if (0 < len) {
					bo.write(buff, 0, len);
				}
				
				len = is.read(buff);
			}
			
			buff = bo.toByteArray();
			Class<?> c = defineClass(name, buff, 0, buff.length);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassNotFoundException();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				
				if (null != bo) {
					bo.close();
				}
			} catch (Exception e) {
				
			}
			
			System.out.println("in MyLoader: " + name);
		} 
	}
}
