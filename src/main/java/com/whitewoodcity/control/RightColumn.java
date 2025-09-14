package com.whitewoodcity.control;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class RightColumn extends GridPane {
  public RightColumn() {
    this.setPadding(new Insets(10));
    this.setVgap(10);
    this.setHgap(10);
    this.add(new Button("test"),0,0);
  }
}
