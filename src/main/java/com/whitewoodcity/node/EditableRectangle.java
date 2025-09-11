package com.whitewoodcity.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class EditableRectangle extends Rectangle {
  private final ObservableList<Rotate> rotates = FXCollections.observableArrayList();
  private final Rotate rotate = new Rotate();

  private EditableRectangle parent;
  private final ObservableList<EditableRectangle> children = FXCollections.observableArrayList();

  public ObservableList<Rotate> getRotates() {
    return rotates;
  }

  public Rotate getRotation() {
    return rotate;
  }

  public ObservableList<EditableRectangle> getChildren() {
    return children;
  }
}
