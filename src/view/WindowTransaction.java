package view;

import java.io.IOException;
import java.util.Optional;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.domain.Transaction;
import model.domain.Utility;

public class WindowTransaction {

	public enum Operation {
		ADD,
		EDIT;
	}

	//**********************************\
	//									|
	//	JavaFX Controls					|
	//									|
	//**********************************/

	@FXML private TextField name;
	@FXML private DatePicker date;
	@FXML private ComboBox<String> type;
	@FXML private TextField value;
	@FXML private ComboBox<String> income;
	@FXML private ComboBox<String> paid;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	private FXMLLoader loader;
	private Parent root;
	private Scene scene;
	private Stage stage;

	private Transaction t;
	private Operation operation;

	//**********************************\
	//									|
	//	Instance Fields					|
	//									|
	//**********************************/

	private static final String FXML = "./fxml/WindowTransaction.fxml";
	private static final String CSS = "./css/WindowTransaction.css";
	private static final String ICON = "./img/icons/calendar.png";

	//**********************************\
	//									|
	//	Construction					|
	//									|
	//**********************************/

	/**
	 * Open a WindowTransaction scene-graph to edit an existing Transaction for a month.
	 * 
	 * @param t
	 * @throws IllegalArgumentException if t is null
	 * @throws IOException
	 */
	public WindowTransaction(Transaction t) throws IllegalArgumentException, IOException {
		this(Utility.nullCheck(t), Operation.EDIT);
	}

	/**
	 * Open a WindowTransaction scene-graph, to create a new Transaction for month.
	 * 
	 * @throws IOException
	 */
	public WindowTransaction() throws IOException {
		this(null, Operation.ADD);
	}

	/**
	 * Consolidated private constructor
	 * 
	 * @param t
	 * @param op
	 * @throws IOException
	 */
	private WindowTransaction(Transaction t, Operation op) throws IOException {
		this.t = t;
		this.operation = op;
		setRoot();
		setScene();
		setStage();

		configJavaFXControls();
		
		Controller.setWindowTransaction(this);
	}

	private void setRoot() throws IOException {
		this.loader = new FXMLLoader(getClass().getResource(FXML));
		this.loader.setController(this);
		this.root = loader.load();
	}

	private void setScene() {
		this.scene = new Scene(this.root);
		this.scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());

	}

	private void setStage() {
		this.stage = new Stage();
		this.stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
		this.stage.setTitle((operation == Operation.ADD) ? "Add Transaction" : "Edit Transaction");
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("not yet implemented");
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setContentText("Are you sure you want to cancel " +
						((operation == Operation.ADD) ? "Adding" : "Editing") +
						" this Transaction?");
				alert.setHeaderText("You are about to abandon this transaction!");
				alert.setTitle("Please confirm cancellation.");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					//stage.close();
					Controller.getWindowMonth().show();
				}
			}
		});
		this.stage.initModality(Modality.APPLICATION_MODAL);

	}

	//**********************************\
	//									|
	//	JavaFX Application Methods		|
	//									|
	//**********************************/

	public void show() {
		this.stage.show();
	}
	
	private void configJavaFXControls() {
		name.setText((t == null) ? "" : t.getName());
		date.setValue((t == null) ? null : t.getDate());
	}
	
	
}
