package com.whitewoodcity.control;

import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.fxgl.vectorview.JVG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainMenu extends MenuBar {
  Menu menu = new Menu("File");
  MenuItem save = new MenuItem("Save");
  MenuItem load = new MenuItem("Load");

  public MainMenu() {
    menu.getItems().addAll(save, load);
    getMenus().add(menu);

    load.setOnAction(_ -> {
      var fileChooser = new FileChooser();
      fileChooser.setTitle("What file would you like to load?");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("svg files", "*.jvg"));
      var window = this.getScene().getWindow();
      var file = fileChooser.showOpenDialog(window);
      if (file != null) {
        try {
          var jsonString = Files.readString(Paths.get(file.getPath()));
          FXGL.<GameApp>getAppCast().addNode(file.getName(),new JVG(jsonString).trim());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
