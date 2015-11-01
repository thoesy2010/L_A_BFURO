package poker.app.view;

import enums.eGame;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import poker.app.MainApp;
import pokerBase.Card;
import pokerBase.Deck;
import pokerBase.GamePlay;
import pokerBase.GamePlayPlayerHand;
import pokerBase.Hand;
import pokerBase.Player;
import pokerBase.Rule;

public class PokerTableController {

	boolean bP1Sit = false;

	// Reference to the main application.
	private MainApp mainApp;
	private GamePlay gme = null;
	private int iCardDrawn = 0;

	@FXML
	public AnchorPane APMainScreen;
	
	@FXML
	public HBox HboxCommonArea;

	@FXML
	public HBox HboxCommunityCards;

	@FXML
	public HBox h1P1;

	@FXML
	public TextField txtP1Name;

	@FXML
	public Label lblP1Name;

	@FXML
	public ToggleButton btnP1SitLeave;

	@FXML
	public Button btnDraw;

	@FXML
	public Button btnPlay;

	public PokerTableController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	@FXML
	private void handleP1SitLeave() {

		int iPlayerPosition = 1;

		if (bP1Sit == false) {
			Player p = new Player(txtP1Name.getText(), iPlayerPosition);
			mainApp.AddPlayerToTable(p);
			lblP1Name.setText(txtP1Name.getText());
			lblP1Name.setVisible(true);
			btnP1SitLeave.setText("Leave");
			txtP1Name.setVisible(false);
			bP1Sit = true;
		} else {
			mainApp.RemovePlayerFromTable(iPlayerPosition);
			btnP1SitLeave.setText("Sit");
			txtP1Name.setVisible(true);
			lblP1Name.setVisible(false);
			bP1Sit = false;
		}

	}

	@FXML
	private void handleDraw() {
		iCardDrawn++;

		// Draw a card for each player seated
		for (Player p : mainApp.GetSeatedPlayers()) {
			Card c = gme.getGameDeck().drawFromDeck();

			if (p.getiPlayerPosition() == 1) {
				GamePlayPlayerHand GPPH = gme.FindPlayerGame(gme, p);
				GPPH.addCardToHand(c);
				String strCard = "/res/img/" + c.getCardImg();
				ImageView img = new ImageView(new Image(getClass().getResourceAsStream(strCard), 75, 75, true, true));

				ImageView i = (ImageView) h1P1.getChildren().get(iCardDrawn - 1);
				ImageView iCardFaceDown = (ImageView) HboxCommonArea.getChildren().get(0);
				final ParallelTransition transitionForward = createTransition(i, iCardFaceDown,
						new Image(getClass().getResourceAsStream(strCard), 75, 75, true, true));

				transitionForward.play();
				// h1P1.getChildren().add(img);

				if (iCardDrawn == 5) {
					GPPH.getHand().EvalHand();
					System.out.println(GPPH.getHand().getHandStrength());
				}
			}
		}

		if (iCardDrawn == 5) {

			btnDraw.setVisible(false);
		}

	}

	@FXML
	private void handlePlay() {
		// Clear all players hands
		h1P1.getChildren().clear();

		// Get the Rule, start the Game
		Rule rle = new Rule(eGame.FiveStud);
		gme = new GamePlay(rle);

		// Add the seated players to the game
		for (Player p : mainApp.GetSeatedPlayers()) {
			gme.addPlayerToGame(p);
			GamePlayPlayerHand GPPH = new GamePlayPlayerHand();
			GPPH.setGame(gme);
			GPPH.setPlayer(p);
			GPPH.setHand(new Hand());
			gme.addGamePlayPlayerHand(GPPH);
		}

		// Add a deck to the game
		gme.setGameDeck(new Deck());

		btnDraw.setVisible(true);
		iCardDrawn = 0;

		String strCard = "/res/img/b1fv.png";

		for (int i = 0; i < gme.getNbrOfCards(); i++) {
			ImageView img = new ImageView(new Image(getClass().getResourceAsStream(strCard), 75, 75, true, true));

			h1P1.getChildren().add(img);
		}

		ImageView imgBottomCard = new ImageView(
				new Image(getClass().getResourceAsStream("/res/img/b2fh.png"), 75, 75, true, true));

		HboxCommonArea.getChildren().add(imgBottomCard);

	}

	private ParallelTransition createTransition(final ImageView iv, final ImageView ivStart, final Image img) {
		
		FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(.25), iv);
		fadeOutTransition.setFromValue(1.0);
		fadeOutTransition.setToValue(0.0);
		fadeOutTransition.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				iv.setImage(img);
				;
			}

		});

		FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(.25), iv);
		fadeInTransition.setFromValue(0.0);
		fadeInTransition.setToValue(1.0);

		/*
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), ivStart);

		
		translateTransition.setFromX(0);
		translateTransition.setToX(ivX - ivStartX1);
		translateTransition.setFromY(0);
		translateTransition.setToY(ivY - ivStartY1);
		
		translateTransition.setCycleCount(2);
		translateTransition.setAutoReverse(false);
*/
//		RotateTransition rotateTransition = new RotateTransition(Duration.millis(150), ivStart);
//		rotateTransition.setByAngle(90f);
//		rotateTransition.setCycleCount(1);
//		rotateTransition.setAutoReverse(false);
		
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(fadeOutTransition, fadeInTransition);

		return parallelTransition;
	}
}
