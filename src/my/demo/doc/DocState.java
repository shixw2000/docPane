package my.demo.doc;

public class DocState {
	private PageState m_pageStat;
	private PageState m_begStat;
	private final Axis m_last;
	private final Axis m_curr;
	private final PosInfo m_posInfo;
	private int m_usedH;
	private boolean m_selected;
	
	DocState() {
		m_begStat = new PageState();
		m_pageStat = new PageState();
		m_last = new Axis();
		m_curr = new Axis();
		m_posInfo = new PosInfo();
	}
	
	public int usedH() {
		return m_usedH;
	}
	
	public void setUsedH(int h) {
		m_usedH = h;
	}
	
	PageState begStat() {
		return m_begStat;
	}
	
	PageState pageStat() {
		return m_pageStat;
	}
	
	public Axis last() {
		return m_last;
	}
	
	public Axis curr() {
		return m_curr;
	}
	
	public PosInfo posInfo() {
		return m_posInfo;
	}
	
	public void disableSel() {
		if (m_selected) {
			m_selected = false;
		}
	}
	
	public void enableSel() {
		if (!m_selected) {
			m_last.setRow(m_curr.row());
			m_last.setCol(m_curr.col());
			
			m_selected = true;
		}
	}
	
	public boolean selected() {
		return m_selected;
	}
	
	public void display(String prompt) {
		Axis axis = curr();
		PageState begStat = begStat();
		PageState nowStat = pageStat();
		PosInfo posInfo = posInfo();
		
		System.out.printf("display<%s>| axis=(%d, %d)| pos=(%d, %d)|"
				+ " beg=(%d, %d)| currStat=(%d, %d)\n",
				prompt,
				axis.row(), axis.col(), posInfo.pos(), posInfo.x(), 
				begStat.pageNo(), begStat.pageLine(),
				nowStat.pageNo(), nowStat.pageLine());
	}
}

class Axis implements Comparable<Axis>, Cloneable {
	private int m_row;
	private int m_col;
	
	void reset() {
		m_row = m_col= 0;
	}
	
	void set(Axis other) {
		m_row = other.m_row;
		m_col = other.m_col;
	}
	
	void setRow(int row) {
		m_row = row > 0 ? row : 0;
	}
	
	void setCol(int col) {
		m_col = col > 0 ? col : 0;
	}
	
	int row() {
		return m_row;
	}
	
	int col() {
		return m_col;
	}
	
	@Override
	public int compareTo(Axis other) {
		if (m_row < other.m_row ||
				(m_row == other.m_row 
				&& m_col < other.m_col)) {
			return -1;
		} else if (m_row == other.m_row && 
				m_col == other.m_col) {
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	public Axis clone() {
		try {
			Axis axis = (Axis)super.clone();
			
			return axis;
		} catch (Exception e) {
			return null;
		}
	}
}

class PosInfo {
	private int m_pos;
	private int m_x;
	
	void reset() {
		m_pos = 0;
		m_x = 0;
	}
	
	int x() {
		return m_x;
	}
	
	int pos() {
		return m_pos;
	}
	
	void setPosX(int pos, int x) {
		m_pos = pos;
		m_x = x;
	}
}

class PageState implements Comparable<PageState>, Cloneable {
	private int m_pageNo;
	private int m_page_line;
	
	void reset() {
		m_pageNo = 0;
		m_page_line = 0;
	}
	
	boolean isZero() {
		return 0 == m_pageNo && 0 == m_page_line;
	}
	
	void setPageInfo(PageState other) {
		m_pageNo = other.m_pageNo;
		m_page_line = other.m_page_line;
	}
	
	void setPageInfo(int pageNo, int pageLine) {
		m_pageNo = pageNo;
		m_page_line = pageLine;
	}
	
	int pageNo() {
		return m_pageNo;
	}
	
	int pageLine() {
		return m_page_line;
	}
	
	@Override
	public int compareTo(PageState other) {
		if (m_pageNo < other.m_pageNo ||
				(m_pageNo == other.m_pageNo 
				&& m_page_line < other.m_page_line)) {
			return -1;
		} else if (m_pageNo == other.m_pageNo && 
				m_page_line == other.m_page_line) {
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	public PageState clone() {
		try {
			PageState page = (PageState)super.clone();
			
			return page;
		} catch (Exception e) {
			return null;
		}
	}
}
