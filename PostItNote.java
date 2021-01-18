package application;
import javafx.application.Application;
import javafx.stage.Stage;

//Author: Gabriella Shim (110299577)

public class PostItNote extends Application {
	PostItNoteStage mainWindow;
	
	public static void main(String[] args) {
        System.out.println("Starting Post-It Note application...");
        System.out.println("Author: Gabriella Shim (yoosy007)");
        launch(args);
    }
	
	//@Override
    public void start(Stage arg0) throws Exception {
    	//create the main window for first Post-It Note
    	mainWindow = new PostItNoteStage(200,200,100,100);
    }

}