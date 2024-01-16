package my.demo.doc;

public class DocSegment {
	public static class Selector {
		private int m_i;
		private int m_j;
		
		int i() {
			return m_i;
		}
		
		int j() {
			return m_j;
		}
		
		void from(Axis axis) {
			m_i = axis.row();
			m_j = axis.col();
		}
		
		void to(Axis axis) {
			axis.setRow(m_i);
			axis.setCol(m_j);
		}
		
		void set(int i, int j) {
			m_i = i;
			m_j = j;
		}
	}
	
	public static class Cursor {
		int m_pos;
		ListRoot<? extends Element>.Node m_pilot;
		
		Cursor(int pos, ListRoot<? extends Element>.Node pilot) {
			m_pos = pos;
			m_pilot = pilot;
		}
	} 
	
	@FunctionalInterface
	public static interface SelectCb1 {
		void select(Element s, int beg, int end, boolean bHit);
	}
	
	@FunctionalInterface
	public static interface SelectCb2 {
		void select(Element s, int beg, int end, boolean eof);
	}
	
	public static Cursor find(ListRoot<? extends Element> eles, int pos) {
		ListRoot<? extends Element>.Node pilot = null;
		Element e = null;
		
		pilot = eles.begin();
		while (pilot != eles.end()) {
			e = pilot.data();
			
			if (pos > e.length()) {
				pos -= e.length();
				pilot = pilot.next();
			} else {
				return new Cursor(pos, pilot);
			}
		}
		
		return null;
	}
	
	public static void range(ListRoot<? extends Element> eles,
			int beg, int end, SelectCb1 cb) {
		ListRoot<? extends Element>.Node curr = null;
		Element data = null;
		int pos = 0;
		int cnt = 0;
		int start = 0;
		int len = 0;
		
		if (beg < end) {
			curr = eles.begin();
			while (eles.end() != curr && start < end) {
				data = curr.data();
				len = data.length();
				
				curr = curr.next();
				
				if (start + len <= beg) {
					cb.select(data, 0, len, false);
					
					start += len;
				} else if (start + len <= end) {
					if (start < beg) {
						pos = beg - start;
						cb.select(data, 0, pos, false);
						cb.select(data, pos, len, true);
					} else {
						cb.select(data, 0, len, true);
					}
					
					start += len;
				} else {
					if (start < beg) {
						pos = beg - start;
						cb.select(data, 0, pos, false);
						
						cnt = end - beg;
						cb.select(data, pos, pos + cnt, true);
					} else {
						pos = end - start;
						cb.select(data, 0, pos, true);
					}
					
					start = end;
				}
			}
		}
	}
	
	public static void select(ListRoot<? extends Element> eles,
			Selector from, Selector to, SelectCb2 cb) {
		ListRoot<? extends Element>.Node curr = null;
		Element data = null; 
		int index = 0;
		
		if (from.m_i == to.m_i && from.m_j <= to.m_j) {
			curr = eles.getNode(from.m_i);
			data = curr.data();
			
			cb.select(data, from.m_j, to.m_j, true);
		} else if (from.m_i < to.m_i) {
			index = from.m_i;
			curr = eles.getNode(index);
			data = curr.data();
			cb.select(data, from.m_j, data.length(), false);
			
			++index;
			curr = curr.next();
			while (index < to.m_i && curr != eles.end()) {
				data = curr.data();
				cb.select(data, 0, data.length(), false);

				++index;
				curr = curr.next();
			}
			
			if (index == to.m_i && curr != eles.end()) {
				data = curr.data();
				cb.select(data, 0, to.m_j, true);
			}
		}
	}
	
	private static Attribute creatAttr(DocView doc,
			Slice s, int beg, int end, int[] ids) {
		Slice child = null;
		int len = s.length();
		int[] attrs = ids.clone();
		int i = EnumAttr.ATTR_FONT.ordinal();
		
		if (0 == beg && end == len) {
			child = s;
		} else {
			child = s.split(beg, end);
		}
		
		if (SLICE_TYPE.CHINESE_SLICE == s.type()) {
			attrs[i] = doc.adjustCn(attrs[i]);
		} else if (SLICE_TYPE.EMOJI_SLICE == s.type() ||
				SLICE_TYPE.SURROGATE_SLICE == s.type()) {
			attrs[i] = doc.adjustEmoji(attrs[i]);
		} else {
			
		} 
		
		return new Attribute(child, attrs);
	}
	
