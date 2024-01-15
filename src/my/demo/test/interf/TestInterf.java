package my.demo.test.interf;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


class CanvasInScrollPane extends JPanel {
    private static final Dimension CANVAS_SIZE = new Dimension(300, 300);
    private static final Dimension APP_SIZE = new Dimension(500, 250);
    Canvas canvas = new Canvas();
    JPanel panel = new JPanel();

    public CanvasInScrollPane() {
        canvas.setPreferredSize(CANVAS_SIZE);
        canvas.setBackground(Color.blue);

        panel.setPreferredSize(CANVAS_SIZE);
        panel.setBackground(Color.red);

        setPreferredSize(APP_SIZE);
        setLayout(new GridLayout(1, 0, 5, 0));
        add(new JScrollPane(canvas));
        add(new JScrollPane(panel));
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("CanvasInScrollPane");
        frame.getContentPane().add(new CanvasInScrollPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }
}
