package my.demo.test.jmf;

import java.net.InetAddress;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionManager;
import javax.media.rtp.rtcp.SourceDescription;
import javax.swing.JFrame;

public class TestJmf_01 extends JFrame implements ControllerListener  {
	private CaptureDeviceInfo cdi;
	private Player player;
	private Vector<CaptureDeviceInfo> audioDevs;
	private Vector<CaptureDeviceInfo>  videoDevs;
	
	public static void main(String[] argv) throws Exception {
		TestJmf_01 test = new TestJmf_01();
		
		B b = new B();
		b.disp(3);
		
		System.out.printf("========end=========\n");
	}
	
	void init() {
		audioDevs = new Vector<>();
		videoDevs = new Vector<>();
		
		Vector<CaptureDeviceInfo> devList = CaptureDeviceManager.getDeviceList(null);
		
		if (null != devList && 0 < devList.size()) {
			Format[] formats = null;
			
			for (CaptureDeviceInfo t : devList) {
				System.out.println(t.getName());
				
				formats = t.getFormats();
				
				for (Format f : formats) {
					System.out.printf("encodeing=%s| class=%s|\n", 
							f.getEncoding(), f.getDataType().toString());
					
					if (f instanceof AudioFormat) {
						audioDevs.add(t);
						break;
					} else if (f instanceof VideoFormat) {
						videoDevs.add(t);
						break;
					}
				}
			}
		}
	}
	
	void createAudio() throws Exception {
		if (audioDevs.isEmpty()) {
			return;
		}
		
		cdi = audioDevs.firstElement();
		player = Manager.createPlayer(cdi.getLocator());
		player.addControllerListener(this);
	}
	
	void test() throws Exception { 
		SessionManager sm = new com.sun.media.rtp.RTPSessionMgr();
		SessionAddress laddr = new SessionAddress();
		InetAddress addr = InetAddress.getByName("www.baidu.com");
		SourceDescription sds[] = new SourceDescription[] {
				new SourceDescription(SourceDescription.SOURCE_DESC_EMAIL, 
						"a@sina.com", 1, false),
				new SourceDescription(SourceDescription.SOURCE_DESC_CNAME, 
						sm.generateCNAME(), 1, false),
				new SourceDescription(SourceDescription.SOURCE_DESC_TOOL, 
						"jmf 2.0", 1, false) 
		};
		
		SessionAddress saddr = new SessionAddress(addr, 8888, addr, 8889);
		
		sm.initSession(laddr, sds, 0.05, 0.25);
		sm.startSession(saddr, 2, null);
		
		System.out.println("addr=" + addr.getHostAddress());
	}

	@Override
	public void controllerUpdate(ControllerEvent ce) {
		if (ce instanceof RealizeCompleteEvent) {
			
		}
	}
}

class A {
	int disp(int x) {
		System.out.println("In A");
		return x;
	}
}

class B extends A {
	void disp() {
		super.disp(3);
		
		System.out.println("In B");
	}
}

class K {
	public static void main(String[] argv) {
		B b = new B();
		
		b.disp();
	}
}
