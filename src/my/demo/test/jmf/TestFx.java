package my.demo.test.jmf;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;
import javafx.scene.Scene;


public class TestFx extends Application {
	static public void main(String[] argv) {
		System.out.println("helo, world");
		
		new TestFx().launch(argv);
	}
	
	@Override
	public void start(Stage stage) {
		System.out.println("Inside start method");
		
		stage.setTitle("javaFx Skeleton");
		FlowPane rootNode = new FlowPane();
		Scene scene = new Scene(rootNode, 300, 200);
		stage.setScene(scene);
		stage.show();
	}
}