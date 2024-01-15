package my.demo.doc;

public class DocExcept extends Exception {
	private static final long serialVersionUID = 11L;
	
	public DocExcept(String msg, Throwable th) {
		super(msg, th);
	}
}
