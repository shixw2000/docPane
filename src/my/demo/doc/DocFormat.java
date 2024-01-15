package my.demo.doc;

enum EnumAttr {
	ATTR_FONT,
	ATTR_FORE_COLOR,
	ATTR_BACK_COLOR
}

public class DocFormat { 
	public static final int DEF_ID_VAL = -1; 
	public static final int MAX_FORMAT_SIZE = EnumAttr.values().length;
	
	public static boolean isDefVal(int id) {
		return id == DEF_ID_VAL;
	}
	
	public static class BlockCursor implements Comparable<BlockCursor> {
		ListRoot<Block> m_list;
		ListRoot<Block>.Node m_node;
		Block m_b;
		int m_pos;
		int m_id;
		
		BlockCursor(ListRoot<Block> blocks) {
			m_list = blocks;
			m_node = m_list.begin();
			m_b = null;
			m_pos = 0;
			m_id = DEF_ID_VAL;
		}
		
		public int compareTo(BlockCursor other) {
			return m_pos - other.m_pos;
		}
		
		boolean next() {
			if (null != m_b) {
				m_pos += m_b.m_size;
				m_id = m_b.m_id;
				
				m_b = null;
				return true;
			} else if (m_list.end() != m_node) {
				m_b = m_node.data();
				m_node = m_node.next();
				
				if (0 < m_b.m_distance) {
					m_pos += m_b.m_distance;
					m_id = DEF_ID_VAL;
				} else {
					m_pos += m_b.m_size;
					m_id = m_b.m_id;
					
					m_b = null;
				}
				
				return true;
			} else {
				m_pos = 0;
				m_id = DEF_ID_VAL;
				return false;
			}
		}
		
		int pos() {
			return m_pos;
		}
		
		int id() {
			return m_id;
		}
	}
	
	public static class FormatCursor {
		private PriorityQue<BlockCursor> queue;
		private BlockCursor[] cursors;
		private int[] m_ids;
		private int m_pos;
		
		FormatCursor(ListRoot<Block>[] blocks) {
			queue = new PriorityQue<BlockCursor>(DocFormat.MAX_FORMAT_SIZE);
			cursors = new BlockCursor[DocFormat.MAX_FORMAT_SIZE];
			m_ids = new int[DocFormat.MAX_FORMAT_SIZE];
			
			for (int i=0; i<DocFormat.MAX_FORMAT_SIZE; ++i) {
				cursors[i] = new BlockCursor(blocks[i]);
				m_ids[i] = DocFormat.DEF_ID_VAL;
				
				if (cursors[i].next()) {
					queue.push(cursors[i]);
				}
			}
		}
		
		int pos() {
			return m_pos;
		}
		
		int[] ids() {
			return m_ids;
		}
		
		public boolean next() {
			if (!queue.isEmpty()) {
				BlockCursor top = queue.top();
				
				m_pos = top.pos();
				for (int i=0; i<cursors.length; ++i) {
					m_ids[i] = cursors[i].id();
				}
				
				while (!queue.isEmpty()) {
					top = queue.top();
					if (top.pos() <= m_pos) {
						queue.pop();
						if (top.next()) {
							queue.push(top);
						}
					} else {
						break;
					}
				}
				
				return true;
			} else {
				m_pos = 0;
				for (int i=0; i<cursors.length; ++i) {
					m_ids[i] = DocFormat.DEF_ID_VAL;
				}
				
				return false;
			}
		}
	}
	
	public static FormatCursor cursor(ListRoot<Block>[] blocks) {
		return new FormatCursor(blocks);
	}
}

