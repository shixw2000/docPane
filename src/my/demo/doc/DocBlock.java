package my.demo.doc;

class Block {
	int m_distance;
	int m_size;
	int m_id;
	
	Block(int dist, int size, int id) {
		m_distance = dist;
		m_size = size;
		m_id = id;
	}
	
	int distance() {
		return m_distance;
	}
	
	int length() {
		return m_size;
	}
}

public class DocBlock {
	public static void add(ListRoot<Block> blocks, int pos, int len) {
		ListRoot<Block>.Node pilot = null;
		Block s = null;
		int start = 0;
		
		pilot = blocks.begin();
		while (pilot != blocks.end()) {
			s = pilot.data();
			
			start += s.m_distance;
			if (pos < start) {
				s.m_distance += len;
				break;
			} else if (pos == start) {
				if (0 < pos) {
					s.m_distance += len;
				} else {
					s.m_size += len;
				}
				
				break;
			} else {
				start += s.m_size; 
				if (pos <= start) { 
					s.m_size += len;
					break;
				}
			}
			
			pilot = pilot.next();
		}
	}
	
	public static ListRoot<Block> truncate(ListRoot<Block> blocks, int pos) {
		ListRoot<Block> other = null;
		ListRoot<Block>.Node pilot = null;
		Block s = null;
		int start = 0;
		
		pilot = blocks.begin();
		while (pilot != blocks.end()) {
			s = pilot.data();
			
			start += s.m_distance;
			if (pos <= start) {
				s.m_distance = start - pos;
				
				other = blocks.split(pilot);
				break;
			} else if (pos < start + s.m_size) {
				int size = start + s.m_size - pos;
				Block b = new Block(0, size, s.m_id);
				
				s.m_size = pos - start;
				blocks.append(pilot, b);
				pilot = pilot.next();
				
				other = blocks.split(pilot);
				break;
			} else {
				start += s.m_size;
			}
			
			pilot = pilot.next();
		}
	
		if (null == other) {
			other = new ListRoot<Block>();
		}
	
		return other;
	}
	
	public static void join(int total, ListRoot<Block> dst,
			ListRoot<Block> src) {
		int max = 0;
		Block head = null;
		
		if (!src.isEmpty()) {
			head = src.front();
			
			for (Block b: dst) {
				max += b.m_distance + b.m_size;
			}
			
			if (max < total) {
				head.m_distance += total - max;
			}
			
			dst.append(src);
		} 
	}
	
	public static void delete(ListRoot<Block> blocks, int beg, int end) {
		ListRoot<Block>.Node curr = null;
		ListRoot<Block>.Node next = null;
		Block b = null;
		int start = 0;
		int stop = 0;
		int mark = 0;
		
		next = blocks.begin();
		while (blocks.end() != next) {
			curr = next;
			next = curr.next();
			b = curr.data();
			
			start = stop + b.m_distance;
			stop = start + b.m_size;
			if (stop <= beg) {
				mark = stop;
				continue;
			} else if (stop <= end) {
				if (beg <= start) {
					blocks.del(curr);
				} else {
					b.m_size = beg - start;
					mark = beg;
				}
			} else {
				if (end <= start) {
					b.m_distance = (start - end) + (beg - mark);
				} else if (beg <= start) {
					b.m_size = stop - end;
					b.m_distance = beg - mark;
				} else {
					b.m_size = (stop - end) + (beg - start);
				}
				
				break;
			}
		}
	}
	
	public static void format(ListRoot<Block> blocks, int beg, int end, int id) {
		ListRoot<Block>.Node curr = null;
		ListRoot<Block>.Node next = null;
		Block b = null;
		Block tmp = null;
		int start = 0;
		int stop = 0;
		int mark = 0;
		
		next = blocks.begin();
		while (blocks.end() != next) {
			curr = next;
			next = curr.next();
			b = curr.data();
			
			start = stop + b.m_distance;
			stop = start + b.m_size;
			
			if (stop <= beg) {
				mark = stop;
				continue;
			} else if (stop <= end) {
				if (beg <= start) {
					blocks.del(curr);
				} else {
					b.m_size = beg - start;
					mark = beg;
				}
			} else {
				if (end <= start) {
					b.m_distance = start - end;
				} else if (beg <= start) {
					b.m_size = stop - end;
					b.m_distance = 0;
				} else {
					tmp = new Block(b.m_distance, beg - start, b.m_id);
					blocks.prepend(curr, tmp);
					
					b.m_distance = 0;
					b.m_size = (stop - end);
				}
				
				next = curr;
				break;
			}
		}
		
		tmp = new Block(beg - mark, end - beg, id);
		blocks.prepend(next, tmp);
	}
	
	public static void unformat(ListRoot<Block> blocks, int beg, int end) {
		ListRoot<Block>.Node curr = null;
		ListRoot<Block>.Node next = null;
		Block b = null;
		Block tmp = null;
		int start = 0;
		int stop = 0;
		int mark = 0;
		
		next = blocks.begin();
		while (blocks.end() != next) {
			curr = next;
			next = curr.next();
			b = curr.data();
			
			start = stop + b.m_distance;
			stop = start + b.m_size;
			
			if (stop <= beg) {
				mark = stop;
				continue;
			} else if (stop <= end) {
				if (beg <= start) {
					blocks.del(curr);
				} else {
					b.m_size = beg - start;
					mark = beg;
				}
			} else {
				if (end <= start) {
					b.m_distance = start - mark;
				} else if (beg <= start) {
					b.m_size = stop - end;
					b.m_distance = end - mark;
				} else {
					tmp = new Block(b.m_distance, beg - start, b.m_id);
					blocks.prepend(curr, tmp);
					
					b.m_distance = end - beg;
					b.m_size = (stop - end);
				}
				
				break;
			}
		}
	} 
}
