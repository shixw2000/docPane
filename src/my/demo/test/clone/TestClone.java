package my.demo.test.clone;

import com.vdurmont.emoji.EmojiParser;

public class TestClone {
	public void test() throws Exception {
		String path = "com.vdurmont.emoji.EmojiParser";
		
//		Class<?> cl = Class.forName(path);
//		Method m = cl.getMethod("parseToUnicode", String.class);
		
		EmojiParser p = new EmojiParser();
		
//		path = m.invoke(null, "helo" + "😂");
		path = p.parseToUnicode("helo" + "😂");
		
		System.out.println(path + "--" + this.getClass().getClassLoader());
	}
}