package my.demo.doc;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MouseCtx { 
	private DocModel m_operator;
	private boolean m_pressed;
	
	MouseCtx(DocModel operator) {
		m_operator = operator;
	}
	
	public void pressed(MouseEvent e) { 
		int modifier = e.getModifiersEx();
		
		if (modifier == InputEvent.BUTTON1_DOWN_MASK) {
			m_pressed = true;
			m_operator.seek(e.getX(), e.getY());
		} else {
			m_pressed = false;
			m_operator.noop();
		}
	}
	
	public void released(MouseEvent e) { 
		int modifier = e.getModifiers();
		
		if (modifier == KeyEvent.BUTTON1_MASK && m_pressed) {
			m_pressed = false;
		}
	}
	
	public void move(MouseEvent e) {
	}
	
	public void drag(MouseEvent e) {
		int modifier = e.getModifiersEx();

		if (modifier == InputEvent.BUTTON1_DOWN_MASK && m_pressed) {
			m_operator.drag(e.getX(), e.getY());
		} 
	}
}
