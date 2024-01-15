package my.demo.doc;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.stream.Collector.Characteristics;

import javax.imageio.ImageIO;

public final class DocTool { 
	public static String getFromClip() {
		String txt = null;
		Image img = null;
		
		try {
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable ctx = clip.getContents(null);
			
			if (ctx.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				txt = (String)ctx.getTransferData(DataFlavor.stringFlavor);
				System.out.printf("getClip: len=%d| txt=%s|\n", txt.length(), txt);
			} 

			if (ctx.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				img = (Image)ctx.getTransferData(DataFlavor.imageFlavor);
				BufferedImage bi = new BufferedImage(img.getWidth(null),
						img.getHeight(null), 
						BufferedImage.TYPE_INT_RGB);
						
				java.awt.Graphics g = bi.getGraphics();
				g.drawImage(img, 0, 0, null);
				ImageIO.write(bi, "jpg", new FileOutputStream("s.jpg"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return txt;
	}
	
	public static void set2Clip(String txt) { 
		try {
			if (null != txt && !txt.isEmpty()) {
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable data = new StringSelection(txt);
	
				clip.setContents(data, null);
				
				System.out.printf("setClip: len=%d| txt=%s|\n", txt.length(), txt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isSupplementary(int cp) {
		return Character.isSupplementaryCodePoint(cp);
	}
	
	public static boolean isConcatenate(int cp) {
		return cp == 0x200d;
	}
	
	public static boolean isSupplementary(char ch1, char ch2) {
		if (Character.isSurrogatePair(ch1, ch2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isSurrogate(char c) {
		return Character.isSurrogate(c);
	}
	
	public static int toCP(char ch1, char ch2) {
		return Character.toCodePoint(ch1, ch2);
	}
	
	public static char[] toChars(int cp) {
		return Character.toChars(cp);
	}
	
	public static boolean isChnChar(char c) {
		return Character.isIdeographic(c) || 
				isHanPunct(c);
	}
	
	public static boolean isHan(char c) {
		Character.UnicodeScript cu = Character.UnicodeScript.of(c);
		
		if (Character.UnicodeScript.HAN == cu) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isHanPunct(char c) {
		Character.UnicodeBlock cu = Character.UnicodeBlock.of(c);
		
		if (Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION == cu ||
				Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS == cu ||
				Character.UnicodeBlock.VERTICAL_FORMS == cu ||
				Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS == cu ||
				Character.UnicodeBlock.GENERAL_PUNCTUATION == cu) { 
			return true;
		} else {
			return false;
		}
	}
}
