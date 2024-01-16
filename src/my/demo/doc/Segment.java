package my.demo.doc;

import java.awt.FontMetrics;

public class Segment implements Element {
	private int m_length; 
	private final ListRoot<Slice> m_slices;
	private final int[] m_defs;
	private final ListRoot<Block>[] m_blocks;
	private final ListRoot<Attribute> m_attrs;
	private final ListRoot<SegmentLine> m_lines;
	
	public Segment() {
		m_slices = new ListRoot<Slice>();
		m_defs = new int[DocFormat.MAX_FORMAT_SIZE];
		m_blocks = new ListRoot[DocFormat.MAX_FORMAT_SIZE];
		m_attrs = new ListRoot<Attribute>();
		m_lines = new ListRoot<SegmentLine>();
		
		for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
			m_blocks[i] = new ListRoot<Block>();
			m_defs[i] = DocFormat.DEF_ID_VAL;
		}
	}
	
	void clear() {
		m_length = 0;
		m_attrs.clear();
		m_slices.clear();
		m_lines.clear();
		
		for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
			m_blocks[i].clear();
			m_defs[i] = DocFormat.DEF_ID_VAL;
		}
	}
	
	boolean isEmpty() {
		return 0 == m_length;
	}
	
	public int length() {
		return m_length;
	}
	
	ListRoot<SegmentLine> lines() {
		return m_lines;
	}
	
	public void add(DocView doc, int pos, Slice s) {
		int total = length();
		int cnt = s.length();
		
		DocSlice.add(m_slices, pos, s);
		
		if (0 < total) {
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				DocBlock.add(m_blocks[i], pos, s.length());
			} 
		} else {
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				if (!DocFormat.isDefVal(m_defs[i])) {
					DocBlock.format(m_blocks[i], 0, cnt, m_defs[i]);
					
					m_defs[i] = DocFormat.DEF_ID_VAL;
				}
			}
		}
		
		m_length += cnt;
	}
	
	public int del(DocView doc, int beg, int end) {
		int total = length();
		int cnt = 0;
		
		cnt = DocSlice.delete(m_slices, beg, end);
		
		if (cnt < total) {
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				DocBlock.delete(m_blocks[i], beg, end);
			}
		} else {
			int[] attr = getAttr(0);
			
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				m_blocks[i].clear();
				m_defs[i] = attr[i];
			}
		} 
		
		m_length -= cnt;
		
		System.out.printf("del_segment| range=(%d, %d)| total=%d| cnt=%d|\n", 
				beg, end, total, cnt);
		
		return cnt;
	}
	
	public Segment truncate(DocView doc, int pos) {
		Segment segment = new Segment();
		ListRoot<Slice> slices = null;
		ListRoot<Block> blocks = null;
		int total = length();
		int[] attr = null;
		
		if (0 < total) {
			if (0 == pos || pos == total) {
				attr = getAttr(pos);
				if (null == attr) {
					System.out.println();
				}
			}
			
			slices = DocSlice.truncate(m_slices, pos);
			segment.m_slices.swap(slices);
			
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				blocks = DocBlock.truncate(m_blocks[i], pos);
				segment.m_blocks[i].swap(blocks);
				
				try {
				if (0 == pos) {
					this.m_defs[i] = attr[i];
				} else if (pos == total) {
					segment.m_defs[i] = attr[i];
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			segment.m_length = m_length - pos;
			m_length = pos;
		} else {
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				if (!DocFormat.isDefVal(this.m_defs[i])) {
					segment.m_defs[i] = this.m_defs[i];
				}
			}
		} 
		
		return segment; 
	}
	
	public void join(DocView doc, Segment other) {
		int total = length();
		int otherTotal = other.length();
		boolean chg = ((0 == total) && (0 < otherTotal));
		
		DocSlice.join(m_slices, other.m_slices);
		
		for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
			DocBlock.join(total, m_blocks[i], other.m_blocks[i]);
			
			if (chg && !DocFormat.isDefVal(m_defs[i])) {
				m_defs[i] = DocFormat.DEF_ID_VAL;
			}
		}
		
		m_length = total + otherTotal;
		other.m_length = 0;
	}
	
	public void format(DocView doc, EnumAttr en, 
			int beg, int end, int id) {
		int total = length();
		
		if (0 < total) {
			DocBlock.format(m_blocks[en.ordinal()], beg, end, id);
		} else {
			m_defs[en.ordinal()] = id;
		}
	}
	
	public void unformat(DocView doc, EnumAttr en, 
			int beg, int end) {
		int total = length();
		
		if (0 < total) {
			DocBlock.unformat(m_blocks[en.ordinal()], beg, end);
		} else {
			m_defs[en.ordinal()] = DocFormat.DEF_ID_VAL;
		}
	}
	
	public void getText(StringBuilder sb, int beg, int end) { 
		OriginCb cc = new OriginCb();
		Cache cache = cc.cache();
		
		DocSegment.SelectCb1 cb = new DocSegment.SelectCb1() {
			public void select(Element s, int beg1, int end1, boolean bHit) {
				if (bHit) {
					Slice slice = (Slice)s;
					
					cc.reset();
					cc.setRange(beg1, end1);
					slice.handle(cc);
					if (0 < cache.m_len) {
						sb.append(cache.m_buf, cache.m_offset, cache.m_len);
					}
				}
			}
		};
		
		DocSegment.range(m_slices, beg, end, cb);
	}
	
	public int[] getAttr(int col) {
		DocSegment.Cursor cursor = null;
		Attribute attr = null;
		
		cursor = DocSegment.find(m_attrs, col);
		if (null != cursor) {
			attr = (Attribute)cursor.m_pilot.data();
			
			return attr.attrs();
		} else {
			return m_defs;
		}
	}
	
	public Attribute getItem(int col) {
		DocSegment.Cursor cursor = null;
		Attribute attr = null;
		
		cursor = DocSegment.find(m_attrs, col);
		if (null != cursor) {
			attr = (Attribute)cursor.m_pilot.data();
			
			return attr;
		} else {
			return null;
		}
	}
	
	public void findByCol(DocView doc, int col, 
			PageState page, PosInfo info) {
		page.reset();
		info.reset();
		
		for (SegmentLine segline: m_lines) {
			if (col <= segline.begCol() + segline.length()) {
				int pos = col - segline.begCol();
				
				segline.findByPos(doc, pos, info);
				page.setPageInfo(segline.pageNo(), 
						segline.pageLine());
				return;
			}
		}
	}
	
	public void updateAttr(DocView doc, int maxW) {
		ListRoot<Attribute> attrs = null;
		
		attrs = DocSegment.slice2Attrs(doc, m_slices, m_blocks);
		m_attrs.swap(attrs); 
		
		updateLine(doc, maxW);
	}
	
	private ListRoot<SegmentLine> getDefLine(DocView doc) {
		ListRoot<SegmentLine> lines = new ListRoot<SegmentLine>();
		SegmentLine line = new SegmentLine(this, 0);
		int font = m_defs[EnumAttr.ATTR_FONT.ordinal()];
		FontMetrics fm = doc.getFM(font);
		int h = fm.getHeight();
		int baseY = fm.getAscent();
		
		line.setH(h, baseY);
		lines.push_back(line);
		return lines;
	}
	
	public void updateLine(DocView doc, int maxW) {
		ListRoot<SegmentLine> lines = null;
		
		if (!m_attrs.isEmpty()) {
			lines = DocSegment.attrs2Line(this, doc, maxW, m_attrs);
		} else {
			lines = getDefLine(doc);
		}
		
		m_lines.swap(lines);
	} 
}
