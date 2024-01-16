package my.demo.doc;

import java.awt.FontMetrics;
import java.awt.Graphics;

class Attribute implements Element { 
	private Slice m_slice;
	private int[] m_attrs;
	private FontMetrics m_fm;
	
	Attribute(Slice s, int[] attrs) {
		m_slice = s;
		m_attrs = attrs;
	}
	
	public SLICE_TYPE type() {
		return m_slice.type();
	}
	
	public final int length() {
		return m_slice.length();
	}
	
	final int[] attrs() {
		return m_attrs;
	}
	
	int fontId() {
		return m_attrs[EnumAttr.ATTR_FONT.ordinal()];
	}
	
	int foreClrId() {
		return m_attrs[EnumAttr.ATTR_FORE_COLOR.ordinal()];
	}
	
	int backClrId() {
		return m_attrs[EnumAttr.ATTR_BACK_COLOR.ordinal()];
	}
	
	FontMetrics getFm(DocView doc) {
		if (null == m_fm) {
			m_fm = doc.getFM(fontId());
		}
		
		return m_fm;
	}
	
	public void calcW(WidthCb cb) {
		handle(cb);
		
		if (!cb.isValid()) {
			DocView doc = cb.m_doc;
			FontMetrics fm = getFm(doc);
			Cache cache = cb.cache();
			
			cb.m_retW = doc.widthCache(cache, fm);
		}
	}
	
	public void calcDim(DimenCb cb) {
		handle(cb);
		
		if (!cb.isValid()) {
			DocView doc = cb.m_doc;
			FontMetrics fm = getFm(doc);
			Cache cache = cb.cache();
			
			cb.m_retW = doc.widthCache(cache, fm);
			cb.m_retH = fm.getHeight();
			cb.m_retBaseY = fm.getAscent();
		}
	}
	
	final boolean chkRange(Callback cb) {
		if (0 <= cb.beg() && cb.beg() < cb.end() && 
				cb.end() <= length()) {
			return true;
		} else {
			return false;
		}
	}
	
	final void handle(Callback cb) {
		m_slice.handle(cb);
	}
	
	final boolean isAll(Callback cb) {
		if (0 == cb.beg() && length() == cb.end()) {
			return true;
		} else {
			return false;
		}
	}
	
	final ViewEx creat(int beg, int end, int w, int h, int baseY) {
		int len = length();
		ViewEx ve = null;
		
		if (0 == beg && end == len) {
			ve = new ViewEx(m_slice, m_attrs, w, h, baseY);
		} else {
			ve = new ViewEx(m_slice.split(beg, end), m_attrs, w, h, baseY);
		}
		
		return ve;
	}
}

class ViewEx extends Attribute {
	private int m_w;
	private int m_h;
	private int m_baseY;
	
	ViewEx(Slice s, int[] attrs, int w, int h, int baseY) {
		super(s, attrs);
		
		m_w = w;
		m_h = h;
		m_baseY = baseY;
	}
	
	public int w() {
		return m_w;
	}
	
	public int h() {
		return m_h;
	}
	
	public int baseY() {
		return m_baseY;
	}
	
	public void calcW(WidthCb cb) {
		if (isAll(cb)) {
			cb.m_retW = w();
		} else {
			super.calcW(cb);
		}
	}
	
	public void draw(DrawLineCb cb) {
		int font = fontId();
		int foreId = foreClrId();
		int backId = backClrId();
		DocView doc = cb.m_doc;
		Graphics g = cb.m_g;
		
		doc.setFont(g, font);
		doc.setForeColor(g, foreId); 
		doc.setBackColor(g, backId);
		
		handle(cb); 
		
		if (!cb.isValid()) { 
			Cache cache = cb.cache();
			int off = cb.m_h - cb.m_textH;
			
			if (isAll(cb)) {
				cb.m_retW = w();
			} else {
				FontMetrics fm = getFm(doc);
				
				cb.m_retW = doc.widthCache(cache, fm);
			}
			
			if (!DocFormat.isDefVal(backId)) {
				doc.clearRect(g, cb.m_x, cb.m_y+off, cb.m_retW, cb.m_textH); 
			}
			
			doc.drawCache(g, cb.m_x, cb.m_y+off, cb.m_baseY, cache); 
		}
	}
	
	public void fill(FillLineCb cb) {
		handle(cb);
		
		if (!cb.isValid()) {
			DocView doc = cb.m_doc;
			Graphics g = cb.m_g;
			int off = (cb.m_h - cb.m_textH) + (cb.m_baseY - m_baseY);
			
			if (isAll(cb)) {
				cb.m_retW = w();
			} else { 
				FontMetrics fm = getFm(doc);
				Cache cache = cb.cache();
				
				cb.m_retW = doc.widthCache(cache, fm);
			}
			
			doc.fillRect(g, cb.m_x, cb.m_y+off, cb.m_retW, m_h);
		}
	}
}

