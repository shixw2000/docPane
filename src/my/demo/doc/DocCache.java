package my.demo.doc;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;


abstract class CacheSlice implements Slice {
	private int m_off;
	private int m_length;
	
	CacheSlice(int off, int len) {
		m_off = off;
		m_length = len;
	} 
	
	abstract char[] data(); 
	abstract CacheSlice creat(int off, int len);
	abstract int offset(int i);
	
	public final int offset() {
		return m_off;
	}
	
	@Override
	public final int length() {
		return m_length;
	}
	
	public boolean canMerge(Slice other) {
		if (this.getClass() == other.getClass() &&
				this.type() == other.type()) {
			CacheSlice cs = (CacheSlice)other;
			
			if (this.offset(m_length) == cs.offset(0)) {
				return true;
			}
		}
		
		return false;
	}
	
	public CacheSlice merge(Slice other) {
		if (this.getClass() == other.getClass() &&
				this.type() == other.type()) {
			CacheSlice cs = (CacheSlice)other;
			
			if (this.offset(m_length) == cs.offset(0)) {
				return creat(offset(), m_length + cs.m_length);
			}
		}
		
		return null;
	}
	
	public CacheSlice split(int beg, int end) {
		if (isValid(beg, end)) {
			int off = offset(beg);
			int length = end - beg;
			
			return creat(off, length);
		} else {
			return null;
		}
	}
	
	boolean isValid(int beg, int end) {
		if (0 <= beg && beg < end && end <= m_length) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public final void handle(Callback cb) {
		int beg = cb.beg();
		int end = cb.end();
		
		if (isValid(beg, end)) {
			int off1 = offset(beg);
			int off2 = offset(end);
			Cache cache = cb.cache();
			
			cache.set(data(), off1, off2 - off1);
		} else {
			System.out.println(">>> invalid cache cb range!!");
		}
	}
}

abstract class MetaSlice implements Slice { 
	public final int length() {
		return 1;
	}
	
	public final boolean canMerge(Slice other) {
		return false;
	}
	
	public final Slice merge(Slice other) {
		return null;
	}
	
	public final MetaSlice split(int beg, int end) {
		return null;
	}
	
	final boolean isValid(int beg, int end) {
		if (0 == beg && 1 == end) {
			return true;
		} else {
			return false;
		}
	}
}

final class TokenSlice extends MetaSlice { 
	private static final TokenSlice[] TOKENS = new TokenSlice[] {
			new TokenSlice(SLICE_TYPE.TOKEN_SLICE, KeyEvent.VK_TAB, "    "),
			new TokenSlice(SLICE_TYPE.NEWLINE_SLICE, KeyEvent.VK_ENTER, ""),
			new TokenSlice(SLICE_TYPE.IGNORE_SLICE, '\r', ""),
			new TokenSlice(SLICE_TYPE.ESCAPE_SLICE, KeyEvent.VK_ESCAPE, ""), //escape
			new TokenSlice(SLICE_TYPE.BACKSPCE_SLICE, KeyEvent.VK_BACK_SPACE, ""),
			new TokenSlice(SLICE_TYPE.BACKSPCE_SLICE, KeyEvent.VK_DELETE, ""),
	};
	
	public static boolean isTokenChar(char c) {
		for (TokenSlice t: TOKENS) {
			if (c == t.m_origin[0]) {
				return true;
			}
		}
		
		return false;
	}
	
	public static TokenSlice getSlice(char c) {
		for (TokenSlice t: TOKENS) {
			if (c == t.m_origin[0]) {
				return t;
			}
		}
		
		return null;
	}
	
	private SLICE_TYPE m_type;
	private char[] m_origin;
	private char[] m_desc;
	
	private TokenSlice(SLICE_TYPE type, int c, String s) {
		m_type = type;
		m_origin = new char[] { (char)c };
		m_desc = s.toCharArray();
	}
	
	@Override
	public SLICE_TYPE type() {
		return m_type;
	}
	
	public final void handle(Callback cb) {
		if (isValid(cb.beg(), cb.end())) { 
			Cache cache = cb.cache();
			
			if (cb instanceof OriginCb) {
				cache.set(m_origin, 0, 1);
			} else {
				cache.set(m_desc, 0, m_desc.length);
			}
		} else {
			System.out.println(">>> invalid token cb range!!");
		}
	}
}

class ImageSlice extends MetaSlice { 
	private static final char[] DEF_UNKNOWN_IMG_TXT = "Invalid_img!".toCharArray();
	private Image m_img;
	private String m_path;
	private int m_w;
	private int m_h;
	private int m_imgW;
	private int m_imgH;
	
