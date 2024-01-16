package my.demo.doc;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;


class DocModel { 
	private DocCache m_cache;
	private ListRoot<Segment> m_segments;
	private DocView m_doc;
	private DocPage m_pages;
	private DocState m_state;
	
	@FunctionalInterface
	public static interface Selector {
		void select(Segment segment, int beg, int end, boolean eof);
	}
	
	DocModel() { 
		m_cache = new DocCache();
		m_segments = new ListRoot<Segment>();
		m_pages = new DocPage();
		m_state = new DocState();
	} 
	
	public void setView(DocView doc) { 
		Segment segment = new Segment();
		
		m_segments.push_back(segment); 
		m_doc = doc;
//		test();
	}
	
	public void test() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		for (Font f: ge.getAllFonts()) {
			System.out.printf("family=%s| name=%s| face=%s|\n",
					f.getFamily(),
					f.getName(),
					f.getFontName());
		}
		
		System.out.printf("family=%d| font=%d|\n", 
				ge.getAvailableFontFamilyNames().length, ge.getAllFonts().length);
	}
	
	void clear() {
		m_cache.clear();
		m_segments.clear();
		m_pages.clear();
	}
	
	private void page2Axis(PageState page, PosInfo info, Axis axis) {
		m_pages.page2Axis(page, info, axis);
	}
	
	private void axis2Page(Axis axis, PageState page, PosInfo info) {
		Segment segment = null;
		int row = axis.row();
		int col = axis.col();
		
		segment = getSegment(row);
		segment.findByCol(m_doc, col, page, info);
	}
	
	private void findByXY(int maxW, int usedH, 
			PageState begStat, int x, int y, 
			PageState pageStat,
			PosInfo posInfo, Axis axis) {
		m_pages.findByXY(m_doc, maxW, usedH, begStat, x, y, 
				pageStat, posInfo, axis);
	}
	
	private void update(DocState stat) {
		m_pages.updatePage(m_doc, m_segments);
		axis2Page(stat.curr(), stat.pageStat(), stat.posInfo());
		
		adjust(m_state);
		m_doc.show();
	}
	
	private void updateByPosition(DocState stat) {
		page2Axis(stat.pageStat(), stat.posInfo(), stat.curr());
		
		adjust(m_state);
		m_doc.show();
	}
	
	public void reloadAttr(DocState stat) {
		int maxW = m_doc.maxW();
		
		for (Segment segment: m_segments) { 
			segment.updateAttr(m_doc, maxW);
		}
		
		m_pages.updatePage(m_doc, m_segments);
		axis2Page(stat.curr(), stat.pageStat(), stat.posInfo());
		
		/* !!! here we invalidate begstat */
		m_state.begStat().reset(); 
		adjust(m_state);
		m_doc.show();
	}
	
	public void reloadLine(DocState stat) {
		int maxW = m_doc.maxW();
		
		for (Segment segment: m_segments) { 
			segment.updateLine(m_doc, maxW);
		}
		
		m_pages.updatePage(m_doc, m_segments);
		axis2Page(stat.curr(), stat.pageStat(), stat.posInfo());
		
		/* !!! here we invalidate begstat */
		m_state.begStat().reset(); 
		adjust(m_state);
		m_doc.show();
	}
	
	private void adjust(DocState stat) {
		int usedH = 0;
		
		usedH = m_pages.adjustBegPage(m_doc, stat.begStat(), stat.pageStat());
		
		stat.setUsedH(usedH);
		disp();
	}
	
	private Segment getSegment(int row) {
		return m_segments.getData(row);
	}
	
	private ListRoot<Segment>.Node locate(int row) {
		ListRoot<Segment>.Node info = null;
		
		info = m_segments.getNode(row);
		return info;
	}
	
	private void _setAttrs(Axis from, Axis to, EnumAttr en, int attr) {
		DocSegment.Selector s1 = new DocSegment.Selector();
		DocSegment.Selector s2 = new DocSegment.Selector();
		int maxW = m_doc.maxW();
		
		DocSegment.SelectCb2 cb = new DocSegment.SelectCb2() {
			public void select(Element s, int beg, int end, boolean eof) {
				Segment segment = (Segment)s;
				
				segment.format(m_doc, en, beg, end, attr); 
				segment.updateAttr(m_doc, maxW);
			}
		};
		
		if (0 < from.compareTo(to)) {
			Axis tmp = from;
			from = to;
			to = tmp;
		}
		
		s1.from(from);
		s2.from(to);
		DocSegment.select(m_segments, s1, s2, cb);
	}
	
	private void _unsetAttrs(Axis from, Axis to, EnumAttr en) {
		DocSegment.Selector s1 = new DocSegment.Selector();
		DocSegment.Selector s2 = new DocSegment.Selector();
		int maxW = m_doc.maxW();
		
		DocSegment.SelectCb2 cb = new DocSegment.SelectCb2() {
			public void select(Element s, int beg, int end, boolean eof) {
				Segment segment = (Segment)s;
				
				segment.unformat(m_doc, en, beg, end);
				segment.updateAttr(m_doc, maxW);
			}
		};
		
		if (0 < from.compareTo(to)) {
			Axis tmp = from;
			from = to;
			to = tmp;
		}
		
		s1.from(from);
		s2.from(to);
		DocSegment.select(m_segments, s1, s2, cb);
	}
	
	private String _getTexts(Axis from, Axis to) {
		StringBuilder sb = new StringBuilder();
		DocSegment.Selector s1 = new DocSegment.Selector();
		DocSegment.Selector s2 = new DocSegment.Selector();
		
		DocSegment.SelectCb2 cb = new DocSegment.SelectCb2() {
			public void select(Element s, int beg, int end, boolean eof) {
				Segment segment = (Segment)s;
				
				segment.getText(sb, beg, end);
				if (!eof) {
					sb.append("\n");
				}
			}
		};
		
		if (0 < from.compareTo(to)) {
			Axis tmp = from;
			from = to;
			to = tmp;
		}
		
		s1.from(from);
		s2.from(to);
		DocSegment.select(m_segments, s1, s2, cb);
		
		return sb.toString();
	}
	
	private void _fillArea(Graphics g, int w, int h,
			PageState begStat, int maxH,
			Axis from, Axis to) {
		PageState p1 = new PageState();
		PosInfo i1 = new PosInfo();
		PageState p2 = new PageState();
		PosInfo i2 = new PosInfo();
		
		if (0 < from.compareTo(to)) {
			Axis tmp = from;
			from = to;
			to = tmp;
		} 

		axis2Page(from, p1, i1);
		axis2Page(to, p2, i2);
		
		m_doc.setForeColor(g, m_doc.fillColor());
		m_pages.fills(m_doc, g, w, h, begStat, maxH,
				p1, i1.pos(), p2, i2.pos());
	}
	
	private Axis _delRange(Axis from, Axis to) {
		ListRoot<Segment>.Node prev = null;
		ListRoot<Segment>.Node curr = null;
		ListRoot<Segment>.Node next = null;
		Segment preSeg = null;
		Segment segment = null;
		int row = 0;
		int cnt = 0;
		int maxW = m_doc.maxW();
		
		if (0 < from.compareTo(to)) {
			Axis tmp = from;
			
			from = to;
			to = tmp;
		} 
		
		if (from.row() == to.row()) {
			curr = locate(from.row());
			segment = curr.data();
			cnt = segment.del(m_doc, from.col(), to.col());
			segment.updateAttr(m_doc, maxW);
		} else {
			row = from.row(); 
			prev = locate(row);
			preSeg = prev.data();
			cnt = preSeg.del(m_doc, from.col(), preSeg.length());
			
			++row;
			curr = prev.next();
			while (row < to.row() && curr != m_segments.end()) {
				segment = curr.data();
				cnt += segment.length() + 1;
				
				System.out.printf("del_total_segment| total=%d|\n", 
						segment.length());
				
				next = curr;

				++row;
				curr = curr.next();
				m_segments.del(next);
			}
			
			if (curr != m_segments.end() && row == to.row()) {
				segment = curr.data(); 
				cnt += segment.del(m_doc, 0, to.col());
				
				preSeg.join(m_doc, segment);
				
				m_segments.del(curr);
				cnt += 1;
			}
			
			preSeg.updateAttr(m_doc, maxW);
		}
		
		System.out.printf("del_range| from=(%d, %d)| to=(%d, %d)| cnt=%d|\n",
				from.row(), from.col(),
				to.row(), to.col(),
				cnt);
		
		return from;
	}
	
	private Axis _addSlice(Axis axis, Slice s) {
		ListRoot<Segment>.Node node = null;
		Segment segment = null;
		int row = axis.row();
		int col = axis.col();
		int len = s.length();
		
		node = locate(row);
		segment = node.data();
		segment.add(m_doc, col, s);
		segment.updateAttr(m_doc, m_doc.maxW());
		
		col += len;
		axis.setCol(col);
		return axis;
	}
	
	private Axis _truncate(Axis axis) {
		ListRoot<Segment>.Node node = null;
		Segment segment = null;
		Segment newSeg = null;
		int row = axis.row();
		int col = axis.col();
		int maxW = m_doc.maxW();
		
		node = locate(row);
		segment = node.data();
		
		newSeg = segment.truncate(m_doc, col); 
		m_segments.append(node, newSeg); 
		segment.updateAttr(m_doc, maxW);
		newSeg.updateAttr(m_doc, maxW);
		
		axis.setRow(++row); 
		axis.setCol(0);
		return axis;
	}
	
	private Axis _delOnce(Axis axis) {
		ListRoot<Segment>.Node curr = null;
		ListRoot<Segment>.Node prev = null;
		Segment segment = null;
		Segment prevSeg = null;
		int row = axis.row();
		int col = axis.col();
		int total = 0;
		
		curr = locate(row);
		segment = curr.data();
		total = segment.length();
		
		if (0 < col && col <= total) {
			segment.del(m_doc, col-1, col);
			segment.updateAttr(m_doc, m_doc.maxW());
			
			axis.setCol(--col);
		} else if (0 == col && m_segments.end() != curr.prev()) {
			prev = curr.prev();
			prevSeg = prev.data();
			
			--row;
			col = prevSeg.length();
			
			prevSeg.join(m_doc, segment);
			prevSeg.updateAttr(m_doc, m_doc.maxW());
			
			m_segments.del(curr);
			
			axis.setRow(row); 
			axis.setCol(col);
		} else {
			/* do nothing */
		} 
		
		return axis;
	}

	public void foreward() {
		m_pages.foreward(m_doc, m_state.pageStat(), m_state.posInfo());
		
		m_state.disableSel();
		updateByPosition(m_state);
	}
	
	public void backward() {
		m_pages.backward(m_doc, m_state.pageStat(), m_state.posInfo());
		
		m_state.disableSel();
		updateByPosition(m_state);
	}
	
	public void up() {
		m_pages.up(m_doc, m_state.pageStat(), m_state.posInfo());
		
		m_state.disableSel();
		updateByPosition(m_state);
	}
	
	public void down() {
		m_pages.down(m_doc, m_state.pageStat(), m_state.posInfo());
		
		m_state.disableSel();
		updateByPosition(m_state);
	}
	
	public void seek(int x, int y) { 
		int maxW = m_doc.maxW();
		int usedH = m_state.usedH();
		int w = m_doc.statusW();
		
		System.out.printf("====seek| x=%d| y=%d|\n", x, y);
		x -= w;
		
		findByXY(maxW, usedH, m_state.begStat(), x, y,
				m_state.pageStat(), m_state.posInfo(), 
				m_state.curr());
		
		m_state.disableSel();
		disp();
		m_doc.show();
	}
	
	public void drag(int x, int y) { 
		int maxW = m_doc.maxW();
		int usedH = m_state.usedH();
		int w = m_doc.statusW();
		
		System.out.printf("====drag| x=%d| y=%d|\n", x, y);
		
		x -= w;
		findByXY(maxW, usedH, m_state.begStat(), x, y,
				m_state.pageStat(), m_state.posInfo(), 
				m_state.curr());
		
		m_state.enableSel();
		disp();
		m_doc.show();
	}
	
	public void resize(int w, int h) { 
		m_doc.reSize(w, h);
		
		reloadLine(m_state); 
	}
	
	public void truncate() {
		_truncate(m_state.curr());
		
		m_state.disableSel();
		update(m_state);
	}
	
	public void addSlice(Slice s) {
		_addSlice(m_state.curr(), s);
		
		m_state.disableSel();
		update(m_state);
	}
	
	public void dealSlices(ListRoot<Slice> list) {
		Axis axis = m_state.curr();
		
		for (Slice s: list) {
			if (s.type() != SLICE_TYPE.NEWLINE_SLICE) {
				_addSlice(axis, s);
			} else {
				_truncate(axis);
			}
		}
		
		m_state.disableSel();
		update(m_state);
	}
	
	public void delete() {
		if (!m_state.selected()) {
			_delOnce(m_state.curr());
		} else {
			Axis axis = null;
			
			axis = _delRange(m_state.last(), m_state.curr());
			
			m_state.curr().set(axis);
			m_state.disableSel();
		}
		
		update(m_state);
	}
	
	public void setAttrs(EnumAttr en, int attr) {
		if (m_state.selected()) {
			_setAttrs(m_state.last(), m_state.curr(), en, attr);
			
			update(m_state);
		}
	}
	
	public void unsetAttrs(EnumAttr en) {
		if (m_state.selected()) {
			_unsetAttrs(m_state.last(), m_state.curr(), en);
			
			update(m_state);
		}
	} 
	
	public void paste2Clip() {
		if (m_state.selected()) {
			String txt = null;
			
			txt = _getTexts(m_state.last(), m_state.curr());
			DocTool.set2Clip(txt);
			m_doc.show();
		}
	}
	
	public void copyFromClip() {
		String txt = null;
		
		txt = DocTool.getFromClip();
		if (null != txt && !txt.isEmpty()) {
			handleText(txt);
		}
		
		m_state.disableSel();
	}
	
	public void handleImage(String path) {
		Slice s = m_cache.creatImage(m_doc, path);
		
		addSlice(s);
	}
	
	public void handleChar(char... str) {
		Slice s = m_cache.creat(str);
		
		if (null != s) {
			if (!(s instanceof TokenSlice)) {
				addSlice(s);
			} else {
				switch (s.type()) {
				case TOKEN_SLICE:
					addSlice(s);
					break;
					
				case NEWLINE_SLICE:
					truncate();
					break;
					
				case BACKSPCE_SLICE:
					delete();
					break;
					
				default:
					/* ignore */
					break;
				}
			}
		}
	}
	
	public void handleText(String text) {
		ListRoot<Slice> list = m_cache.creat(text);
		
		dealSlices(list);
	}
	
	public void noop() {
	}
	
	public void fillArea(Graphics g, int w, int h) { 
		if (m_state.selected()) {
			int maxH = m_doc.pageH();
			Graphics2D g2d = (Graphics2D)g;
			Composite origin = g2d.getComposite();
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			
			g2d.setComposite(alpha); 
			
			_fillArea(g, w, h, m_state.begStat(), maxH,
					m_state.last(), m_state.curr());
			
			g2d.setComposite(origin);
		}
	}
	
	public void drawPages(Graphics g, int w, int h) {
		m_pages.draw(m_doc, g, w, h, m_state.begStat()); 
	} 
	
	int[] getAttr(Axis axis) { 
		Segment segment = null;
		int[] attrs = null;
		
		segment = getSegment(axis.row());
		attrs = segment.getAttr(axis.col());
		return attrs;
	}
	
	Attribute getItem(Axis axis) { 
		Segment segment = null;
		
		segment = getSegment(axis.row());
		return segment.getItem(axis.col());
	}
	
	public void drawPosInfo(DocView doc, Graphics g, int w, int h) {
		PageState begStat = m_state.begStat();
		PageState pageStat = m_state.pageStat();
		PosInfo posInfo = m_state.posInfo();
		Axis axis = m_state.curr();
		DimenCb dc = new DimenCb(doc);
		Segment segment = null;
		Attribute attr = null;
		SegmentLine segline = null;
		int offH = 0;
		int x = 0;
		int y = 0;
		
		y = m_pages.rangeH(begStat, pageStat);
		if (0 <= y) {
			x = w + posInfo.x();
			
			segment = getSegment(axis.row());
			segline = m_pages.getLine(pageStat);
			
			if (!segment.isEmpty()) {
				attr = segment.getItem(axis.col());
				DocSegment.getAttrDim(doc, attr, dc);
				
				if (!dc.isValid()) { 
					offH = segline.h() - segline.textH();
					y += offH + segline.baseY() - dc.m_retBaseY;
					doc.drawLine( g, x, y, x, y + dc.m_retH);
				} else {
					offH = segline.h() - dc.m_retH;
					doc.drawLine( g, x, y+offH, x, y + segline.h());
				}
			} else {
				doc.drawLine( g, x, y, x, y + segline.h());
			}
		}
	}
	
	public void renderStatus(DocView doc, Graphics g, int w, int h) {
		PageState pageStat = m_state.pageStat();
		PosInfo posInfo = m_state.posInfo();
		int statusFont = doc.statusFont();
		int statusColor = doc.statusColor(); 
		Axis axis = m_state.curr();
		int maxH = doc.pageH();
		int baseY = doc.statusBaseY();
		FontMetrics statusFm = doc.getFM(statusFont);
		int[] attrs = null;
		int x = 0;
		int y = maxH;
		
		attrs = getAttr(axis);
		
		doc.setFont(g, statusFont);
		doc.setBackColor(g, attrs[EnumAttr.ATTR_BACK_COLOR.ordinal()]);
		doc.clearRect(g, x, y, w, h);
		
		doc.setForeColor(g, attrs[EnumAttr.ATTR_FORE_COLOR.ordinal()]);
		doc.fillArc(g, x, y, w, h, -90, 180); 
		
		doc.setForeColor(g, statusColor);
		doc.drawLine(g, w, 0, w, doc.height());
		doc.drawLine(g, 0, y, doc.width(), y);
		x += w * 2;
		
		String info = "page: " + Integer.toString(pageStat.pageNo()) 
			+ ", line: " + Integer.toString(pageStat.pageLine()) 
			+ ", col: " + Integer.toString(posInfo.pos());

		doc.drawString(g, x, y, baseY, info); 
		x += statusFm.stringWidth(info) + w;
		
		FontItem fontItem = doc.fontItem(attrs[EnumAttr.ATTR_FONT.ordinal()]);
		doc.drawString(g, x, y, baseY, fontItem.getFontName() + ": " + fontItem.getSize());
	}
	
	public void render(Graphics g) {
		int w = m_doc.statusW();
		int h = m_doc.statusH();
		
		m_doc.reset(g);
		
		drawPages(g, w, h);
		fillArea(g, w, h);
		renderStatus(m_doc, g, w, h);
		drawPosInfo(m_doc, g, w, h);
	}
	
	public void disp() {
		int maxH = m_doc.pageH();
		int usedH = m_state.usedH();
		Axis old_axis = m_state.last();
		Axis axis = m_state.curr();
		PageState beg = m_state.begStat();
		PageState stat = m_state.pageStat();
		PosInfo info = m_state.posInfo();
		
		System.out.printf("h=(%d, %d)| old_axis=(%d, %d)|" +
				"axis=(%d, %d)| beg=(%d, %d)| stat=(%d, %d)| pos=(%d, %d)|\n", 
				maxH, usedH, 
				old_axis.row(), old_axis.col(),
				axis.row(), axis.col(),
				beg.pageNo(), beg.pageLine(),
				stat.pageNo(), stat.pageLine(),
				info.pos(), info.x());
	}
}

 