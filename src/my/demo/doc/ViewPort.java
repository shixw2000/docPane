package my.demo.doc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ViewPort {
	private DocModel m_model;
	private DocView m_view;
	private MouseCtx m_mouse_hd;
	private KeyboradInput m_key_hd;
	
	ViewPort(DocPane component) {
		m_view = new DocView(component);
		m_model = new DocModel();
		m_mouse_hd = new MouseCtx(m_model); 
		m_key_hd = new KeyboradInput(m_model);
		
		m_model.setView(m_view);
		register(component);
	}
	
	void register(DocPane component) {
		component.addKeyListener(listenKey());
		component.addMouseListener(listenMouse());
		component.addMouseMotionListener(listenMouseMotion());
		component.addComponentListener(listenComponent());
		
//		component.setIgnoreRepaint(true);
		
		component.setMinimumSize(new Dimension(m_view.minW(), m_view.minH()));
		component.setPreferredSize(new Dimension(m_view.width(), m_view.height())); 
	}
	
	void setFont(String fontFam, int fontStyle, int fontSize) {
		int id = m_view.creatFont(fontFam, fontStyle, fontSize);
		
		m_model.setAttrs(EnumAttr.ATTR_FONT, id);
	}
	
	public void addImage(String path) {
		m_model.handleImage(path);
	}
	
	public void setForeColor(Color c) {
		int id = m_view.creatColor(c);
		
		m_model.setAttrs(EnumAttr.ATTR_FORE_COLOR, id);
	}
	
	public void setBackColor(Color c) {
		int id = m_view.creatColor(c);
		
		m_model.setAttrs(EnumAttr.ATTR_BACK_COLOR, id);
	}
	
	public void unsetFont() {
		m_model.unsetAttrs(EnumAttr.ATTR_FONT);
	}
	
	public void unsetForeColor() {
		m_model.unsetAttrs(EnumAttr.ATTR_FORE_COLOR);
	}
	
	public void unsetBackColor() {
		m_model.unsetAttrs(EnumAttr.ATTR_BACK_COLOR);
	}
	
	KeyListener listenKey() {
		return m_key_hd;
	}
	
	MouseListener listenMouse() {
		return new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				/*
				 * System.out.printf("mouse released: button=0x%x:%d| modifiers=0x%x:0x%x|\n",
				 * e.getButton(), e.getClickCount(), e.getModifiers(), e.getModifiersEx());
				 */
				
				m_mouse_hd.released(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				/*
				 * System.out.printf("mouse pressed: x=(%d, %d), button=0x%x:%d| modifiers=0x%x:0x%x|\n",
				 * e.getX(), e.getY(), e.getButton(), e.getClickCount(), e.getModifiers(),
				 * e.getModifiersEx());
				 */
				
				m_mouse_hd.pressed(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				/*
				 * System.out.
				 * printf("mouse clicked: x=(%d, %d), button=0x%x:%d| modifiers=0x%x:0x%x|\n",
				 * e.getX(), e.getY(), e.getButton(), e.getClickCount(), e.getModifiers(),
				 * e.getModifiersEx());
				 */
			}
		};
	}
	
	MouseMotionListener listenMouseMotion() {
		return new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				/*
				 * System.out.printf("move: x=(%d, %d), button=0x%x| modifiers=0x%x:0x%x|\n",
				 * e.getX(), e.getY(), e.getButton(), e.getModifiers(), e.getModifiersEx());
				 */
				
				m_mouse_hd.move(e);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				/*
				 * System.out.printf("drag: x=(%d, %d), button=0x%x| modifiers=0x%x:0x%x|\n",
				 * e.getX(), e.getY(), e.getButton(), e.getModifiers(), e.getModifiersEx());
				 */ 
				
				m_mouse_hd.drag(e); 
			}
		};
	}
	
	ComponentListener listenComponent() {
		
		return new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("component show");
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				Component comp = e.getComponent();
				
				m_model.resize(comp.getWidth(), comp.getHeight());
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				System.out.println("component hidden");
			}
		};
	}
	
	void render(Graphics g) {
		m_model.render(g);
	}
}
