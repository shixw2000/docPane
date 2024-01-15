package my.demo.doc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.TreeSet;

class Conf { 
	int m_width;
	int m_height;
	int m_pageW;
	int m_pageH;
	int m_min_w;
	int m_min_h;
	int m_status_w;
	int m_status_h;
	int m_status_baseY;
	int[] m_def_attrs;
	int m_status_font;
	int m_status_color;
	int m_fill_color;
	
	Conf() {
		m_min_w = 60;
		m_min_h = 50;
		m_def_attrs = new int[DocFormat.MAX_FORMAT_SIZE];
	} 
	
	void setSize(int w, int h) {
		if (m_min_w > w) {
			w = m_min_w;
		}
		
		if (m_min_h > h) {
			h = m_min_h;
		}
		
		m_width = w;
		m_height = h;
		
		m_pageW = m_width - m_status_w;
		m_pageH = m_height - m_status_h;
	}
}

public class DocView { 
	private FormatConf m_formatConf;
	private Conf m_config;
	private DocPane m_component;
	private int m_last_font;
	private int m_last_fore_color;
	private int m_last_back_color; 
	private TreeSet<String> m_cn_fonts;
	private TreeSet<String> m_emoji_fonts;
	private RenderingHints hints;
	
	DocView(DocPane component) { 
		m_component = component;
		m_formatConf = new FormatConf();
		m_config = new Conf();
		
		m_cn_fonts = new TreeSet<String>();
		m_emoji_fonts = new TreeSet<String>();
		hints = new RenderingHints(null);
		
		init(); 
	}
	
	private void init() {
		initConf();
		initRender();
		
		m_cn_fonts.add("Dialog");
		m_emoji_fonts.add("Segoe UI Emoji");
		
		m_last_font = DocFormat.DEF_ID_VAL;
		m_last_fore_color = DocFormat.DEF_ID_VAL;
		m_last_back_color = DocFormat.DEF_ID_VAL; 
	}
	
