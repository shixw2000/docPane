package my.demo.doc;

import java.util.Comparator;

public class PriorityQue<T> {
	private Object[] m_heap;
	private int m_size;
	private int m_capacity;
	private Comparator<? super T> m_comparator;
	
	PriorityQue(int capacity, Comparator<? super T> comparator) {
		m_heap = new Object[capacity + 1];
		m_capacity = capacity;
		m_comparator = comparator;
	}
	
	PriorityQue(int capacity) {
		m_heap = new Object[capacity + 1];
		m_capacity = capacity;
	}
	
	@SuppressWarnings("unchecked")
	protected int compare(Object o1, Object o2) {
		if (null != m_comparator) {
			return m_comparator.compare((T)o1, (T)o2);
		} else {
			Comparable<? super T> key = (Comparable<? super T>)o1;
			
			return key.compareTo((T)o2);
		}
	}
	
	private void upHeap() {
		int curr = m_size;
		int up = 0;
		
		while (1 < curr) {
			up = curr >>> 1;
			
			if (0 > compare(m_heap[curr], m_heap[up])) {
				swap(curr, up);
				curr = up;
			} else {
				return;
			}
		}
	}
	
	private void downHeap() {
		int curr = 1;
		int l = curr << 1;
		int r = l + 1;
		
		while (r <= m_size) {
			if (0 < compare(m_heap[l], m_heap[r])) {
				l = r;
			}
			
			if (0 > compare(m_heap[l], m_heap[curr])) {
				swap(l, curr);
				
				curr = l;
				l = curr << 1;
				r = l + 1;
			} else {
				return;
			}
		}
		
		if (l == m_size) {
			if (0 > compare(m_heap[l], m_heap[curr])) {
				swap(l, curr);
			}
		}
	}
	
	private void swap(int i, int j) {
		m_heap[0] = m_heap[i];
		m_heap[i] = m_heap[j];
		m_heap[j] = m_heap[0];
	}
	
	public boolean isEmpty() {
		return 0 == m_size;
	}
	
	public boolean isFull() {
		return m_size == m_capacity;
	}
	
	public int size() {
		return m_size;
	}
	
	public int capacity() {
		return m_capacity;
	}
	
	@SuppressWarnings("unchecked")
	public T top() {
		if (0 < m_size) {
			return (T)m_heap[1];
		} else {
			return null;
		}
	}
	
	public boolean pop() {
		if (1 < m_size) {
			m_heap[1] = m_heap[m_size];
			--m_size;
			
			downHeap();
			return true;
		} else if (1 == m_size ){
			m_size = 0;
			return true;
		} else {
			/* invalid */
			return false;
		}
	}
	
	public boolean push(T obj) {
		if (m_size < m_capacity) {
			m_heap[++m_size] = obj;
			
			upHeap();
			return true;
		} else {
			/* invalid */
			return false;
		}
	}
}

/*
 * class Main extends PriorityQue<Integer> { Main() { super(new Integer[30],
 * null); }
 * 
 * public static void main(String[] args) { Main m = new Main(); Scanner scan =
 * new Scanner(System.in); int n = 0;
 * 
 * while (0 < (n = scan.nextInt()) && !m.isFull()) { m.push(n); }
 * 
 * scan.close();
 * 
 * while (!m.isEmpty()) { n = m.top();
 * 
 * System.out.printf(" %d", n); m.pop(); } } }
 */
