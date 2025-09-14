package com.whitewoodcity.control;

import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.node.EditableRectangle;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class RightColumn extends GridPane {
  ChoiceBox<TreeItem<Node>> choiceBox = new ChoiceBox<>();

  public RightColumn() {
    this.setPadding(new Insets(10));
    this.setVgap(10);
    this.setHgap(10);
    this.add(choiceBox,0,0);
  }

  public void update(){
    choiceBox.setOnAction(null);
    choiceBox.getItems().clear();
    choiceBox.setValue(null);
    choiceBox.setDisable(true);
    var rect = FXGL.<GameApp>getAppCast().getCurrentRect();
    if(rect == null) return;
    choiceBox.setDisable(false);
    choiceBox.getItems().add(null);
    choiceBox.getItems().addAll(EditorApp.getEditorApp().leftColumn.getTreeItems());

    removeTextureFromItems(choiceBox.getItems(), rect);

    var map = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap().inverse();

    choiceBox.setValue(map.get(rect.parent()));

    choiceBox.setConverter(new StringConverter<>() {

      @Override
      public String toString(TreeItem<Node> item) {
        return item == null ? "" : EditorApp.getEditorApp().leftColumn.getText(item);
      }

      @Override
      public TreeItem<Node> fromString(String string) {
        return choiceBox.getItems().stream()
          .filter(item -> EditorApp.getEditorApp().leftColumn.getText(item).equals(string))
          .findFirst().orElse(null);
      }
    });

    choiceBox.setOnAction(_ -> {
      var child = map.get(rect);
      var parent = choiceBox.getValue();
      EditorApp.getEditorApp().bottomPane.setParent(child, parent);
    });
  }

  private void removeTextureFromItems(ObservableList<TreeItem<Node>> items, EditableRectangle rect) {
    var map = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap();
    for (var child : rect.children()) {
      removeTextureFromItems(items, child);
    }
    var item = map.inverse().get(rect);
    items.remove(item);
  }
}
