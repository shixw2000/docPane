package my.demo.doc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

enum EnumDataType {
	DataVersion(100),
	DataModel(101),
	
	DataFont(200),
	DataColor(201),
	
	DataFormats(300),
	DataSlice(301),
	
	DataSegment(400),
	
	DataAttr(500),
	
	DataSliceCtx(600),
	
	DataEnd(10000);
	
	EnumDataType(int type) {
		m_type = type;
	}
	
	private int m_type;
	
	public int type() {
		return m_type;
	}
}



public class DocIO {
	public static final Charset cs = StandardCharsets.UTF_8;
	private final static int DEF_DOC_VERSION = 0xABCDEF01;
	
	
}