	private ImageSlice(String path) {
		m_path = path;
	}
	
	public static ImageSlice loadImg(DocView doc, String path) {
		ImageSlice slice = new ImageSlice(path);
		
		try (InputStream in = new FileInputStream(path)) {
			Image img = ImageIO.read(in); 
			
			if (null != img) {
				int maxW = doc.maxW();
				int maxH = doc.pageH() / 3;
				int w = img.getWidth(null);
				int h = img.getHeight(null);
				
				slice.m_img = img;
				slice.m_imgW = w;
				slice.m_imgH = h;
				
				slice.adjustDim(maxW, maxH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return slice;
	}
	
	public String path() {
		return m_path;
	}
	
	public SLICE_TYPE type() {
		return SLICE_TYPE.IMAGE_SLICE;
	}
	
	public final void handle(Callback cb) {
		if (isValid(cb.beg(), cb.end())) { 
			if (null != m_img) {
				if (cb instanceof WidthCb) {
					handleWidthCb((WidthCb)cb);
				} else if (cb instanceof DrawLineCb) {
					handleDrawLineCb((DrawLineCb)cb);
				} else if (cb instanceof FillLineCb) {
					handleFillLineCb((FillLineCb)cb);
				} else if (cb instanceof DimenCb) {
					handleDimCb((DimenCb)cb);
				} else if (cb instanceof OriginCb) {
					/* no text */
					cb.cache().reset();
				} else {
					System.out.println(">>> invalid image cb type!!");
				}
				
				cb.setValid(true);
			} else {
				Cache cache = cb.cache();
			
				cache.set(DEF_UNKNOWN_IMG_TXT, 0, DEF_UNKNOWN_IMG_TXT.length);
			}
		} else {
			System.out.println(">>> invalid image cb range!!");
		}
	}
	
	private void adjustDim(int w, int h) {
		if (w < m_imgW || h < m_imgH) {
			float ratioW = (float)m_imgW / w;
			float ratioH = (float)m_imgH / h;
			float ratio = ratioW > ratioH ? ratioW : ratioH;
			
			m_w = (int)(m_imgW / ratio);
			m_h = (int)(m_imgH / ratio);
		} else {
			m_w = m_imgW;
			m_h = m_imgH;
		}
	}
	
	private void handleDimCb(DimenCb cb) {
		cb.m_retW = m_w;
		cb.m_retH = m_h;
		cb.m_retBaseY = 0;
	}
	
	private void handleWidthCb(WidthCb cb) {
		cb.m_retW = m_w;
	}
	
	private void handleDrawLineCb(DrawLineCb cb) {
		DocView doc = cb.m_doc;
		Graphics g = cb.m_g;
		int off = cb.m_h - m_h;
		
		doc.drawImage(g, m_img, cb.m_x, cb.m_y+off, m_w, m_h);
		cb.m_retW = m_w;
	}
	
	private void handleFillLineCb(FillLineCb cb) {
		DocView doc = cb.m_doc;
		Graphics g = cb.m_g;
		int off = cb.m_h - m_h;
		
		doc.fillRect(g, cb.m_x, cb.m_y+off, m_w, m_h);
		cb.m_retW = m_w;
	}
}

class CacheData {
	private int m_capacity;
	private int m_size;
	private char m_buffer[];
	
	public void store(char str[], int off, int len) { 
		if (m_size + len > m_capacity) {
			m_capacity += 0x100 + len;

			char[] s = new char[m_capacity];
			
			if (0 < m_size) {
				System.arraycopy(m_buffer, 0, s, 0, m_size);
			}
			
			m_buffer = s; 
		} 
		
		System.arraycopy(str, off, m_buffer, m_size, len);
		m_size += len;
	}
	
	public void store(char c) {
		if (m_size >= m_capacity) {
			m_capacity += 0x100;

			char[] s = new char[m_capacity];
			
			if (0 < m_size) {
				System.arraycopy(m_buffer, 0, s, 0, m_size);
			}
			
			m_buffer = s; 
		} 
		
		m_buffer[m_size] = c; 
		++m_size;
	}
	
	public char[] data() {
		return m_buffer;
	}
	
	public int size() {
		return m_size;
	}
	
	public void clear() {
		m_size = m_capacity = 0;
		m_buffer = null;
	}
}

public class DocCache {
	private CacheData m_cache;
	
	public DocCache() {
		m_cache = new CacheData();
	}
	
	public void clear() {
		m_cache.clear();
	}
	
	private class CommSlice extends CacheSlice {
		SLICE_TYPE m_type;
		
		CommSlice(SLICE_TYPE type, int off, int len) {
			super(off, len);
			m_type = type;
		}
		
		public SLICE_TYPE type() {
			return m_type;
		}
		
		char[] data() {
			return m_cache.data();
		}
		
		int offset(int i) {
			return offset() + i;
		}
		
		CommSlice creat(int off, int len) {
			return new CommSlice(m_type, off, len); 
		}
	}
	
	private class SurrogateSlice extends MetaSlice {
		private static final int DEF_SURROGATE_SIZE = 2;
		
		private int m_off;
		
		SurrogateSlice(int off) {
			m_off = off;
		}
		
		public SLICE_TYPE type() {
			return SLICE_TYPE.SURROGATE_SLICE;
		}
		
		public final void handle(Callback cb) {
			if (isValid(cb.beg(), cb.end())) { 
				Cache cache = cb.cache();
				
				cache.set(m_cache.data(), m_off, DEF_SURROGATE_SIZE);
			} else {
				System.out.println(">>> invalid surrogate cb range!!");
			}
		}
	}
	
	CacheSlice creatCache(SLICE_TYPE type, 
			char[] data, int off, int len) {
		int pos = m_cache.size();
		
		m_cache.store(data, off, len);
		return new CommSlice(type, pos, len);
	}
	
	private CacheSlice creatCache(SLICE_TYPE type, char c) {
		int pos = m_cache.size();
		
		m_cache.store(c);
		return new CommSlice(type, pos, 1);
	}
	
	private SurrogateSlice creatSurrogate(char c1, char c2) {
		int pos = m_cache.size();
		
		m_cache.store(c1);
		m_cache.store(c2);
		return new SurrogateSlice(pos);
	}
	
	private TokenSlice creatToken(char c) {
		TokenSlice ts = TokenSlice.getSlice(c);
		
		return ts;
	}
	
	public ImageSlice creatImage(DocView doc, String path) {
		ImageSlice is = ImageSlice.loadImg(doc, path);
		
		return is;
	}
	
	Slice _creat(char c) { 
		if (TokenSlice.isTokenChar(c)) {
			return creatToken(c);
		} else if (DocTool.isChnChar(c)) {
			return creatCache(SLICE_TYPE.CHINESE_SLICE, c);
		} else {
			return creatCache(SLICE_TYPE.NORMAL_SLICE, c);
		}
	}
	
	Slice _creat(char c1, char c2) {
		return creatSurrogate(c1, c2);
	}
	
	Slice creat(char... str) {
		if (1 == str.length) {
			return _creat(str[0]);
		} else if (2 == str.length && 
				DocTool.isSupplementary(str[0], str[1])) {
			return _creat(str[0], str[1]);
		} else {
			return null;
		}
	}
	
	ListRoot<Slice> creat(String text) {
		ListRoot<Slice> list = new ListRoot<Slice>();
		char[] data = text.toCharArray();
		char ch1 = '\0';
		char ch2 = '\0';
		Slice s = null;
		
		for (int i=0; i<data.length; ++i) {
			ch1 = data[i];
			if (DocTool.isSurrogate(ch1)) {
				if (i + 1 < data.length) {
					ch2 = data[i+1];
					if (DocTool.isSupplementary(ch1, ch2)) {
						++i;
						
						s = _creat(ch1, ch2);
						list.push_back(s); 
					}
				}
			} else if (TokenSlice.isTokenChar(ch1)) {
				s = creatToken(ch1);
				if (SLICE_TYPE.TOKEN_SLICE == s.type() ||
						SLICE_TYPE.NEWLINE_SLICE == s.type()) {
					list.push_back(s);
				}
			} else if (DocTool.isChnChar(ch1)) {
				s = creatCache(SLICE_TYPE.CHINESE_SLICE, ch1);
				list.push_back(s);
			} else {
				s = creatCache(SLICE_TYPE.NORMAL_SLICE, ch1);
				list.push_back(s);
			}
		}
		
		return list;
	}
}