	public static ListRoot<Attribute> slice2Attrs(DocView doc,
			ListRoot<Slice> slices,
			ListRoot<Block>[] blocks) {
		ListRoot<Attribute> attrs = new ListRoot<Attribute>();
		DocFormat.FormatCursor itr = DocFormat.cursor(blocks);
		ListRoot<Slice>.Node node = slices.begin();
		Slice data = null;
		Attribute attr = null;
		int off = 0;
		int len = 0;
		int cnt = 0;
		int start = 0;
		int maxPos = 0;
		
		while (itr.next()) {
			maxPos = itr.pos();
			
			while (start < maxPos) {
				if (0 == len) {
					if (slices.end() != node) {
						data = node.data();
						node = node.next();
						
						off = 0;
						len = data.length();
						
						continue;
					} else {
						break;
					}
				}
				
				if (start + len <= maxPos) {
					cnt = len; 
				} else {
					cnt = maxPos - start;
				}
				
				attr = creatAttr(doc, data, off, off + cnt, itr.ids());
				attrs.push_back(attr);
				
				start += cnt;
				off += cnt;
				len -= cnt;
			}
		}
		
		if (0 < len) {
			attr = creatAttr(doc, data, off, off + len, itr.ids());
			attrs.push_back(attr);
			
			off += len;
			len = 0;
		}
		
		while (slices.end() != node) {
			data = node.data();
			node = node.next();
			
			attr = creatAttr(doc, data, 0, data.length(), itr.ids());
			attrs.push_back(attr);
		}
		
		return attrs;
	}
	
	public static void getAttrDim(DocView doc, Attribute attr, DimenCb dc) {
		dc.reset();
		dc.setRange(0, attr.length());
		attr.calcDim(dc);
	}
	
	public static ListRoot<SegmentLine> attrs2Line(
			Segment segment,
			DocView doc, int maxW,
			ListRoot<Attribute> attrs) {
		PosInfo posInfo = new PosInfo();
		DimenCb dc = new DimenCb(doc);
		ListRoot<SegmentLine> lines = null;
		SegmentLine line = null;
		ViewEx view = null;
		int col = 0;
		int off = 0;
		int len = 0;
		int leftW = 0;
		int w = 0;

		lines = new ListRoot<SegmentLine>();
		line = new SegmentLine(segment, col);
		lines.push_back(line);
		
		w = maxW;
		for (Attribute attr: attrs) { 
			getAttrDim(doc, attr, dc);
			
			off = 0;
			len = attr.length();
			leftW = dc.m_retW;
			
			while (0 < len) {
				if (leftW <= w) {
					view = attr.creat(off, off + len,
							leftW, dc.m_retH, 
							dc.m_retBaseY);
					line.addView(view);
					
					col += len;
					w -= leftW;
					break;
				} else if (1 == len) {
					line = new SegmentLine(segment, col);
					lines.push_back(line);
					w = maxW;
					
					view = attr.creat(off, off + len,
							leftW, dc.m_retH, 
							dc.m_retBaseY);
					line.addView(view);
					
					col += len;
					w -= leftW;
					break;
				} else {
					findByWidth(doc, attr, off, w, posInfo);
					
					if (0 < posInfo.pos()) {
						view = attr.creat(off, off + posInfo.pos(),
								posInfo.x(), dc.m_retH, 
								dc.m_retBaseY);
						line.addView(view);
						
						col += posInfo.pos();
						off += posInfo.pos();
						len -= posInfo.pos();
						leftW -= posInfo.x();
					}
					
					if (0 < len) {
						line = new SegmentLine(segment, col);
						lines.push_back(line);
						w = maxW;
					}
				}
			}
		}
		
		return lines;
	}
	
	public static void findByWidth(DocView doc, Attribute attr,
			int start, int maxW, PosInfo posInfo) {
		WidthCb cb = new WidthCb(doc);
		int cnt = 0;
		int w = 0;
		
		cb.setRange(start, attr.length());
		attr.calcW(cb);
		
		w = cb.m_retW;
		if (w <= maxW) {
			cnt = attr.length() - start;
			posInfo.setPosX(cnt, w);
		} else {
			cnt = 0;
			w = 0;
			for (int i=start; i<attr.length() && w < maxW; ++i) {
				cb.reset();
				cb.setRange(i, i+1);
				attr.calcW(cb);
				
				if (w + cb.m_retW <= maxW) {
					w += cb.m_retW;
					++cnt;
				} else {
					break;
				}
			}
			
			posInfo.setPosX(cnt, w);
		}
	}
	
	public static void findByPos(DocView doc, Attribute attr,
			int start, int len, PosInfo posInfo) {
		WidthCb cb = new WidthCb(doc);
		
		cb.setRange(start, start + len);
		attr.calcW(cb);
		posInfo.setPosX(len, cb.m_retW);
	}
}
