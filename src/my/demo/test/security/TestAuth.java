package my.demo.test.security;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.module.NTSystem;

public class TestAuth {
	public static void main(String[] args) throws Exception {
		NTSystem nts = new NTSystem();
		
		System.out.println("user: " + nts.getName());
		
		System.setSecurityManager(new SecurityManager());
		
		LoginContext ctx = new LoginContext("Login1");
		ctx.login();
		
		Subject sub = ctx.getSubject();
		
		System.out.println("login ok: " + sub.getPrincipals(NTUserPrincipal.class));
		
		String res = Subject.doAsPrivileged(sub, new PrivilegedAction<String>() {
			public String run() {
				return System.getProperty("java.home");
			}
		}, null);
		
		System.out.println(res);
	}
}
