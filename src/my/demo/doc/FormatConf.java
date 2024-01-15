package my.demo.doc;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class FontItem implements Comparable<FontItem> {
	private String m_name;
	private int m_style;
	private int m_size;
	private Font m_font;
	
	FontItem(String name, int style, int size) {
		m_name = name;
		m_style = style;
		m_size = size;
	}
	
	void setFont(Font f) {
		m_font = f;
	}
	
	Font getFont() {
		return m_font;
	}
	
	String getFontName() {
		return m_name;
	}
	
	int getSize() {
		return m_size;
	}
	
	int getStyle() {
		return m_style;
	}
	
	public int compareTo(FontItem other) {
		if (this.m_size != other.m_size) {
			return this.m_size - other.m_size;
		} else if (this.m_style != other.m_style) {
			return this.m_style - other.m_style;
		} else {
			return this.m_name.compareTo(other.m_name);
		}
	}
}

class ColorItem implements Comparable<ColorItem> {
	private int m_clr;
	private Color m_color;
	
	ColorItem(int clr) {
		m_clr = clr;
	}
	
	void setColor(Color c) {
		m_color = c;
	}
	
	Color getColor() {
		return m_color;
	}
	
	public int compareTo(ColorItem other) {
		return this.m_clr - other.m_clr;
	}
}

public class FormatConf {
	private Map<FontItem, Integer> m_set_font; 
	private Map<ColorItem, Integer> m_set_color;
	private ArrayList<FontItem> m_list_font;
	private ArrayList<ColorItem> m_list_color;
	
	public FormatConf() {
		m_set_font = new TreeMap<FontItem, Integer>();
		m_set_color = new TreeMap<ColorItem, Integer>();
		m_list_font = new ArrayList<FontItem>();
		m_list_color = new ArrayList<ColorItem>();
	}
	
	public int creatFont(String name, int style, int size) {
		FontItem item = new FontItem(name, style, size);
		Font font = null;
		int id = 0;
		
		if (m_set_font.containsKey(item)) {
			id = m_set_font.get(item);
		} else {
			font = new Font(name, style, size);
			item.setFont(font);
			
			id = m_list_font.size();
			m_list_font.add(item);
			m_set_font.put(item, id);
		}
			
		return id;
	}
	
	public int creatColor(int clr) {
		ColorItem item = new ColorItem(clr);
		Color color = null;
		int id = 0;
		
		if (m_set_color.containsKey(item)) {
			id = m_set_color.get(item);
		} else {
			color = new Color(clr);
			item.setColor(color);
			
			id = m_list_color.size();
			m_list_color.add(item);
			m_set_color.put(item, id);
		} 
			
		return id;
	}
	
	public FontItem getFontItem(int id) {
		return m_list_font.get(id);
	}
	
	public ColorItem getColorItem(int id) {
		return m_list_color.get(id);
	}
}
