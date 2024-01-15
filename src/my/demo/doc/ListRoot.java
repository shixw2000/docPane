package my.demo.doc;

import java.util.Iterator;


public class ListRoot<T> implements Iterable<T> { 
	private int m_size;
	private Node m_root;
	
	public class Node {
		private T m_data;
		private Node m_next;
		private Node m_prev;
		
		private Node(T data) {
			m_data = data;
			m_next = this;
			m_prev = this;
		}
		
		public T data() {
			return m_data;
		}
		
		public Node next() {
			return m_next;
		}
		
		public Node prev() {
			return m_prev;
		}
		
		private void setData(T data) {
			m_data = data;
		}
		
		private void reset() {
			m_next = m_prev = this;
		}
		
		private void append(Node node) {
			node.m_prev = this;
			node.m_next = m_next;
			
			m_next.m_prev = node;
			m_next = node;
		}
		
		private void prepend(Node node) {
			node.m_prev = m_prev;
			node.m_next = this;
			
			m_prev.m_next = node;
			m_prev = node;
		}
		
		private void del() {
			if (m_next != this) {
				m_next.m_prev = m_prev;
				m_prev.m_next = m_next;
				
				reset();
			}
		}
	}
	
	public ListRoot() {
		m_size = 0;
		m_root = new Node(null);
	}
	
	private void reset() {
		m_size = 0;
		m_root.reset();
	}
 
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Node m_curr = begin();
			
			@Override
			public boolean hasNext() {
				return m_curr != end();
			}
			
			@Override
			public T next() {
				T data = m_curr.data();
				m_curr = m_curr.next();
				
				return data;
			}
		};
	}
	
	public boolean isEmpty() {
		return m_root.m_next == m_root;
	}
	
	public int size() {
		return m_size;
	}
	
	public void clear() {
		while (m_root.m_next != m_root) {
			m_root.m_next.del();
		}
		
		m_size = 0;
	}
	
	public Node begin() {
		return m_root.m_next;
	}
	
	public Node rbegin() {
		return m_root.m_prev;
	}
	
	public Node end() {
		return m_root;
	}
	
	public T front() {
		if (m_root.m_next != m_root) {
			return m_root.m_next.m_data;
		} else {
			return null;
		}
	}
	
	public T back() {
		if (m_root.m_prev != m_root) {
			return m_root.m_prev.m_data;
		} else {
			return null;
		}
	}
	
	public T getData(int index) {
		Node node = getNode(index);
		
		if (end() != node) {
			return node.data();
		} else {
			return null;
		}
	}
	
	public Node getNode(int index) {
		if (0 <= index && index < m_size) {
			for (Node curr=begin(); curr!=end(); curr=curr.next()) {
				if (0 < index) {
					--index;
				} else {
					return curr;
				}
			}
		}
		
		return end();
	}
	
	/* these are write operations */
	public void push_back(T data) {
		Node node = new Node(data);
		
		m_root.prepend(node);

		++m_size;
	}
	
	public void push_front(T data) {
		Node node = new Node(data);
		
		m_root.append(node);
		++m_size;
	}
	
	public void pop_back() {
		if (m_root.m_prev != m_root) {
			m_root.m_prev.del();
			
			--m_size;
		}
	}
	
	public void pop_front() {
		if (m_root.m_next != m_root) {
			m_root.m_next.del();
			
			--m_size;
		}
	}
	
	public void del(Node node) {
		if (null != node) {
			node.del();
			
			--m_size;
		}
	}
	
	public void replace(Node pilot, T data) {
		pilot.setData(data);
	}
	
	public void append(Node pilot, T data) {
		Node node = new Node(data);
		
		pilot.append(node);
		++m_size;
	}
	
	public void prepend(Node pilot, T data) {
		Node node = new Node(data);
		
		pilot.prepend(node);
		++m_size;
	}
	
	public void swap(ListRoot<T> other) { 
		if (!other.isEmpty() && !isEmpty()) {
			ListRoot<T> list = new ListRoot<T>();
			
			list.append(other);
			other.append(this);
			this.append(list);
		} else if (!isEmpty()) {
			other.append(this);
		} else if (!other.isEmpty()) {
			this.append(other);
		} else {
			/* all empty list */
		}
	}
	
	public void append(ListRoot<T> other) {
		if (!other.isEmpty()) {
			Node beg = other.begin();
			Node end = other.end();

			insert(rbegin(), m_root, beg, end);
			
			m_size += other.m_size;
			other.m_size = 0;
			
			other.reset();
		}
	}
	
	public void prepend(ListRoot<T> other) {
		if (!other.isEmpty()) {
			Node beg = other.begin();
			Node end = other.end();

			insert(m_root, begin(), beg, end);
			
			m_size += other.m_size;
			other.m_size = 0;
			
			other.reset();
		}
	}
	
	public ListRoot<T> split(Node pilot) { 
		if (pilot != end()) {
			ListRoot<T> other = erase(pilot, end());
			int size = 0;
			
			for (Node curr=other.begin(); curr!= other.end(); curr=curr.m_next) {
				++size;
			}
			
			if (0 < size) {
				m_size -= size;
				other.m_size = size;
			}
			
			return other;
		} else {
			return null;
		}
	}
	
	/* [beg, end)*/
	private void insert(Node pilot1, Node pilot2, Node beg, Node end) {
		if (beg != end) {
			Node last = end.m_prev;
			
			beg.m_prev = pilot1;
			pilot1.m_next = beg;
			
			last.m_next = pilot2;
			pilot2.m_prev = last;
		}
	}
	
	/* [beg, end) */
	private ListRoot<T> erase(Node beg, Node end) { 
		ListRoot<T> other = new ListRoot<T>();
	
		if (beg != end) {
			Node pilot = beg.m_prev; 

			other.insert(other.m_root, other.m_root, beg, end);
			
			pilot.m_next = end;
			end.m_prev = pilot;
		}
		
		return other;
	}
	
	public void toArray(T[] arr) {
		int cnt = 0;
		
		for (Node curr = begin(); end() != curr; curr = curr.next()) {
			arr[cnt++] = curr.data();
		}
	}
 }
