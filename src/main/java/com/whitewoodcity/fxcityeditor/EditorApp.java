package com.whitewoodcity.fxcityeditor;

import module javafx.controls;
import com.whitewoodcity.control.*;

public class EditorApp extends Application {

  public MainMenu mainMenu = new MainMenu();
  public LeftColumn leftColumn = new LeftColumn();
  public BottomPane bottomPane = new BottomPane();
  public RightColumn rightColumn = new RightColumn();

  private static EditorApp editorApp;

  public EditorApp() {
    editorApp = this;
  }

  public static EditorApp getEditorApp() {
    return editorApp;
  }

  @Override
  public void start(Stage stage) {

    var gamePane = GameApp.embeddedLaunch(new GameApp());
    gamePane.setRenderFill(Color.TRANSPARENT);

    var vbox = new VBox();
    var border = new BorderPane();
    border.setCenter(gamePane);
    border.setRight(rightColumn);
    border.setLeft(leftColumn);
    border.setBottom(bottomPane);
    vbox.getChildren().addAll(mainMenu, border);

    stage.setScene(new Scene(vbox, Screen.getPrimary().getBounds().getWidth() * .75, Screen.getPrimary().getBounds().getHeight() * .75));

    gamePane.prefWidthProperty().bind(stage.getScene().widthProperty());
    gamePane.prefHeightProperty().bind(stage.getScene().heightProperty());
    gamePane.renderWidthProperty().bind(stage.getScene().widthProperty());
    gamePane.renderHeightProperty().bind(stage.getScene().heightProperty());

    stage.show();
  }
}
