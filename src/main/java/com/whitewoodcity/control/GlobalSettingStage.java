package com.whitewoodcity.control;

import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.javafx.jvg.JVG;
import com.whitewoodcity.node.NumberField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GlobalSettingStage extends Stage {

  public NumberField entityX = new NumberField(-(int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getWidth());
  public NumberField entityY = new NumberField(-(int) Screen.getPrimary().getBounds().getHeight(), (int) Screen.getPrimary().getBounds().getHeight());

  public NumberField lineX = new NumberField(-(int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getWidth());
  public NumberField lineY = new NumberField(-(int) Screen.getPrimary().getBounds().getHeight(), (int) Screen.getPrimary().getBounds().getHeight());

  public NumberField translateX = new NumberField(-(int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getWidth());
  public NumberField translateY = new NumberField(-(int) Screen.getPrimary().getBounds().getHeight(), (int) Screen.getPrimary().getBounds().getHeight());

  public GlobalSettingStage() {
    var button = new Button("Close");
    var applyButton = new Button("Apply");

    var gridpane = new GridPane(20, 20);
    gridpane.setPadding(new Insets(10));

    setScene(new Scene(gridpane));
    button.setOnAction(_ -> this.close());

    gridpane.add(new Label("Entity X:"), 0, 0);
    gridpane.add(entityX, 1, 0);
    gridpane.add(new Label("Entity Y:"), 0, 1);
    gridpane.add(entityY, 1, 1);

    gridpane.add(new Label("Line X:"), 0, 2);
    gridpane.add(lineX, 1, 2);
    gridpane.add(new Label("Line Y:"), 0, 3);
    gridpane.add(lineY, 1, 3);

    gridpane.add(new Label("Global translate X:"), 0, 4);
    gridpane.add(translateX, 1, 4);
    gridpane.add(new Label("Global translate  Y:"), 0, 5);
    gridpane.add(translateY, 1, 5);
    gridpane.add(applyButton, 0, 6, 2, 1);
    applyButton.setOnAction(_ -> {
      EditorApp.getEditorApp().bottomPane.keyFrames.forEach(f -> {
        f.getRectBiMap().values().forEach(rect -> {
          switch (rect.getNode()) {
            case ImageView imageView -> {
              imageView.setX(imageView.getX() + translateX.getValue());
              imageView.setY(imageView.getY() + translateY.getValue());
            }
            case JVG jvg -> jvg.move(translateX.getValue(), translateY.getValue());
            default -> {}
          }
          rect.setX(rect.getX() + translateX.getValue());
          rect.setY(rect.getY() + translateY.getValue());
          var r = rect.getRotates().getFirst();
          r.setPivotX(r.getPivotX() + translateX.getValue());
          r.setPivotY(r.getPivotY() + translateY.getValue());
          rect.update();
        });
      });
    });

    var multiply = new Button("*");
    var divide = new Button("/");
    var hBox = new HBox(multiply, divide);
    gridpane.add(hBox, 0,7);
    var factor = new NumberField(1,2);
    factor.setText("1.1");
    gridpane.add(factor, 1,7);

    multiply.setOnAction(_-> zoom(factor.getDouble()));
    divide.setOnAction(_-> zoom(1.0/factor.getDouble()));

    gridpane.add(button, 0, 8, 2, 1);
  }

  private void zoom(double f){
    EditorApp.getEditorApp().bottomPane.keyFrames.forEach(keyFrame -> {
      keyFrame.getRectBiMap().values().forEach(rect -> {
        switch (rect.getNode()) {
          case ImageView imageView -> {
            imageView.setFitWidth(imageView.getFitWidth() * f);
            imageView.setFitHeight(imageView.getFitHeight() * f);
          }
          case JVG jvg -> jvg.zoom(f);
          default -> {}
        }
        rect.setX(rect.getX() * f);
        rect.setY(rect.getY() * f);
        rect.setWidth(rect.getWidth() * f);
        rect.setHeight(rect.getHeight() * f);
        var r = rect.getRotates().getFirst();
        r.setPivotX(r.getPivotX() * f);
        r.setPivotY(r.getPivotY() * f);
        rect.update();
      });
    });
  }
}
