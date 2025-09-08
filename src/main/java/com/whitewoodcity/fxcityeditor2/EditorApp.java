package com.whitewoodcity.fxcityeditor2;

import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxgl.vectorview.JVG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    load.setOnAction(_->{
      var fileChooser = new FileChooser();
      fileChooser.setTitle("What file would you like to load?");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("svg files", "*.jvg"));
      var window = stage.getScene().getWindow();
      var file = fileChooser.showOpenDialog(window);
      if(file!=null){
        try {
          var jsonString = Files.readString(Paths.get(file.getPath()));
          var vectorGraphics = new JVG(jsonString);
          IO.println(vectorGraphics.getXY());
          vectorGraphics.trim();
          IO.println(vectorGraphics.getChildren());
          FXGL.<GameApp>getAppCast().entity.getViewComponent().addChild(vectorGraphics);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    var vbox = new VBox();
    vbox.getChildren().addAll(menuBar, gamePane);

    stage.setScene(new Scene(vbox));
    stage.show();
  }
}
