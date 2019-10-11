package paintproject;

import java.awt.image.*;
import java.net.MalformedURLException;
import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 * <h1>Paint Project (Nate Weber)</h1>
 * Paint program is a replica version of Microsoft Paint, Allows user to preform
 * a number of tasks within the program, Some of these functions include file
 * manipulations as well as drawing and image modifications.
 * <p>
 * <b>Note:</b> the project is set to work with images of size 500x500.
 * 
 * @author Nate Weber
 * @version 1.3
 * @since 2017-09-03
 */
public class PaintProject extends Application{
    private final Stack<Image> undoStack = new Stack();
    private final Stack<Image> redoStack = new Stack();
    /*
    Creation of all variables used throughout the program
    */
    private File file;
    private Image image;
    private Rectangle rect = new Rectangle();
    private double sliderValue, x, y;
    private Canvas canvas, lastCanvas, newCanvas;
    private Path path;
    private WritableImage  wi;
    private GraphicsContext graphic, graphicNew;
    private BorderPane fullPane;
    private ColorPicker colorpicker;
    private VBox topMenu;
    private Slider slider;
    private FlowPane colorPane, modificationPane;
    private StackPane imagePane;
    private Line line;
    private Label label;
    private ImageView viewMyImage;
    private Alert alert;
    private Scene scene;
    private Menu fileMenu, editMenu, viewMenu;
    private MenuItem openDD, saveDD, saveAsDD, undoDD, redoDD, clearDD, freeDraw, 
            snapDraw, clearDraw, squareDraw, eraseDraw, circleDraw, moveDrawn, 
            selectionTool, addText, dropper;    
    @Override
    /**
    * The start method includes all of the layouts and menu set up, is also includes
    * setting actions for each button or option.
    * 
    * @author Nate
    * @param Stage
     */
    public void start(Stage primaryStage){
        
        
    //MENUBAR LAYOUT CREATION
        MenuBar menuBar = new MenuBar(); //Creates MenuBar (location top left)
        menuBar.setLayoutX(0);
        menuBar.setLayoutY(0);
        fileMenu = new Menu("File");    //Menu headers
        editMenu = new Menu("Edit");
        viewMenu = new Menu("View");
        openDD = new MenuItem("Open Image");    //Open file drop down
        openDD.setOnAction(open);
        openDD.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+O"));   //Key combinations for shortcuts
        saveAsDD = new MenuItem("Save as...");  //Save as drop down 
        saveAsDD.setOnAction(saveas);
        saveAsDD.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        saveDD = new MenuItem("Save");  //Save drop down
        saveDD.setOnAction(save);
        saveDD.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        undoDD = new MenuItem("Undo");  //Undo drop down
        undoDD.setOnAction(undo);
        undoDD.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        redoDD = new MenuItem("Redo");  //Redo drop down
        redoDD.setOnAction(redo);
        redoDD.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));
        clearDD = new MenuItem("Clear");  //Clear drop down
        clearDD.setOnAction(clearCanvas);
        
        fileMenu.getItems().addAll(openDD, saveDD, saveAsDD);   //adds drop downs under menu headers
        editMenu.getItems().addAll(undoDD, redoDD, clearDD);     
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);    //adds headers to MenuBar
       
    //LINE CHOICE SELECTION
        MenuButton lineChoice = new MenuButton("Draw Choice");
        MenuButton drawOptions = new MenuButton("Draw Options");
        freeDraw = new MenuItem("Free Draw");   //menuItems under drop downs
        snapDraw = new MenuItem("Snap Draw");
        squareDraw = new MenuItem("Square Draw");
        clearDraw = new MenuItem("Clear Draw Choice");
        eraseDraw = new MenuItem("Eraser");
        circleDraw = new MenuItem("Circle Draw");
        moveDrawn  = new MenuItem("Move Drawn Object");
        selectionTool = new MenuItem("Selection Tool");
        addText = new MenuItem("Add Text");
        dropper = new MenuItem("Image Color Select");
        freeDraw.setOnAction(freeDrawAction);   //sets actions for drop down actions
        snapDraw.setOnAction(snapDrawAction);
        squareDraw.setOnAction(squareDrawAction);
        clearDraw.setOnAction(clearDrawAction);
        eraseDraw.setOnAction(eraseAction);
        circleDraw.setOnAction(circleDrawAction);
        selectionTool.setOnAction(selectionAction);
        moveDrawn.setOnAction(moveAction);
        addText.setOnAction(addTextAction);
        dropper.setOnAction(dropperAction);
        lineChoice.getItems().addAll(freeDraw, snapDraw, squareDraw, circleDraw);
        drawOptions.getItems().addAll(clearDraw, eraseDraw, moveDrawn, selectionTool, addText, dropper);      
        
    //IMAGE LAYOUT CREATION
        viewMyImage = new ImageView();
        viewMyImage.setLayoutX(10);
        viewMyImage.autosize();
        //sets image on open to white
        image = new Image("http://www.russellandtate.com/blog/wp-content/uploads/2014/02/2015-03-31-blank-white-square-500x500.png");
        viewMyImage.setImage(image);
        
    //COLOR MODIFICATIONS
        colorpicker = new ColorPicker();    //creates a colorpicker
        colorpicker.setValue(Color.BLACK);  //default color is black
        colorpicker.setOnAction(colorswitch); //Set action for colorpicker use
        
    //WINDOW SMART CLOSE
        primaryStage.setOnCloseRequest(smartClose); //sets smartclose on exit click
        
    //LINE WIDTH ADJUSTMENTS
        slider = new Slider();  //creation of slider for width adjustment
        slider.setMin(1);   //allows value to be set 1-15
        slider.setMax(15);  
        label = new Label("1.0"); //default value of slider is 1
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setOnMouseReleased(setSlider);   //action for slider
        
    //SCENE LAYOUT CREATION
        modificationPane = new FlowPane(lineChoice, drawOptions,colorpicker, slider, label);    //Top pane tools
        modificationPane.setHgap(20);
        modificationPane.setVgap(10);
        modificationPane.setAlignment(Pos.CENTER);
        fullPane = new BorderPane();    //fullPane is everything together
        canvas = new Canvas(500,500);   //overlays image and can be drawn on
        graphic = canvas.getGraphicsContext2D();
        imagePane = new StackPane(viewMyImage,canvas);
        topMenu = new VBox(menuBar);    //pane for menubar at top
        fullPane.setTop(topMenu);
        fullPane.setBottom(imagePane);
        fullPane.setCenter(modificationPane);
        scene = new Scene(fullPane, 500, 600);
        
    //STAGE LAYOUT CREATION
        primaryStage.setTitle("Paint Project - Nate Weber");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    /**
    * This is the main method which launches the paint project.
    * 
    * @author Nate    
    * @param args is a string array
    */
    public static void main(String[] args) {
        launch(args);
        
    }
    /**
    * This method is used to allow the user to save the resulting image to a 
    * file of choice.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> saveas = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            viewMyImage.setImage(image);
            FileChooser fileChooser = new FileChooser();        //creates new filechooser
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //Set extension filters to jpg and png
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
            fileChooser.setTitle("Save Image");
            File file = fileChooser.showSaveDialog(new Stage());    //opens the saveas box
            if (file != null) {     //if file choosen exists save it
                try {
                    WritableImage writableImage = new WritableImage((int)image.getWidth(),(int)image.getHeight());
                    imagePane.snapshot(null, writableImage);        //takes snapshot of current imagePane
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);  //saves image out to choosen file
                }catch(IOException ex) {
                    //BAD FILE PATH ERROR
                }
            }
        }
    };
    /**
    * This method is used to allow the user to save the resulting image to the current
    * file path.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> save = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            viewMyImage.setImage(image);
            if (file != null) {     //if file path exists save it
                try {
                    WritableImage writableImage = new WritableImage((int)image.getWidth(),(int)image.getHeight());
                    imagePane.snapshot(null, writableImage);    //takes snapshot of the current imagePane
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);      //saves image to choose file path
                }catch(IOException ex) {
                    //BAD FILE PATH ERROR
                }
            }
        }
    };
    /**
    * This method is used to save and return a snap shot of the current image.
    * 
    * @return Image This returns the current state of the image.
    * @author Nate
    */
    public Image saveUndoImage(){
        WritableImage wimage = new WritableImage((int)graphic.getCanvas().getWidth(),(int)graphic.getCanvas().getHeight());
        graphic.getCanvas().snapshot(null, wimage); //Copying all that is in Canvas
        //gc is GraphicContext object from Canvas, it has drawing functions
        BufferedImage bi = SwingFXUtils.fromFXImage((Image)wimage, null); 
        return SwingFXUtils.toFXImage(bi, null); //returns an image of current state of canvas
        
    }
    /**
    * This method is used to allow the user to open an image of choice to begin
    * editing.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> open = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t){              
            try{
                FileChooser chooser = new FileChooser();    //creates a new filechooser for open
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //restricts file extensions
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                chooser.setTitle("Open Image");
                file = chooser.showOpenDialog(new Stage()); //opens new dialog box for open
                image = new Image(file.toURI().toURL().toString()); //sets the image to the choosen image
                viewMyImage.setImage(image);    //displays image on canvas
                graphic.drawImage(image, 0, 0);
            }
            catch(MalformedURLException ex){
                //BAD FILE PATH ERROR
            }
        }            
    };
    /**
    * This method allows the user to undo any modification that has been 
    * done to the image.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> undo = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent e) {   
            if (!undoStack.isEmpty()) {     //if the undoStack is NOT empty
                redoStack.push(saveUndoImage());             //Saves previous state to redoStack
                graphic.drawImage(undoStack.pop(), 0, 0);    // Draws previous image onto canvas    
            }
        }
    };
   /**
    * This method allows the user to redo any modification that has been undone
    * to the image.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> redo = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent e) {   
            if (!redoStack.isEmpty()) {     //if the redoStack is NOT empty
                undoStack.push(saveUndoImage());            //Saves previous state to undoStack
                graphic.drawImage(redoStack.pop(), 0, 0);   //Draws previous image onto canvas
            }
        }
    };
    /**
    * This method is used to allow the user select any color of choice by using 
    * the ColorPicker Class.
    * 
    * @see ColorPicker
    * @author Nate
    */
    EventHandler<ActionEvent> colorswitch = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            graphic.setStroke(colorpicker.getValue());     //sets colorpicker to new value is changed
        }
    };
    /**
    * This method is used to allow the user modify line width using a slider function.
    * 
    * @see Slider
    * @author Nate
    */
    EventHandler<MouseEvent> setSlider = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            sliderValue = slider.getValue();        //reads value from slider and saves as new width
            String str = String.format("%.1f", sliderValue);
            label.setText(str);         //sets label of slider to choosen value
            graphic.setLineWidth(sliderValue);
        }
    };
    /**
    * Prompts user with the option to save/close/cancel whenever they try to exit the program,
    * The purpose is to help users remember to save their creation.
    * 
    * @author Nate
    * @see Alert
    */
    EventHandler<WindowEvent> smartClose = new EventHandler<WindowEvent>(){
        @Override
        public void handle(WindowEvent we){
            alert = new Alert(AlertType.CONFIRMATION);      //new alert is open when X is clicked
            //alert setup information
            alert.setTitle("Smart Close");
            alert.setHeaderText("Save/Cancel/Close");
            alert.setContentText("Save your masterpiece!");
            //creation of three buttons in smartClose dialog
            ButtonType one = new ButtonType("Save");
            ButtonType two = new ButtonType("Cancel");
            ButtonType three = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(one, two, three);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == one){       //if save is clicked
                viewMyImage.setImage(image);
                FileChooser fileChooser = new FileChooser();    //opens filechooser for saving
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");   //Set extension filters to jpg and png
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
                fileChooser.setTitle("Save Image");
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {
                    try {
                        WritableImage writableImage = new WritableImage((int)image.getWidth(),(int)image.getHeight());
                        imagePane.snapshot(null, writableImage);        //takes a snapshot of current state of canvas
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, "png", file);      //saves image to choosen valid file path
                    }catch(IOException ex) {
                        //BAD FILE PATH ERROR
                    }
                }
                alert.close();
            }else if (result.get() == two) { //if cancel is clicked
                alert.hide();       //clears alert
                we.consume();       //closes dialog box
            }
        }
    };
    /**
    * This method is used to allow the user clear the entire canvas 
    * to include only the original image.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> clearCanvas = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            graphic.clearRect(0, 0, 500, 500);      //wipes canvas of all drawn lines
            undoStack.clear();                   //clears undoStack
        }
    };
    /**
    * This method is used to allow the user to free hand draw using their mouse or track pad.
    * 
    * @see GraphicsContext
    * @author Nate
    */
    EventHandler<ActionEvent> freeDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMousePressed((MouseEvent freeDrawAction) -> {
                undoStack.push(saveUndoImage());        //saves image to undoStack
                graphic.beginPath();     
                graphic.setLineJoin(StrokeLineJoin.ROUND);  //makes pixels drawn round
                graphic.setLineCap(StrokeLineCap.ROUND);    //so lines have soft edges
                graphic.moveTo(freeDrawAction.getX(), freeDrawAction.getY());
                graphic.stroke();
            });
            canvas.setOnMouseDragged((MouseEvent freeDrawAction) -> {
                graphic.lineTo(freeDrawAction.getX(), freeDrawAction.getY());
                graphic.stroke();
            });
            canvas.setOnMouseReleased(null);
        }
    };
    /**
    * This method is used to allow the user to draw a straight line from point to point.
    * 
    * @see GraphicsContext
    * @author Nate
    */
    EventHandler<ActionEvent> snapDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMousePressed((MouseEvent snapAction) -> {
                undoStack.push(saveUndoImage());        //saves image to undoStack
                graphic.beginPath();
                x = snapAction.getSceneX();     //saves clicked X and Y values
                y = snapAction.getSceneY();
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased((MouseEvent snapAction) -> {
                //draws line from clicked X and Y values to current X and Y values
                graphic.strokeLine(x, y-100, snapAction.getSceneX(), snapAction.getSceneY()-100);
                graphic.stroke();
            });
        }
    };
    /**
    * This method is used to allow the user to draw a square from dragging their mouse or track pad.
    * 
    * @see GraphicsContext
    * @author Nate
    */
    EventHandler<ActionEvent> squareDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMousePressed((MouseEvent squareAction) -> {
                undoStack.push(saveUndoImage());        //saves image to undoStack
                graphic.beginPath();
                x = squareAction.getSceneX();       //saves current X/Y values
                y = squareAction.getSceneY();
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased((MouseEvent squareAction) -> {
                //draws lines on all 4 sides of the square using the saved X/Y values as well as the current X/Y values
                graphic.strokeLine(x, y-100, squareAction.getSceneX(), y-100);
                graphic.strokeLine(x, squareAction.getSceneY()-100, squareAction.getSceneX(), squareAction.getSceneY()-100);
                graphic.strokeLine(x, squareAction.getSceneY()-100, x, y-100);
                graphic.strokeLine(squareAction.getSceneX(), squareAction.getSceneY()-100, squareAction.getSceneX(), y-100);
                graphic.stroke();
            });
        }
    };
    /**
    * This method is used to allow the user to draw a circle using their 
    * mouse or track pad.
    * 
    * @see GraphicsContext
    * @author Nate
    */
    EventHandler<ActionEvent> circleDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            Circle circle = new Circle();   //creation of new circle to be drawn
            canvas.setOnMousePressed((MouseEvent circleAction) -> {
                undoStack.push(saveUndoImage());    //saves image to undoStack
                circle.setCenterX(circleAction.getX());     //sets current X/Y as center 
                circle.setCenterY(circleAction.getY());
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased((MouseEvent circleAction) -> {
                //on mouse released set radius depending on which direction the mouse was dragged
                if(Math.abs(circleAction.getY()- circle.getCenterY()) > Math.abs(circleAction.getX() - circle.getCenterX())){
                    circle.setRadius(Math.abs(circleAction.getY() - circle.getCenterY()));
                }else{
                    circle.setRadius(Math.abs(circleAction.getX() - circle.getCenterX()));
                }
                graphic.strokeOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            });
        }
    };
    /**
    * This method is used reset the on mouse events to NULL.
    * 
    * @author Nate
    */
    EventHandler<ActionEvent> clearDrawAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMousePressed(null); //clears mouse actions
            canvas.setOnMouseReleased(null);
            canvas.setOnMouseDragged(null);
        }
    };
    /**
    * This method is used to allow the user to erase
    * 
    * @see GraphicsContext
    * @author Nate
    */
    EventHandler<ActionEvent> eraseAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            canvas.setOnMouseDragged((MouseEvent eraseDrag) -> {
                graphic.setLineJoin(StrokeLineJoin.ROUND);  //sets eraser tip as round
                graphic.setLineCap(StrokeLineCap.ROUND);
                graphic.clearRect(eraseDrag.getX(), eraseDrag.getY(), slider.getValue(), slider.getValue());    //clears current pixels
                //graphic.lineTo(freeDrawAction.getX(), freeDrawAction.getY());
                
            });
            canvas.setOnMousePressed(null);
            canvas.setOnMouseReleased(null);
        }
    };
    /**
    * This method is used to allow the user to move the selected cut image, 
    * however, this can only be used after the selection method has been used.
    * 
    * @see GraphicsContext 
    * @see PixelReader 
    * @see Image
    * @author Nate
    */
    EventHandler<ActionEvent> moveAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent me) {
            undoStack.push(saveUndoImage());        //pushes current image state onto undoStack
            wi = new WritableImage((int)rect.getWidth(), (int)rect.getHeight());
            PixelWriter pix = wi.getPixelWriter();  //creation of a pixelWriter used in moveAction
            
            Image i = imagePane.snapshot(null ,null);       //takes snapShot of current state of canvas
            PixelReader pixread = i.getPixelReader();
            
            //filters through EVERY pixel
            for(int p = 1; p < (int)rect.getWidth() - 1 ; p++){
                for(int q = 1; q < (int)rect.getHeight() - 1; q++){
                    pix.setArgb(p, q, pixread.getArgb(((int)rect.getX())+p, ((int)rect.getY())+q)); 
                }
            }
            
            graphic.setFill(Color.WHITE);       //leaves a white space behind cut selection
            graphic.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            
            newCanvas = new Canvas(rect.getWidth(), rect.getHeight());     //newCanvas = the cut out image
            GraphicsContext newGraphic = newCanvas.getGraphicsContext2D();
            newGraphic.drawImage(wi, 0, 0);
            imagePane.getChildren().add(newCanvas);     //adds cut out image to the imagePane
            
            //mouse pressed action for moveAction
            canvas.setOnMousePressed((MouseEvent event) -> {
                newCanvas.setTranslateX(event.getSceneX() - (scene.getWidth() - canvas.getLayoutX())/2);
                newCanvas.setTranslateY(event.getSceneY() - (scene.getHeight() - canvas.getLayoutY())/2);
            });
            //mouse dragged action for moveAction
            canvas.setOnMouseDragged((MouseEvent event) -> {
                newCanvas.setTranslateX(event.getSceneX() - (scene.getWidth() - canvas.getLayoutX())/2);
                newCanvas.setTranslateY((event.getSceneY() - (scene.getHeight() - canvas.getLayoutY())/2) - 50);
            });
            //mouse released action for moveAction
            canvas.setOnMouseReleased((MouseEvent event) -> {
                graphic.drawImage(wi, event.getX() - (rect.getWidth()/2), event.getY()- (rect.getHeight()/2));
                imagePane.getChildren().remove(newCanvas);
                canvas.setOnMousePressed(null);
                canvas.setOnMouseReleased(null);
                canvas.setOnMouseDragged(null);
            });
            
        }
    };
    /**
    * This method is used to allow the user to select a piece of the 
    * image to be moved with the move action.
    * 
    * @see GraphicsContext
    * @see Rectangle
    * @author Nate
    */
    EventHandler<ActionEvent> selectionAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            canvas.setOnMousePressed((MouseEvent selectionPressed) -> {
                graphic.beginPath();
                undoStack.push(saveUndoImage());        //saves image onto undoStack
                graphic.setLineWidth(0.5);              //set up line
                graphic.setStroke(Color.BLACK);         //sets default stroke color to black
                rect.setX(selectionPressed.getX());     
                rect.setY(selectionPressed.getY());
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased((MouseEvent selectionReleased) -> {
                rect.setWidth(selectionReleased.getX() - rect.getX());      //sets width and height of rectangle
                rect.setHeight(selectionReleased.getY() - rect.getY());
                graphic.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()); //draws rectangle onto canvas
                graphic.closePath();
            });
        }
    };
    /**
    * This Class is used to include four inputs into the addTextAction() dialog
    * 
    * @author Nate
    */
    private static class Results {
        /**
         * Text that the user wants to add to the image.
         */
        String text;
        /**
         *  Font size to be used for text.
         */
        String size;
        /**
         * X value for start of text.
         */
        String xpix;
        /**
         * Y value for start of text.
         */
        String ypix;
        public Results(String text, String size, String xpix, String ypix) {
            this.text = text; 
            this.size = size; 
            this.xpix = xpix;
            this.ypix = ypix;
        }
    }
    /**
    * This method is used to allow the user to add text to the image by providing
    * the text, font size, x value, y value.
    * 
    * @see Dialog
    * @see GridPane
    * @author Nate
    */
    EventHandler<ActionEvent> addTextAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            Dialog<Results> textBox = new Dialog<>();   //setting up the popup box
            textBox.setTitle("Image Text");
            textBox.setHeaderText("Add text to your image!");
            GridPane grid = new GridPane();  //gridPane to be used inside the popup box
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            ButtonType doneButton = new ButtonType("Done", ButtonData.OK_DONE);     //allows user to click done/close
            textBox.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);
            TextField userText = new TextField();       //setting up the text fields
            userText.setPromptText("Text");
            TextField fontSize = new TextField();
            fontSize.setPromptText("Size");
            TextField xPixel = new TextField();
            xPixel.setPromptText("Pixel");
            TextField yPixel = new TextField();
            yPixel.setPromptText("Pixel");
            grid.add(new Label("Text: "), 0, 0);        //positioning the text fields
            grid.add(userText, 1, 0);
            grid.add(new Label("Font Size: "), 0, 1);
            grid.add(fontSize, 1, 1);
            grid.add(new Label("Pixel (X Value): "), 0, 2);
            grid.add(xPixel, 1, 2);
            grid.add(new Label("Pixel (Y Value): "), 0, 3);
            grid.add(yPixel, 1, 3);
            textBox.getDialogPane().setContent(grid);
            Optional<Results> result = textBox.showAndWait();      //shows dialog box and waits for user input
            undoStack.push(saveUndoImage());                       //adds changes to undoStack
            graphic.setStroke(colorpicker.getValue());
            graphic.setFont(new Font("Verdana", Integer.parseInt(fontSize.getText())));
            graphic.fillText(userText.getText(), Integer.parseInt(xPixel.getText()), Integer.parseInt(yPixel.getText()));
        }
    };
    /**
    * This method is used to allow the user to select any color from the image to be using in drawing.
    * 
    * @see Canvas
    * @see ColorPicker
    * @see PixelReader
    * @author Nate
    */
    EventHandler<ActionEvent> dropperAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            canvas.setOnMousePressed((MouseEvent dropAction) -> {
                PixelReader pr = image.getPixelReader();    //creates new pixelReader
                graphic.setStroke(pr.getColor((int)dropAction.getX(), (int)dropAction.getY())); //gets the current color from the colorpicker
                colorpicker.setValue(pr.getColor((int)dropAction.getX(), (int)dropAction.getY()));
            });
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased(null);
        }
    };
}