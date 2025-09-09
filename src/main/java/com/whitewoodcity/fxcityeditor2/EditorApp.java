package com.whitewoodcity.fxcityeditor2;

import module javafx.controls;
import com.whitewoodcity.borders.MainMenu;

public class EditorApp extends javafx.application.Application {
  @Override
  public void start(Stage stage) throws Exception {

    var gamePane = GameApp.embeddedLaunch(new GameApp());
    gamePane.setRenderFill(Color.TRANSPARENT);

    var vbox = new VBox();
    vbox.getChildren().addAll(new MainMenu(), gamePane);

    stage.setScene(new Scene(vbox));
    stage.show();
  }
}
