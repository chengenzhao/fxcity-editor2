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
            case JVG jvg -> {
              jvg.move(translateX.getValue(), translateY.getValue());
              rect.setX(rect.getX() + translateX.getValue());
              rect.setY(rect.getY() + translateY.getValue());
              var r = rect.getRotates().getFirst();
              r.setPivotX(r.getPivotX() + translateX.getValue());
              r.setPivotY(r.getPivotY() + translateY.getValue());
              rect.update();
            }
            default -> {
            }
          }
        });
      });
    });

    gridpane.add(button, 0, 7, 2, 1);
  }
}
