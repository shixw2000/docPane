package my.demo.doc;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class DocPage {
	static class PageInfo {
		int m_pageNo;
		int m_begLine;
		int m_size;
		int m_h;
		
		PageInfo(int pageNo, int h, int beg, int size) {
			m_pageNo = pageNo;
			m_h = h;
			m_begLine = beg;
			m_size = size;
		}
	}
	
	private ListRoot<PageInfo> m_pages;
	private ListRoot<SegmentLine> m_lines;
	private int m_totalH;
	
	DocPage() {
		m_pages = new ListRoot<PageInfo>();
		m_lines = new ListRoot<SegmentLine>();
	} 
	
	void clear() {
		m_pages.clear();
		m_lines.clear();
		m_totalH = 0;
	}
	
	public int pageSize() {
		return m_pages.size();
	}
	
	public int lineSize() {
		return m_lines.size();
	}
	
	private SegmentLine getLine(int n) {
		return m_lines.getData(n);
	}
	
	private int totalH() {
		return m_totalH;
	}
	
	private int toIndex(PageState pageStat) {
		int pageNo = pageStat.pageNo();
		int pageLine = pageStat.pageLine();
		
		if (0 <= pageNo && pageNo < pageSize()) {
			PageInfo pi = m_pages.getData(pageNo);
		
			if (0 <= pageLine && pageLine < pi.m_size) {
				return pi.m_begLine  + pageLine;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	private void toPage(int n, PageState page) {
		SegmentLine segline = getLine(n);
		
		page.setPageInfo(segline.pageNo(),
				segline.pageLine());
	}
	
	public SegmentLine getLine(PageState pageStat) {
		int index = toIndex(pageStat);
		
		return getLine(index);
	}
	
	public void up(DocView doc, PageState pageStat, PosInfo posInfo) {
		SegmentLine segline = null;
		int line = 0;
		int x = posInfo.x();
		
		line = toIndex(pageStat);
		if (0 < line) {
			--line; 
			toPage(line, pageStat);
			
			segline = getLine(line); 
			segline.findByWidth(doc, x, posInfo); 
		} 
	}
	
	public void down(DocView doc, PageState pageStat, PosInfo posInfo) {
		SegmentLine segline = null;
		int line = 0;
		int x = posInfo.x();
		
		line = toIndex(pageStat);
		if (line + 1 < lineSize()) {
			++line; 
			toPage(line, pageStat);
			
			segline = getLine(line); 
			segline.findByWidth(doc, x, posInfo); 
		}
	}
	
	public void foreward(DocView doc, PageState pageStat, PosInfo posInfo) {
		SegmentLine preline = null;
		SegmentLine segline = null;
		int line = 0;
		int pos = posInfo.pos();
		
		line = toIndex(pageStat);
		segline = getLine(line); 
		
		if (0 < pos) {
			--pos; 
			segline.findByPos(doc, pos, posInfo); 
		} else if (0 < line) { 
			--line;
			toPage(line, pageStat);
			preline = getLine(line);
			
			if (preline.row() == segline.row()) {
				pos = preline.length() - 1;
			} else {
				pos = preline.length();
			}
			
			preline.findByPos(doc, pos, posInfo); 
		} else {
			/* at front of doc */
		}
	}
	
	public void backward(DocView doc, PageState pageStat, PosInfo posInfo) {
		SegmentLine nextline = null;
		SegmentLine segline = null;
		int line = 0;
		int pos = posInfo.pos();
		
		line = toIndex(pageStat);
		segline = getLine(line); 
		
		if (pos < segline.length()) {
			++pos;
			segline.findByPos(doc, pos, posInfo);
		} else if (line + 1 < lineSize()){
			++line;
			toPage(line, pageStat);
			nextline = getLine(line);
			
			if (nextline.row() == segline.row()) {
				pos = 1;
			} else {
				pos = 0;
			}
			
			nextline.findByPos(doc, pos, posInfo); 
		} else {
			/* at end of doc */
		}
	}
	
	public void updatePage(DocView doc, ListRoot<Segment> segments) {
		ListRoot<SegmentLine> lines = new ListRoot<SegmentLine>();
		ListRoot<PageInfo> pages = new ListRoot<PageInfo>();
		int maxH = doc.pageH();
		int row = 0;
		int h = 0;
		int totalH = 0;
		int pageNo = 0;
		int pageLine = 0;
		int currLine = 0;
		int begLine = 0;
		
		for (Segment segment: segments) { 
			for (SegmentLine line: segment.lines()) { 
				lines.push_back(line);
				totalH += line.h();
				
				/* attention here for lineno == 0 */
				if (h + line.h() <= maxH || 0 == pageLine) {
					line.setPageRow(pageNo, pageLine, currLine, row);
					++pageLine;
					
					h += line.h();
				} else {
					pages.push_back(new PageInfo(pageNo, h, begLine, pageLine));
					
					++pageNo;
					begLine = currLine;
					pageLine = 0;
					
					line.setPageRow(pageNo, pageLine, currLine, row);
					++pageLine;
					h = line.h();
				}
				
				++currLine;
			}
			
			++row;
		}
		
		if (0 < pageLine) {
			pages.push_back(new PageInfo(pageNo, h, begLine, pageLine));
		}
		
		m_totalH = totalH; 
		m_lines.swap(lines);
		m_pages.swap(pages);
	}
	
	public void page2Axis(PageState page, PosInfo info, Axis axis) {
		SegmentLine segline = null;
		int row = 0;
		int col = 0;
		int line = 0;
		
		line = toIndex(page);
		
		segline = getLine(line);
		row = segline.row();
		col = segline.begCol() + info.pos();
		
		axis.setRow(row);
		axis.setCol(col);
	}
	
	public void findByXY(DocView doc, int maxW, int maxH,
			PageState begStat, 
			int x, int y, PageState pageStat,
			PosInfo posInfo, Axis axis) {
		PosInfo info = new PosInfo();
		SegmentLine segline = null;
		int line1 = 0;
		int line2 = 0; 
		
		if (0 > x) {
			x = 0;
		} else if (maxW < x) {
			x = maxW;
		}
		
		if (0 > y) {
			y = 0;
		} else if (maxH <= y) {
			/* dec by one pixel */
			y = maxH - 1;
		}
		
		line1 = toIndex(begStat);
		line2 = above(line1, y); 
		segline = getLine(line2); 
		segline.findByWidth(doc, x, info);
		
		if (null != pageStat) {
			toPage(line2, pageStat);
		}
		
		if (null != posInfo) {
			posInfo.setPosX(info.pos(), info.x());
		}
		
		if (null != axis) {
			int row = segline.row();
			int col = segline.begCol() + posInfo.pos();
			
			axis.setRow(row);
			axis.setCol(col);
		}
	}
	
	public void fills(DocView doc, Graphics g, 
			int w, int h,
			PageState begStat, int maxH,
			PageState p1, int i1,
			PageState p2, int i2) {
		WidthCb wc = new WidthCb(doc);
		FillLineCb flc = new FillLineCb(doc, g);
		DocSegment.Selector s1 = new DocSegment.Selector();
		DocSegment.Selector s2 = new DocSegment.Selector();
		int line1 = 0;
		int line2 = 0; 
		int sline = 0;
		int eline = 0;
		
		DocSegment.SelectCb2 cb = new DocSegment.SelectCb2() {
			public void select(Element s, int beg, int end, boolean eof) {
				SegmentLine segline = (SegmentLine)s;
				
				flc.m_x = w; 
				flc.m_h = segline.h();
				flc.m_textH = segline.textH();
				flc.m_baseY = segline.baseY();
				segline.fill(beg, end, wc, flc);
				
				flc.m_y += segline.h();
			}
		};
		
		sline = toIndex(begStat);
		line1 = toIndex(p1);
		if (line1 < sline) {
			line1 = sline;
			i1 = 0;
		}
		
		eline = above(sline, maxH);
		line2 = toIndex(p2);
		if (line2 > eline) {
			line2 = eline;
			i2 = getLine(eline).length();
		}
		
		flc.m_x = 0;
		flc.m_y = rangeH(sline, line1);
		
		s1.set(line1, i1);
		s2.set(line2, i2);
		DocSegment.select(m_lines, s1, s2, cb); 
	}
	
	public void draw(DocView doc, Graphics g, int w, int h, 
			PageState begStat) {
		WidthCb wc = new WidthCb(doc);
		DrawLineCb dlc = new DrawLineCb(doc, g);
		int statusClr = doc.statusColor();
		int statusFont = doc.statusFont();
		DocSegment.Selector s1 = new DocSegment.Selector();
		DocSegment.Selector s2 = new DocSegment.Selector();
		int maxH = doc.pageH();
		SegmentLine segline = null;
		int line1 = 0;
		int line2 = 0; 
				
		DocSegment.SelectCb2 cb = new DocSegment.SelectCb2() {
			public void select(Element s, int beg, int end, boolean eof) {
				SegmentLine segline = (SegmentLine)s; 
				String prefix = String.format("%2d", segline.pageLine() + 1);
				
				doc.setFont(g, statusFont);
				doc.setForeColor(g, statusClr);
				doc.drawString(g, 0, dlc.m_y, segline.baseY(), prefix);
				
				dlc.m_x = w; 
				dlc.m_h = segline.h();
				dlc.m_textH = segline.textH();
				dlc.m_baseY = segline.baseY();
				segline.draw(beg, end, wc, dlc); 
				
				dlc.m_y += segline.h();
			}
		};
		
		dlc.m_x = 0;
		dlc.m_y = 0; 
		
		line1 = toIndex(begStat); 
		line2 = aboveWith(line1, maxH); 
		segline = getLine(line2); 
		s1.set(line1, 0);
		s2.set(line2, segline.length());
		
		DocSegment.select(m_lines, s1, s2, cb); 
	}
	
	/* here cannot use begStat, but just pageStat is valid */
	public int adjustBegPage(DocView doc, PageState begStat,
			PageState pageStat) { 
		int maxH = doc.pageH();
		int usedH = 0; 
		
		if (totalH() <= maxH) {
			begStat.reset();
			usedH = totalH();
		} else {
			int curr = 0;
			int begIndex = 0;
			int minIndex = 0;
			int maxIndex = 0;
			
			curr = toIndex(pageStat);
			begIndex = toIndex(begStat); 
			
			minIndex = belowWith(curr, maxH); 
			if (begIndex > curr) {
				begIndex = curr;
			} else if (begIndex < minIndex) {
				begIndex = minIndex;
			}
			
			toPage(begIndex, begStat);
			
			maxIndex = aboveWith(begIndex, maxH);
			usedH = rangeHWith(begIndex, maxIndex);
		} 
		
		return usedH;
	}
	
	/* [l1, l2)*/
	public int rangeH(int line1, int line2) {
		ListRoot<SegmentLine>.Node curr = null;
		SegmentLine segline = null;
		int h = 0;
		
		curr = m_lines.getNode(line1);
		while (line1 < line2 && m_lines.end() != curr) {
			segline = curr.data();
			h += segline.h();
			
			curr = curr.next();
			++line1;
		}
		
		return h;
	}
	
	/* [l1, l2]*/
	public int rangeHWith(int line1, int line2) {
		ListRoot<SegmentLine>.Node curr = null;
		SegmentLine segline = null;
		int h = 0;
		
		curr = m_lines.getNode(line1);
		while (line1 <= line2 && m_lines.end() != curr) {
			segline = curr.data();
			h += segline.h();
			
			curr = curr.next();
			++line1;
		}
		
		return h;
	}
	
	public int rangeH(PageState p1, PageState p2) {
		int h = 0;
		int index1 = toIndex(p1);
		int index2 = toIndex(p2);
		
		h = rangeH(index1, index2);
		
		return h;
	}
	
	public int rangeHWith(PageState p1, PageState p2) {
		int h = 0;
		int index1 = toIndex(p1);
		int index2 = toIndex(p2);
		
		h = rangeHWith(index1, index2);
		
		return h;
	}
	
	private int belowWith(int curr, int maxH) {
		SegmentLine segline = null;
		int prev = 0;
		
		segline = getLine(curr);
		maxH -= segline.h();
		prev = curr - 1;
		
		while (0 < maxH && 0 <= prev) { 
			segline = getLine(prev);
			
			if (segline.h() <= maxH) {
				maxH -= segline.h();
				curr = prev;
				--prev;
			} else {
				break;
			}
		}
		
		return curr;
	}
	
	private int aboveWith(int curr, int maxH) {
		SegmentLine segline = null;
		int next = curr;
		
		while (0 < maxH && next < lineSize()) { 
			segline = getLine(next);
			
			if (segline.h() <= maxH) {
				maxH -= segline.h();
				curr = next;
				++next;
			} else {
				break;
			}
		}
		
		return curr;
	}
	
	private int above(int curr, int maxH) {
		SegmentLine segline = null;
		int next = curr;
		
		while (0 < maxH && next < lineSize()) { 
			curr = next;
			segline = getLine(curr);
			
			if (segline.h() <= maxH) {
				maxH -= segline.h();
				
				++next;
			} else {
				break;
			}
		}
		
		return curr;
	}
}
