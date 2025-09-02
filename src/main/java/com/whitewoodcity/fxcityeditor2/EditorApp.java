package com.whitewoodcity.fxcityeditor2;

import module javafx.controls;

public class EditorApp extends javafx.application.Application {
  @Override
  public void start(Stage stage) throws Exception {

    var menuBar = new MenuBar();
    var menu = new Menu("File");
    var save = new MenuItem("Save");
    var load = new MenuItem("Load");
    menu.getItems().addAll(save, load);
    menuBar.getMenus().add(menu);

    var gamePane = GameApp.embeddedLaunch(new GameApp());
    gamePane.setRenderFill(Color.TRANSPARENT);

    var vbox = new VBox();
    vbox.getChildren().addAll(menuBar, gamePane);

    stage.setScene(new Scene(vbox));
    stage.show();
  }
}
