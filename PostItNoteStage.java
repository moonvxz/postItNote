package application;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

/*some lambda expressions are used in this code with Event Handler*/

public class PostItNoteStage {
	//set variables
	BorderPane content;			//text area
	BorderPane buttonArea;		//button area
	BorderPane bottom;			//button area for resizing
	Button newPostItNote;		//add new post it notes (+)
	Button deletePostItNote;	//delete post it note (x)
	Button resizePostItNote;	//resize (/)
	TextArea textArea;			//textArea to take notes
	Font buttonFont;			//font for the new or delete buttons
	
	double x;					//horizontal position - mouse event
	double y;					//vertical position - mouse event
	int count = 0;				// count the number of times of add button for new post it note 
	
	ContextMenu rightClickMenu; //context menu for the right click
	MenuItem cut;				//menu items
	MenuItem copy;
	MenuItem paste;
	MenuItem about;
	MenuItem exit;
	
	Clipboard clipboard;		//clipboard
	ClipboardContent textContent;
	
	//mouse coordinate, offset X and Y, width and height
	double mousedragx;
	double mousedragy;
	double dragX;
	double dragY;
	double width;
	double height;
	
	/** constructor with 4 parameters
	 * @param sizeX - width of the application
	 * @param sizeY - height of the application
	 * @param positionX - starting position -horizontal axis
	 * @param positionY - starting position -vertical axis
	 */
	public PostItNoteStage(double sizeX, double sizeY, double positionX, double positionY) {		
		//initiate the variables
		content = new BorderPane();
		buttonArea = new BorderPane();
		newPostItNote = new Button("+");
		deletePostItNote = new Button("x");
		resizePostItNote = new Button("<=>");
		textArea = new TextArea();	
		bottom = new BorderPane();
		
		//add button to the buttonArea
		buttonArea.setLeft(newPostItNote);
		buttonArea.setRight(deletePostItNote);
		bottom.setRight(resizePostItNote);
		
		//create a new stage
		Stage stage = new Stage();
		
		//set the position of the stage with taking param
		stage.setX(positionX);
		stage.setY(positionY);
		
		//to remove own title bar to the window
		stage.initStyle(StageStyle.UNDECORATED);

		//create a new Scene - layout, size
		Scene scene = new Scene(content, sizeY, sizeX);
		
		//set the background colours
		content.setStyle("-fx-background-color: rgb(253, 253, 201)");
		textArea.setStyle("-fx-background-color: rgb(253, 253, 201)");
		resizePostItNote.setStyle("-fx-background-color: rgb(253, 253, 201)");
		buttonArea.setStyle("-fx-background-color: rgb(248, 247, 182)");
		newPostItNote.setStyle("-fx-background-color: rgb(248, 247, 182)");
		deletePostItNote.setStyle("-fx-background-color: rgb(248, 247, 182)");
		
		//alter the font size and colours
		buttonFont = Font.font("Arial", FontWeight.BOLD,20);
		newPostItNote.setFont(buttonFont);
		newPostItNote.setTextFill(Color.GREY);
		deletePostItNote.setFont(buttonFont);
		deletePostItNote.setTextFill(Color.GREY);
		resizePostItNote.setFont(Font.font("Arial", 13));
		resizePostItNote.setTextFill(Color.GREY);
				
		content.setTop(buttonArea);
		content.setCenter(textArea);
		
		//set the scene
		stage.setScene(scene);
		stage.show();
		
		//detect horizontal scroll bar
		textArea.setWrapText(true);
		
		//make the window resizable
		buttonArea.setOnMousePressed(e-> {
			//set offset for the X and Y
			mousedragx = e.getSceneX();
			mousedragy = e.getSceneY();				
		});
		
		buttonArea.setOnMouseDragged(e-> {
			//set the position of the stage
			stage.setX(e.getScreenX() - mousedragx);
			stage.setY(e.getScreenY() - mousedragy);			
		});
		
		//set the resize button on the bottom right of the screen
		content.setBottom(resizePostItNote);
		BorderPane.setAlignment(resizePostItNote,Pos.BOTTOM_RIGHT);
		
		//save the current stage x and y and mouse coordinates
		resizePostItNote.setOnMousePressed(e-> {
			dragX = e.getSceneX();
			dragY = e.getSceneY();
			height = stage.getHeight();
			width = stage.getWidth();	
		});
		
		//add functions to the resize button
		resizePostItNote.setOnMouseDragged(e-> {
			stage.setHeight(height + (e.getSceneY() - dragY));
			stage.setWidth(width + (e.getSceneX() - dragX));
			
			//set the minimum height and width
			if (stage.getHeight() < sizeY || stage.getWidth() < sizeX) {
				stage.setHeight(sizeY);
				stage.setWidth(sizeX);
			}
		});
		
		//change the cursor when resize button is hovered
		resizePostItNote.setOnMouseEntered(e-> {
			scene.setCursor(Cursor.SE_RESIZE);
		});
		
		//resize button is not hovered, cursor changes to default
		resizePostItNote.setOnMouseExited(e-> {
			scene.setCursor(Cursor.DEFAULT);
		});
		
		//cast the inner part of TextArea as a Region
		Region region = (Region) textArea.lookup(".content");
		region.setStyle("-fx-background-color: rgb(253, 253, 201)");
		
		EventHandler<ActionEvent> newButton = new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				x = stage.getX();
				y = stage.getY();
				
				//multiply count with the width to avoid new windows overlapping
				double newX = x + stage.getWidth() + 15 + (stage.getWidth() * count);
				double newY = y;
				
				//check we fit within the screen x size
				Rectangle2D screen = Screen.getPrimary().getVisualBounds();
				if(stage.getX() + stage.getWidth()*2 > screen.getWidth()){
					newX = 0 + (stage.getWidth() * count);
					newY = stage.getY() + stage.getHeight();
				}
				//new position
				new PostItNoteStage(sizeX,sizeY,newX,newY);
		
				count++;	
			}	
		};
		newPostItNote.setOnAction(newButton);
			
		//close post it notes
		deletePostItNote.setOnAction(e -> {
			stage.close();
		});
			
		//create a right click ContextMenu,
		rightClickMenu = new ContextMenu();
		//then create MenuItem
		cut = new MenuItem("Cut");
		copy = new MenuItem("Copy");
		paste = new MenuItem("Paste");
		about = new MenuItem("About");
		exit = new MenuItem("Exit");
		
		//add all items to rightClickMenu
		rightClickMenu.getItems().addAll(cut,copy,paste,about,exit);			

		//display menu instead of the default
		textArea.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);	
		
		//right click event handler
		EventHandler<MouseEvent> rightClick = new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e) {
				//if right clicked, menu appears
				if(e.getButton() == MouseButton.SECONDARY) {
					rightClickMenu.show(content, e.getScreenX(), e.getScreenY());							 
				}		
				//else if left clicked, menu hides
				else if (e.getButton()== MouseButton.PRIMARY) {
					//check if the menu is visible
					if (rightClickMenu.isShowing()) {
						rightClickMenu.hide();						
					}					
				}
			}
		};
		textArea.setOnMouseClicked(rightClick);

		//action for cut
		cut.setOnAction(e-> {			
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent textContent = new ClipboardContent();
			textContent.putString(textArea.getSelectedText());
			clipboard.setContent(textContent);
			textArea.deleteText(textArea.getSelection());			
		});
		
		//action for paste
		paste.setOnAction(e-> {
			textArea.appendText((String)Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT));
		});
		
		//action for copy
		copy.setOnAction(e-> {			
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent textContent = new ClipboardContent();
			textContent.putString(textArea.getSelectedText());
			clipboard.setContent(textContent);
		});
			
		//action for about dialog
		about.setOnAction(e-> {	
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Post-It Note");
			alert.setHeaderText("Post-It Note");
			
			Image image = new Image(getClass().getResource("me.jpg").toExternalForm());
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(150);
			imageView.setFitWidth(130);
			
			GridPane grid = new GridPane();
			Label label1 = new Label(" Digital Post-It Note using JavaFX");	
			Label label2 = new Label(" Version 1.0");
			Label label3 = new Label(" Author: Gabriella Shim");
			Label label4 = new Label(" Copyright(c) 2020");
						
			//put the image on the left then span across 5 rows
			grid.add(imageView, 0, 0, 1, 5);
			grid.add(label1, 1, 0);
			grid.add(label2, 1, 1);
			grid.add(label3, 1, 2);
			grid.add(label4, 1, 3);
			alert.getDialogPane().setContent(grid);
			alert.showAndWait();
		});

		//actions for exit - close window
		exit.setOnAction(e-> {
			stage.close();
		});
	}
}


