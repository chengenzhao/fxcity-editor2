package com.whitewoodcity.control;

import com.whitewoodcity.node.NumberField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GlobalSettingStage extends Stage {

  public NumberField entityX = new NumberField(-(int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getWidth());
  public NumberField entityY = new NumberField(-(int) Screen.getPrimary().getBounds().getHeight(), (int) Screen.getPrimary().getBounds().getHeight());

  public NumberField lineX = new NumberField(-(int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getWidth());
  public NumberField lineY = new NumberField(-(int) Screen.getPrimary().getBounds().getHeight(), (int) Screen.getPrimary().getBounds().getHeight());

  public GlobalSettingStage() {
    var button = new Button("OK");

    var gridpane = new GridPane(20,20);
    gridpane.setPadding(new Insets(10));

    setScene(new Scene(gridpane));
    button.setOnAction(_-> this.close());

    gridpane.add(new Label("Entity X:"),0,0);
    gridpane.add(entityX,1,0);
    gridpane.add(new Label("Entity Y:"),0,1);
    gridpane.add(entityY,1,1);

    gridpane.add(new Label("Line X:"),0,2);
    gridpane.add(lineX,1,2);
    gridpane.add(new Label("Line Y:"),0,3);
    gridpane.add(lineY,1,3);

    gridpane.add(button,0,4,2,1);
  }
}
