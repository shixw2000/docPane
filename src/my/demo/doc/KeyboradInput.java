package my.demo.doc;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboradInput implements KeyListener{
	final int MASK1 = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
	final int MASK2 = InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
	private DocModel m_operator;
	private char m_surrogate0;
	
	KeyboradInput(DocModel operator) {
		m_operator = operator;
		m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
	}
	
	@Override
	public void keyReleased(KeyEvent e) { 
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int modifier = e.getModifiersEx();
		int code = e.getKeyCode();

		/* surrogate reset for keystroke */
		m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
		
		if ((modifier & MASK1) == 0) { 
			procNormalCmd(code); 
		} else if (modifier == InputEvent.CTRL_DOWN_MASK) {
			procCtrlCmd(code); 
		} else {
			procOtherCmd(code);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		int modifier = e.getModifiersEx();
		char c = e.getKeyChar();
		
		System.out.printf("code=0x%x| char=%c|\n", (int)c, c);
		
		if (0 == (modifier & MASK2) && KeyEvent.CHAR_UNDEFINED != c) { 
			if (!DocTool.isSurrogate(c)) {
				procOriginChar(c);
				
				m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
			} else if (KeyEvent.CHAR_UNDEFINED == m_surrogate0) {
				/* the high surrogate part and wait */
				m_surrogate0 = c;
			} else if (DocTool.isSupplementary(m_surrogate0, c)){
				/* the low surrogate part */
				procSurrogate(m_surrogate0, c);
				
				m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
			} else {
				/* invalid surrogate pairs, ignore */
				m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
			}
		} else {
			ignoreCtrlChar(c);
			m_surrogate0 = KeyEvent.CHAR_UNDEFINED;
		} 
	}
	
	private void procOriginChar(char ch) {
		m_operator.handleChar(ch);
	}
	
	private void procSurrogate(char c1, char c2) {
		m_operator.handleChar(c1, c2);
	}
	
	private void ignoreCtrlChar(char ch) {
		
	}
	
	private void procNormalCmd(int code) {
		if (KeyEvent.VK_LEFT == code) {
			m_operator.foreward();
		} else if (KeyEvent.VK_RIGHT == code) {
			m_operator.backward();
		} else if (KeyEvent.VK_UP == code) {
			m_operator.up();
		} else if (KeyEvent.VK_DOWN == code) {
			m_operator.down();
		} else {
//			m_operator.operate(Operator.OperCmd.OPER_CMD_NOOP, code);
		} 
	}
	
	private void procCtrlCmd(int code) {
		if (KeyEvent.VK_V == code) {
			m_operator.copyFromClip();
		} else if (KeyEvent.VK_C == code) {
			m_operator.paste2Clip();
		}
	}
	
	private void procOtherCmd(int code) {
		
	}
}
