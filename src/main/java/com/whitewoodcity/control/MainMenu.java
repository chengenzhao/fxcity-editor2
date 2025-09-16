package com.whitewoodcity.control;

import module java.base;
import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.fxgl.vectorview.JVG;
import com.whitewoodcity.node.EditableRectangle;

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
          var app = EditorApp.getEditorApp();
          var jsonString = Files.readString(Paths.get(file.getPath()));
          var item = app.leftColumn.addNode(file.getName());
          app.bottomPane.keyFrames.forEach(f -> {
            f.getRectBiMap().put(item,createRect(new JVG(jsonString).trim()));
          });
          FXGL.<GameApp>getAppCast().update();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }


  public EditableRectangle createRect(Node node){
    var rect = new EditableRectangle(node);

    switch (node) {
      case JVG jvg -> {
        var d = jvg.getDimension();
        rect.setWidth(d.getWidth());
        rect.setHeight(d.getHeight());

        var xy = jvg.getXY();
        rect.setX(xy.getX());
        rect.setY(xy.getY());
      }
      case ImageView imageView-> {
        rect.setWidth(imageView.getFitWidth());
        rect.setHeight(imageView.getFitHeight());
        rect.setX(imageView.getX());
        rect.setY(imageView.getY());
      }
      default -> {
      }
    }

    return rect;
  };
}
