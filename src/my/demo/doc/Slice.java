package my.demo.doc;

import java.awt.FontMetrics;
import java.awt.Graphics;

interface Element {
	int length();
}

interface Slice extends Element { 
	SLICE_TYPE type();
	void handle(Callback cb);
	
	boolean canMerge(Slice other);
	Slice merge(Slice other);
	Slice split(int beg, int end);
}

interface Callback {
	int beg();
	int end();
	
	boolean isValid();
	void setValid(boolean valid);
	Cache cache();
	void reset();
}

enum SLICE_TYPE {
	NORMAL_SLICE,
	CHINESE_SLICE,
	SURROGATE_SLICE,
	TOKEN_SLICE,
	EMOJI_SLICE,
	IMAGE_SLICE,
	
	NEWLINE_SLICE,
	BACKSPCE_SLICE,
	
	IGNORE_SLICE,
	ESCAPE_SLICE, // for other types: esc#123
	
	UNKNOWN_SLICE,
}

class Cache {
	char[] m_buf;
	int m_offset;
	int m_len; 
	
	void set(char[] buf, int off, int len) {
		m_buf = buf;
		m_offset = off;
		m_len = len;
	}
	
	void reset() {
		m_buf = null;
		m_offset = 0;
		m_len = 0;
	}
}

class Range {
	private int m_beg;
	private int m_end;
	
	public void reset() {
		m_beg = 0;
		m_end = 0;
	}
	
	void setRange(int beg, int end) {
		m_beg = beg;
		m_end = end;
	}
	
	int beg() {
		return m_beg;
	}
	
	int end() {
		return m_end;
	}
}

abstract class ValidCacheCb implements Callback {
	private Cache m_cache;
	private Range m_range;
	private boolean m_valid;
	
	ValidCacheCb() {
		m_cache = new Cache();
		m_range = new Range();
	}
	
	public void reset() {
		m_cache.reset();
		m_range.reset();
		m_valid = false;
	}
	
	public int beg() {
		return m_range.beg();
	}
	
	public int end() {
		return m_range.end();
	}
	
	public boolean isValid() {
		return m_valid;
	}
	
	public void setValid(boolean valid) {
		m_valid = valid;
	}
	
	public void setRange(int beg, int end) {
		m_range.setRange(beg, end);
	}
	
	public Cache cache() {
		return m_cache;
	}
}

class OriginCb extends ValidCacheCb {
	
}

class DimenCb extends ValidCacheCb {
	DocView m_doc;
	int m_retW;
	int m_retH;
	int m_retBaseY;
	
	DimenCb(DocView doc) {
		m_doc = doc;
	}
	
	public void reset() {
		super.reset();
		m_retW = m_retH = m_retBaseY = 0;
	}
}

class WidthCb extends ValidCacheCb {
	DocView m_doc;
	int m_retW;
	
	WidthCb(DocView doc) {
		m_doc = doc;
	}
	
	public void reset() {
		super.reset();
		m_retW = 0;
	}
}

class DrawLineCb extends ValidCacheCb {
	DocView m_doc;
	Graphics m_g;
	int m_x;
	int m_y;
	int m_h;
	int m_textH;
	int m_baseY;
	int m_retW;
	
	DrawLineCb(DocView doc, Graphics g) {
		m_doc = doc;
		m_g = g;
	}
	
	public void reset() {
		super.reset();
		m_retW = 0;
	}
}

class FillLineCb extends ValidCacheCb {
	DocView m_doc;
	Graphics m_g;
	int m_x;
	int m_y;
	int m_h;
	int m_textH;
	int m_baseY;
	int m_retW;
	
	FillLineCb(DocView doc, Graphics g) {
		m_doc = doc;
		m_g = g;
	}
	
	public void reset() {
		super.reset();
		m_retW = 0;
	}
}
