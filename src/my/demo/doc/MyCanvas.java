package my.demo.doc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import javafx.stage.FileChooser;

public class MyCanvas {
	private String m_cwd;
	DocPane pane = new DocPane();
	JFrame m_jf = new JFrame("test canvas");
	JComboBox<String> fontFamily;
	JTextField txtSize;
	JLabel jl1;
	JLabel jl2;
	Icon icon;
	
	MyCanvas() {
		String dir = System.getProperty("user.dir");
		m_cwd = Paths.get(dir).toAbsolutePath().toString();
	}
	
	public static void main(String[] args) {
		MyCanvas canvas = new MyCanvas();
		
		SwingUtilities.invokeLater(() -> canvas.start()); 
	}
	
	public void start() {
		m_jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		creatToolbar(m_jf);
		creatMenu(m_jf);
		
		m_jf.add(pane, BorderLayout.CENTER);
		m_jf.pack();
		m_jf.setVisible(true); 
		
		pane.requestFocus();
	}
	
	private void setFont() {
		String fam = (String)fontFamily.getSelectedItem();
		int size = Integer.parseInt(txtSize.getText());
		int style = Font.PLAIN;
		
		pane.getView().setFont(fam, style, size);
	}
	
	private void setColor(boolean isFore) {
		Color c = JColorChooser.showDialog(m_jf, "choose a color", Color.RED);
		
		if (null != c) {
			if (isFore) {
				pane.getView().setForeColor(c);
			} else {
				pane.getView().setBackColor(c);
			}
		}
	}
	
	public void creatToolbar(JFrame jf) {
		JToolBar toolbar = new JToolBar("My Tool Bar");
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] famNames = ge.getAvailableFontFamilyNames();
		Box box = new Box(BoxLayout.Y_AXIS);
		Box boxImg = new Box(BoxLayout.Y_AXIS);
		JPanel jp = new JPanel(new GridLayout(2, 3));
		JButton btn1 = new JButton("setFont");
		JButton btn2 = new JButton("setForeColor");
		JButton btn3 = new JButton("setBackColor");
		JButton btn4 = new JButton("unsetFont");
		JButton btn5 = new JButton("unsetForeColor");
		JButton btn6 = new JButton("unsetBackColor");
		JButton btnImg = new JButton("add image");

		fontFamily = new JComboBox<String>(famNames);
		txtSize = new JTextField("16");
		
		fontFamily.setSelectedItem("Courier New");
		
		box.add(fontFamily);
		box.add(txtSize);
		
		boxImg.add(btnImg);
		
		jp.add(btn1);
		jp.add(btn2);
		jp.add(btn3);
		jp.add(btn4);
		jp.add(btn5);
		jp.add(btn6);
		btn1.addActionListener(e -> setFont());
		btn2.addActionListener(e -> setColor(true));
		btn3.addActionListener(e -> setColor(false));
		btn4.addActionListener(e -> pane.getView().unsetFont());
		btn5.addActionListener(e -> pane.getView().unsetForeColor());
		btn6.addActionListener(e -> pane.getView().unsetBackColor());
		btnImg.addActionListener(e -> chooseImage());

		toolbar.add(box);
		toolbar.add(jp);
		toolbar.add(boxImg);
		jf.add(toolbar, BorderLayout.NORTH);
	}
	
	private void chooseImage() {
		JFileChooser jfc = new JFileChooser(m_cwd);
		int ret = 0;
		
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		ret = jfc.showOpenDialog(pane);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File[] files = jfc.getSelectedFiles();
			
			for (File f: files) {
				pane.getView().addImage(f.getAbsolutePath());
			}
		}
	}
	
	public void creatMenu(JFrame jf) {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("file");
		JMenu editMenu = new JMenu("edit");
		JMenu aboutMenu = new JMenu("about");
		
		bar.add(fileMenu);
		bar.add(editMenu);
		bar.add(aboutMenu);
		
		jf.setJMenuBar(bar);
	}
}