public class SegmentLine implements Element {
	Segment m_parent;
	int m_line;
	int m_pageNo;
	int m_pageLine;
	int m_row;
	int m_begCol;
	private int m_h;
	private int m_textH;
	private int m_baseY;
	private int m_w;
	private int m_cnt;
	private ListRoot<ViewEx> m_views;
	
	SegmentLine(DocView doc, Segment parent, int begCol) {
		m_parent = parent;
		m_begCol = begCol;
		
		m_views = new ListRoot<ViewEx>();
		
		setH(doc, DocFormat.DEF_ID_VAL);
	}
	
	void setPageRow(int pageNo, int pageLine, int line, int row) {
		m_pageNo = pageNo;
		m_pageLine = pageLine;
		m_line = line;
		m_row = row;
	}
	
	void addView(ViewEx view) {
		m_views.push_back(view);
		
		m_w += view.w();
		m_cnt += view.length(); 
		
		if (m_h < view.h()) {
			m_h = view.h();
		}
		
		if (SLICE_TYPE.IMAGE_SLICE != view.type()) { 
			if (m_textH < view.h()) {
				m_textH = view.h();
			}
			
			if (m_baseY < view.baseY()) {
				m_baseY = view.baseY();
			}
		} 
	}
	
	void setH(DocView doc, int font) {
		FontMetrics fm = doc.getFM(font);
		int h = fm.getHeight();
		int baseY = fm.getAscent();
		
		if (m_h < h) {
			m_h = h;
		}
		
		if (m_textH < h) {
			m_textH = h;
		}
		
		if (m_baseY < baseY) {
			m_baseY = baseY;
		}
	}
	
	public int length() {
		return m_cnt;
	}
	
	int width() {
		return m_w;
	}
	
	int h() {
		return m_h;
	}
	
	int textH() {
		return m_textH;
	}
	
	int row() {
		return m_row;
	}
	
	int begCol() {
		return m_begCol;
	}
	
	int line() {
		return m_line;
	}
	
	int pageNo() {
		return m_pageNo;
	}
	
	int pageLine() {
		return m_pageLine;
	}
	
	int baseY() {
		return m_baseY;
	}
	
	public void findByWidth(DocView doc,
			int maxX, PosInfo posInfo) {
		int x = 0;
		int cnt = 0;
		
		if (0 < maxX) {
			for (ViewEx v: m_views) {
				if (v.w() < maxX) {
					x += v.w();
					maxX -= v.w();
					cnt += v.length();
				} else if (v.w() == maxX) {
					x += v.w();
					cnt += v.length();
					break;
				} else if (1 < v.length()) { 
					DocSegment.findByWidth(doc, v, 0, maxX, posInfo);
					
					x += posInfo.x();
					cnt += posInfo.pos(); 
					break;
				} else {
					break;
				}
			}
		}

		posInfo.setPosX(cnt, x); 
	}
	
	public void findByPos(DocView doc,
			int pos, PosInfo posInfo) {
		int x = 0;
		int cnt = 0;
		
		if (0 < pos) {
			for (ViewEx v: m_views) {
				if (v.length() < pos) {
					x += v.w();
					cnt += v.length();
					pos -= v.length();
				} else if (v.length() == pos) {
					x += v.w();
					cnt += v.length();
					break;
				} else if (1 < v.length()) {
					DocSegment.findByPos(doc, v, 0, pos, posInfo);
					
					x += posInfo.x();
					cnt += posInfo.pos(); 
					break;
				} else {
					break;
				}
			}
		}

		posInfo.setPosX(cnt, x); 
	}
	
	public void draw(int beg, int end, WidthCb wc, DrawLineCb dlc) {
		DocSegment.SelectCb1 cb = new DocSegment.SelectCb1() {
			
			@Override
			public void select(Element s, int beg1, int end1, boolean bHit) {
				ViewEx ve = (ViewEx)s;
				
				if (bHit) {
					dlc.reset();
					dlc.setRange(beg1, end1); 
					ve.draw(dlc);
					
					dlc.m_x += dlc.m_retW;
				} else { 
					wc.reset();
					wc.setRange(beg1, end1);
					ve.calcW(wc);
					
					dlc.m_x += wc.m_retW;
				}
			}
		};
		
		DocSegment.range(m_views, beg, end, cb);
	}
	
	public void fill(int beg, int end, WidthCb wc, FillLineCb flc) {
		DocSegment.SelectCb1 cb = new DocSegment.SelectCb1() {
			
			@Override
			public void select(Element s, int beg1, int end1, boolean bHit) {
				ViewEx ve = (ViewEx)s;
				
				if (bHit) {
					flc.reset();
					flc.setRange(beg1, end1);
					ve.fill(flc);
					
					flc.m_x += flc.m_retW;
				} else { 
					wc.reset();
					wc.setRange(beg1, end1);
					ve.calcW(wc);
					
					flc.m_x += wc.m_retW;
				}
			}
		};
		
		DocSegment.range(m_views, beg, end, cb);
	}
}
