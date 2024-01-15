package my.demo.test.serialize;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class TestSerializable {
	public static int n = 100;
	
	public static void main(String[] args) throws Exception {
		TestSerializable ts = new TestSerializable();
		
		ts.test2();
	}
	
	void test2() {
		JFrame jf = new JFrame();
		JPanel jp = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				char[] str = Character.toChars(0x10400);
				g.drawChars(str, 0, 2, 0, 30);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(250, 200);
			}
		};
		
		jf.add(jp);
		jf.pack();
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	void test() throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		
		D d = new D();
		d.dsp();
		
		out.writeObject(d);
		out.close();
 
		byte[] bs = bo.toByteArray();
		
		System.out.println("size=" + bs.length);
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bs));
		D d1 = (D)in.readObject();
		
		in.close();
		
		d1.dsp();
	}
}

class A {
	int x;
	int y;
	
	A() {
		x = TestSerializable.n++;
		y = TestSerializable.n++;
		
		System.out.printf("In A: x=%d| y=%d|\n", x, y);
	}
}

class B implements Serializable {
	int x;
	int y;
	
	B() {
		x = TestSerializable.n++;
		y = TestSerializable.n++;
		
		System.out.printf("In B: x=%d| y=%d|\n", x, y);
	}
}

class C {
	int x;
	int y;
	A a;
	B b;
	
	C() {
		x = TestSerializable.n++;
		y = TestSerializable.n++;
		
		a = new A();
		b = new B();
		System.out.printf("In C: x=%d| y=%d|\n", x, y);
	}
}

class D implements Serializable {
	int x;
	int y;
	transient A a;
	B b;
	
	D() {
		x = TestSerializable.n++;
		y = TestSerializable.n++;
		
		a = new A();
		b = new B();
		System.out.printf("In D: x=%d| y=%d|\n", x, y);
	}
	
	void dsp() {
		System.out.printf("dsp D: x=%d| y=%d| a.x=%d| a.y=%d| b.x=%d| b.y=%d|\n", 
				x, y, 0, 0, b.x, b.y);
	}
}
