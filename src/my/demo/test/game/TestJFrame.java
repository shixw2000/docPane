package my.demo.test.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestJFrame extends JPanel {
	Font font = new Font("Segoe UI Emoji", Font.PLAIN, 24);
	
	public static void main(String[] args) {
		JFrame jf = new JFrame("test canvas");
		JPanel jp = new TestJFrame();
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
		jf.add(jp);
		jf.pack();
		jf.setVisible(true); 
	}
	
	TestJFrame() {
		setLayout(null);
		setPreferredSize(new Dimension(350, 250));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		String str = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66";
		
		System.out.printf("len=%s|\n", str);
		
//		g.setColor(Color.RED);
		g.setFont(font);
//		g.drawChars(str2.toCharArray(), 0, 2, 50, 50);
		g.drawString(str, 100, 100);
	}
}
