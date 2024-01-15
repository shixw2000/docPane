package my.demo.doc;

public class DocSlice { 
	public static void add(ListRoot<Slice> slices, int pos, Slice s) {
		DocSegment.Cursor cursor = null;
		ListRoot<Slice>.Node curr = null;
		ListRoot<Slice>.Node prev = null;
		ListRoot<Slice>.Node next = null;
		Slice data = null;
		
		cursor = DocSegment.find(slices, pos);
		if (null != cursor) {
			curr = (ListRoot<Slice>.Node)cursor.m_pilot;
			data = curr.data();
			
			if (0 < cursor.m_pos && cursor.m_pos < data.length()) {
				Slice[] splits = split(data, cursor.m_pos);
				
				slices.replace(curr, splits[0]);
				slices.append(curr, splits[1]);
				prev = curr;
				next = curr.next();
			} else if (cursor.m_pos == data.length()) {
				prev = curr;
				if (slices.end() != curr.next()) {
					next = curr.next();
				}
			} else {
				next = curr;
				if (slices.end() != curr.prev()) {
					prev = curr.prev();
				}
			}
			
			insert(slices, prev, next, s);
		} else {
			slices.push_back(s);
		}
	}
	
	public static Slice[] split(Slice s, int pos) {
		if (0 < pos && pos < s.length()) {
			Slice s1 = substr(s, 0, pos);
			Slice s2 = substr(s, pos, s.length());
			
			return new Slice[] {s1, s2};
		} else {
			return null;
		}
	}
	
	public static Slice substr(Slice s, int beg, int end) {
		if (0 <= beg && beg < end && end <= s.length()) {
			return s.split(beg, end);
		} else {
			return null;
		}
	}
	
	private static void insert(ListRoot<Slice> slices,
			ListRoot<Slice>.Node prev,
			ListRoot<Slice>.Node next, Slice s) {
		Slice data = null;
		
		if (null != prev) {
			data = prev.data();
			if (data.canMerge(s)) {
				s = data.merge(s);
				slices.replace(prev, s);
				return;
			}
		}
		
		if (null != next) {
			data = next.data();
			if (s.canMerge(data)) {
				s = s.merge(data);
				slices.replace(next, s);
				return;
			}
		}
		
		if (null != prev) {
			slices.append(prev, s);
		} else {
			slices.prepend(next, s);
		}
	}
	
	public static ListRoot<Slice> truncate(
			ListRoot<Slice> slices, int pos) {
		DocSegment.Cursor cursor = null;
		ListRoot<Slice> other = null;
		ListRoot<Slice>.Node curr = null;
		Slice data = null;
		
		cursor = DocSegment.find(slices, pos);
		if (null != cursor) {
			curr = (ListRoot<Slice>.Node)cursor.m_pilot;
			data = curr.data();
			
			if (0 < cursor.m_pos && cursor.m_pos < data.length()) {
				Slice[] splits = split(data, cursor.m_pos);
				
				slices.replace(curr, splits[0]);
				slices.append(curr, splits[1]);
				curr = curr.next();
			} else if (cursor.m_pos == data.length()) {
				curr = curr.next();
			} else {
				/* keep curr */
			}
			
			other = slices.split(curr);
		}
		
		if (null == other) {
			other = new ListRoot<Slice>();
		}
		
		return other;
	}
	
	public static void join(ListRoot<Slice> dst,
			ListRoot<Slice> src) {
		if (!src.isEmpty()) {
			/* first to join formats */
			dst.append(src); 
		}
	}
	
	public static int delete(ListRoot<Slice> slices,
			int beg, int end) {
		DocSegment.Cursor cursor = null;
		ListRoot<Slice>.Node curr = null;
		ListRoot<Slice>.Node next = null;
		Slice data = null;
		Slice[] splits = null;
		int pos = 0;
		int total = 0;
		int len = 0;
		int cnt = 0;
		
		total = beg < end ? end - beg : 0; 
		if (0 < total) {
			cursor = DocSegment.find(slices, beg);
			if (null != cursor) {
				curr = (ListRoot<Slice>.Node)cursor.m_pilot;
				pos = cursor.m_pos;
				data = curr.data();
				
				if (0 < pos && pos < data.length()) {
					splits = split(data, pos);
					
					slices.replace(curr, splits[0]);
					slices.append(curr, splits[1]);
					curr = curr.next();
				} else if (pos == data.length()) {
					curr = curr.next();
				} else {
					/* already at front */
				}
				
				while (0 < total && slices.end() != curr) {
					data = curr.data();
					len = data.length();

					next = curr.next();
					
					if (len <= total) {
						total -= len;
						cnt += len;
						
						slices.del(curr);
					} else {
						data = substr(data, total, len);
						slices.replace(curr, data);
						
						cnt += total;
						total = 0;
					}
					
					curr = next;
				}
			}
		}
		
		return cnt;
	}
}
