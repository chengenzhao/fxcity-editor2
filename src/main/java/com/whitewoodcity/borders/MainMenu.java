package com.whitewoodcity.borders;

import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor2.GameApp;
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
          var jvg = new JVG(jsonString);
          jvg.trim();
          jvg.setMouseTransparent(true);
          FXGL.<GameApp>getAppCast().entity.getViewComponent().addChild(jvg);
          var d = jvg.getDimension();
          var rect = new Rectangle(d.getWidth(), d.getHeight());
          rect.getStrokeDashArray().addAll(5d);
          rect.setFill(Color.TRANSPARENT);
          rect.setStroke(Color.web("#039ED3"));
          var xy = jvg.getXY();
          rect.setX(xy.getX());
          rect.setY(xy.getY());
          FXGL.<GameApp>getAppCast().entity.getViewComponent().addChild(rect);

        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