	private void initRender() {
		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	private void initConf() {
		int index = 0; 
		
		for (EnumAttr en: EnumAttr.values()) {
			index = en.ordinal();
			
			switch (en) {
			case ATTR_FONT:
				m_config.m_def_attrs[index] = creatFont("Dialog", Font.ITALIC, 24);
				break;
				
			case ATTR_FORE_COLOR:
				m_config.m_def_attrs[index] = creatColor(Color.BLACK);
				break;
				
			case ATTR_BACK_COLOR:
				m_config.m_def_attrs[index] = creatColor(Color.WHITE);
				break;
				
			default:
				break;
			}
		} 
		
		m_config.m_status_font = defAttr(EnumAttr.ATTR_FONT);
		m_config.m_status_color = creatColor(Color.BLACK);
		m_config.m_fill_color = creatColor(Color.ORANGE); 
		
		initStatusBar(); 
		
		m_config.setSize(350, 250);
	}
	
	private void initStatusBar() {
		FontMetrics fm = getFM(m_config.m_status_font);
		
		m_config.m_status_h = fm.getHeight();
		m_config.m_status_w = fm.stringWidth("00:");
		m_config.m_status_baseY = fm.getAscent();
	}
	
	private int adjustFont(int id, TreeSet<String> sets) {
		FontItem item = fontItem(id);
		String name = null;
		
		if (sets.contains(item.getFontName())) {
			return id;
		} else {
			name = sets.first();
			
			return creatFont(name, item.getStyle(), item.getSize());
		}
	}
	
	public int adjustCn(int id) {
		return adjustFont(id, m_cn_fonts);
	}
	
	public int adjustEmoji(int id) {
		return adjustFont(id, m_emoji_fonts);
	}
	
	public int defAttr(EnumAttr en) {
		return attr(en, m_config.m_def_attrs);
	}
	
	public int attr(EnumAttr en, int[] attrs) {
		return attrs[en.ordinal()];
	}
	
	public int statusFont() {
		return m_config.m_status_font;
	}
	
	public int statusColor() {
		return m_config.m_status_color;
	}
	
	public int fillColor() {
		return m_config.m_fill_color;
	}
	
	public int minW() {
		return m_config.m_min_w;
	}
	
	public int minH() {
		return m_config.m_min_h;
	}
	
	public int statusH() {
		return m_config.m_status_h;
	}
	
	public int statusBaseY() {
		return m_config.m_status_baseY;
	}
	
	public int statusW() {
		return m_config.m_status_w;
	}
	
	void clear() {
	}
	
	private void _setBackColor(Graphics g, int id) {
		Graphics2D g2d = (Graphics2D)g;
		Color clr = null;
		
		if (DocFormat.isDefVal(id)) {
			id = defAttr(EnumAttr.ATTR_BACK_COLOR);
		}
		
		clr = m_formatConf.getColorItem(id).getColor();
		
		g2d.setBackground(clr);
	}
	
	private void _setForeColor(Graphics g, int id) {
		Color clr = null;
		
		if (DocFormat.isDefVal(id)) {
			id = defAttr(EnumAttr.ATTR_FORE_COLOR);
		}
		
		clr = m_formatConf.getColorItem(id).getColor();
		
		g.setColor(clr);
	} 
	
	private void _setFont(Graphics g, int id) {
		FontItem item = fontItem(id);
		
		g.setFont(item.getFont());
	}
	
	public void setBackColor(Graphics g, int id) {
		if (m_last_back_color != id) {
			_setBackColor(g, id);
			
			m_last_back_color = id;
		}
	}
	
	public void setForeColor(Graphics g, int id) {
		if (m_last_fore_color != id) {
			_setForeColor(g, id);
			
			m_last_fore_color = id;
		}
	}

	public void setFont(Graphics g, int id) {
		if (m_last_font != id) {
			_setFont(g, id);
			
			m_last_font = id;
		}
	}
	
	private void setRender(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHints(hints);
	}
	
	public void reset(Graphics g) { 
		setRender(g);
		
		_setFont(g, defAttr(EnumAttr.ATTR_FONT)); 
		
		_setBackColor(g, defAttr(EnumAttr.ATTR_BACK_COLOR)); 
		clearRect(g, 0, 0, width(), height());
		
		_setForeColor(g, defAttr(EnumAttr.ATTR_FORE_COLOR)); 
		
		m_last_font = DocFormat.DEF_ID_VAL;
		m_last_fore_color = DocFormat.DEF_ID_VAL;
		m_last_back_color = DocFormat.DEF_ID_VAL;
	}
	
	public void reSize(int w, int h) {
		m_config.setSize(w, h);
		
		m_component.reSize(width(), height()); 
	}
	
	int width() {
		return m_config.m_width;
	}
	
	int height() {
		return m_config.m_height;
	}
	
	public FontItem fontItem(int id) {
		FontItem desc = null;
		
		if (DocFormat.isDefVal(id)) {
			id = defAttr(EnumAttr.ATTR_FONT);
		}
		
		desc = m_formatConf.getFontItem(id);
		return desc;
	}
	
	public FontMetrics getFM(int id) { 
		Font font = fontItem(id).getFont();
		
		return m_component.getFontMetrics(font);
	}
	
	public int maxW() {
		return m_config.m_pageW;
	}
	
	public int pageH() {
		return m_config.m_pageH;
	}
	
	public int widthCache(Cache cache, FontMetrics fm) {
		if (0 < cache.m_len) {
			return fm.charsWidth(cache.m_buf, cache.m_offset, cache.m_len);
		} else {
			return 0;
		}
	}
	
	public void drawCache(Graphics g, int x, int y, int baseY, Cache cache) {
		if (0 < cache.m_len) { 
			drawChars(g, x, y, baseY, cache.m_buf, cache.m_offset, cache.m_len);
		}
	}
	
	public void drawChars(Graphics g, int x, int y, int baseY, 
			char[] data, int off, int len) {
		g.drawChars(data, off, len, x, y + baseY);
	}
	
	public void drawString(Graphics g, int x, int y, int baseY, String txt) {
		g.drawString(txt, x, y + baseY);
	}
	
	public void drawLine(Graphics g, int x1, int y1, int x2, int y2) { 
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void drawRect(Graphics g, int x, int y, int w, int h) { 
		g.drawRect(x, y, w, h); 
	}
	
	public void drawImage(Graphics g, Image img, int x, int y, int w, int h) { 
		g.drawImage(img, x, y, w, h, null); 
	}
	
	public void fillRect(Graphics g, int x, int y, int w, int h) { 
		g.fillRect(x, y, w, h); 
	}
	
	public void clearRect(Graphics g, int x, int y, int w, int h) {
		g.clearRect(x, y, w, h);
	}
	
	public void fillOval(Graphics g, int x, int y, int w, int h) { 
		g.fillOval(x, y, w, h);
	}
	
	public void fillArc(Graphics g, int x, int y, int w, int h, int arc1, int arc2) { 
		g.fillArc(x, y, w, h, arc1, arc2);
	}
	
	public int creatColor(Color c) {
		int id =  m_formatConf.creatColor(c.getRGB());
		
		return id;
	}
	
	public int creatFont(String fontFam, int fontStyle, int fontSize) {
		int id =  m_formatConf.creatFont(fontFam, fontStyle, fontSize);
		
		return id;
	}
	
	public void show() {
		m_component.bsShow();
//		m_component.repaint();
	}
}
