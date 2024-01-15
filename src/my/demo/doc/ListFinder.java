package my.demo.doc;

enum LOG_LEVEL {
	LOG_LEVEL_1, // info
	LOG_LEVEL_2, // debug
	LOG_LEVEL_3	// detail
}


interface DocWriter { 
	void writeInt(int n) throws Exception;
	void writeChars(char[] str, int off, int len) throws Exception;
	void writeString(String str) throws Exception;
}

interface DocReader {
	int readInt() throws Exception;
	void readChars(char[] str, int off, int len) throws Exception;
	String readString() throws Exception;
}
