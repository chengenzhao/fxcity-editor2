package com.whitewoodcity.fxcityeditor2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EditorApp extends javafx.application.Application {
  @Override
  public void start(Stage stage) throws Exception {
    stage.setScene(new Scene(new Button("test")));
    stage.show();
  }
}
