package my.demo.doc;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Collections;

public class DocPane extends Canvas {
	private BufferStrategy bs;
	private Cursor[] m_cursors;
	private int m_cursor_index;
	private Image m_img;
	private ViewPort m_view; 
	
	DocPane() { 
		m_view = new ViewPort(this);
		
		m_cursors = new Cursor[] { 
				new Cursor(Cursor.CROSSHAIR_CURSOR),
				new Cursor(Cursor.DEFAULT_CURSOR) 
		}; 
		
//		for tab keys 
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		
		enableCursor(); 
	}
	
	ViewPort getView() {
		return m_view;
	}
	
	void reSize(int w, int h) {
		m_img = createImage(w, h);
	}
	
	public Graphics getGraph() {
		return getGraphics();
	}
	
	private void disableCursor() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image image = tk.createImage("");
		Cursor cursor = tk.createCustomCursor(image, new Point(0, 0), "Anything");
		setCursor(cursor);
	}
	
	private void enableCursor() {
		if (++m_cursor_index >= m_cursors.length) {
			m_cursor_index = 0;
		}
		
		setCursor(m_cursors[m_cursor_index]); 
	}
	
	@Override
	public void paint(Graphics g) { 
//		System.out.printf("====paint| w=%d| h=%d|\n", getWidth(), getHeight());
		
		render(g);
	}
	
	public void bsShow() {
		Graphics g = m_img.getGraphics();
//		System.out.printf("===bs show\n");
		render(g);
		
		g = getGraph();
		g.drawImage(m_img, 0, 0, null); 
	}
	
	public void bsShow_() {
		do {
			do {
				Graphics g = bs.getDrawGraphics();
				
				render(g);
				g.dispose();
			} while (bs.contentsRestored());
			System.out.println("===bs show");
			bs.show();
		} while (bs.contentsLost());
	}
	
	void render(Graphics g) {
		m_view.render(g);
	}
}
