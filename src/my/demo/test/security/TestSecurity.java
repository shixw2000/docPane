package my.demo.test.security;

public class TestSecurity {
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new SecurityManager());
		
		System.out.println(System.getProperty("java.version"));
	}
}
