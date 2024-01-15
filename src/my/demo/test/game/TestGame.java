package my.demo.test.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.JFrame;

public class TestGame extends JFrame {
	private BufferStrategy bs;
	private boolean running;
	private boolean relative;
	private Canvas canvas;
	private Point point;
	private Thread thr;
	
	TestGame() {
		
	}
	
	void createGui() {
		MouseAdapter mouse = null;
		KeyAdapter keyboard = null;
		
		point = new Point();
		canvas = new Canvas();
		canvas.setSize(640, 480);
		canvas.setBackground(Color.BLACK);
		canvas.setIgnoreRepaint(true);
		
		getContentPane().add(canvas);
		setTitle("Test game");
		setIgnoreRepaint(true);
		pack();
		
		keyboard = new KeyAdapter() {
		}; 
		
		mouse = new MouseAdapter() {
			
		};
		
		canvas.addKeyListener(keyboard);
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);
		canvas.addMouseWheelListener(mouse);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();
		canvas.requestFocus(); 
		
		thr = new Thread(new Runnable() {
			
			@Override
			public void run() {
				running = true;
				
				// TODO Auto-generated method stub
				while (running) {
					processInput();
					renderFrame();
					sleep(10);
				}
			}
		});
		thr.start();
	}
	
	private void renderFrame() {
		Graphics g = null;
		
		try {
			do {
				do {
					g = bs.getDrawGraphics();
					g.clearRect(0, 0, getWidth(), getHeight());
					render(g);
					
					g.dispose();
				} while (bs.contentsRestored());
				
				bs.show();
			} while (bs.contentsLost());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void render(Graphics g) {
		g.setColor(Color.GREEN);
		g.drawString(point.toString(), 20, 20);
		g.drawString("Relative: "+ relative, 20, 35);
		g.drawString("Press C to toggle cursor", 20, 50);
		g.setColor(Color.WHITE);
		g.drawRect(point.x, point.y, 25, 25);
	}
	
	private void disableCursor() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image image = tk.createImage("");
		Cursor cursor = tk.createCustomCursor(image, new Point(0, 0), "Anything");
		setCursor(cursor);
	}
	
	private void sleep( int msec) {
		try {
			Thread.sleep(msec);
		} catch (Exception e) {
			
		}
	}
	
	private void processInput() {
		
	}
 
	public static void main(String[] args) {
		TestGame tg = new TestGame();
		
		tg.createGui(); 
	}
}
