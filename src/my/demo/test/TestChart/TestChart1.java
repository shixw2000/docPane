package my.demo.test.TestChart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestChart1 {
	public static void main(String[] args) {
		String title = "CNNIC:locations";
		JFrame jf = new JFrame(title);
		
		double[] values = {0.76, 0.334, 0.323, 0.126, 0.009, 0.002};
		String[] names = {"home", "work", "netbar", "school", "public", "other"};
		
		jf.getContentPane().add(new BarChart(values, names, title));
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jf.setSize(600, 250);
//		jf.pack();
		jf.setVisible(true); 
	}
}

class BarChart extends JPanel {
	private double[] values;
	private String[] names;
	private String title;
	private double min =0;
	private double max =0;
	
	BarChart(double[] v, String[] n, String t) {
		values = v;
		names = n;
		title = t;
		
		min = values[0];
		max = values[0];
		
		for (int i=1; i<values.length; ++i) {
			if (min > values[i]) {
				min = values[i];
			}
			
			if (max < values[i]) {
				max = values[i];
			}
		}
		double delta = (max - min) * 0.05;
		min -= delta;
		max += delta;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g; 
		
		render(g2d);
	}
	
	void render(Graphics2D g2d) {
		Dimension dim = getSize();
		Font titleFont = new Font("SansSerif", Font.BOLD, 20);
		FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
		Font lableFont = new Font("SansSerif", Font.PLAIN, 10);
		FontMetrics labelMetrics = g2d.getFontMetrics(lableFont);
		
		int titleWidth = titleMetrics.stringWidth(title);
		
		int x = (dim.width - titleWidth) / 2;
		int y = titleMetrics.getAscent();
		g2d.setFont(titleFont);
		g2d.drawString(title, x, y);
		
		int top = titleMetrics.getHeight();
		int bottom = labelMetrics.getHeight();
		double scaleX = dim.getWidth() / values.length;
		double scaleY = (dim.height - top - bottom) / (max - min);
		y = dim.height - labelMetrics.getDescent();
		
		g2d.drawLine(10, top, 10, dim.height - bottom);
		g2d.drawLine(10, dim.height - bottom, dim.width, dim.height - bottom);
		for (int i=0; i<values.length; ++i) {
			int width = (int)(scaleX - 10);
			int height = (int)(scaleY * (values[i] - min));
			int X = (int)(i * scaleX + 10);
			int Y = (int)(dim.height - bottom - height);
			
			g2d.setColor(Color.red);
			g2d.fill3DRect(X, Y, width, height, true);
			
			g2d.setColor(Color.black);
			g2d.draw3DRect(X, Y, width, height, true);
			
			g2d.setFont(lableFont);
			g2d.drawString(names[i], X, y);
		}
	}
}

