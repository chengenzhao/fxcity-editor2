package com.whitewoodcity.control;

import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.whitewoodcity.control.MainMenu.DELETE_BUTTON_PREFIX;

public class LeftColumn extends VBox {

  private final TreeView<Node> treeView = new TreeView<>();

  public LeftColumn() {
    this.getChildren().add(treeView);

    var root = new TreeItem<Node>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);

    treeView.getSelectionModel().selectedItemProperty().addListener((_, old, newV) -> {
      var map = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap();
      if (old != null) {
        FXGL.<GameApp>getAppCast().deSelectRect(map.get(old));
      }
      if (newV != null) {
        FXGL.<GameApp>getAppCast().selectRect(map.get(newV));
      }
    });
  }

  public void select(TreeItem<Node> item) {
    treeView.getSelectionModel().select(item);
  }

  public ObservableList<TreeItem<Node>> getTreeItems() {
    return treeView.getRoot().getChildren();
  }

  public TreeItem<Node> addNode(String name) {
    var root = treeView.getRoot();
    var item = generateNodeItem(name, root);
    item.getValue().setOnMousePressed(_ -> {
      var map = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap();
      FXGL.<GameApp>getAppCast().selectRect(map.get(item));
    });
    root.getChildren().add(item);
    return item;
  }

  public String getText(TreeItem<Node> item) {
    return ((TextField) ((HBox) item.getValue()).getChildren().get(1)).getText();
  }

  private TreeItem<Node> generateNodeItem(String name, TreeItem<Node> root) {
    var hBox = new HBox();
    var treeItem = new TreeItem<Node>(hBox);

    var textField = new TextField(name);
    textField.setPrefWidth(100);

    textField.focusedProperty().addListener((_, _, n) -> {
      if (n) {
        treeView.getSelectionModel().select(treeItem);
      }
    });

    //change the order of items
    var up = new Button("↑");
    var down = new Button("↓");
    var upNDown = new HBox(up, down);
    upNDown.setAlignment(Pos.BASELINE_LEFT);
    var del = new Button("×");
    var visible = new CheckBox();
    visible.setSelected(true);
    hBox.getChildren().addAll(upNDown, textField, del, visible);
    hBox.setAlignment(Pos.BASELINE_LEFT);

    del.setOnAction(_ -> {
      if (treeView.getSelectionModel().getSelectedItem() == treeItem) {
        treeView.getSelectionModel().select(null);
      }

      treeView.getRoot().getChildren().remove(treeItem);
      var rect = EditorApp.getEditorApp().bottomPane.delete(treeItem);
      FXGL.<GameApp>getAppCast().delete(rect);

      FXGL.<GameApp>getAppCast().update();
    });
    del.setId(DELETE_BUTTON_PREFIX + Math.random());

    visible.selectedProperty().addListener((_,_,v)->
      EditorApp.getEditorApp().bottomPane.keyFrames.forEach(f -> f.getRectBiMap().get(treeItem).getNode().setVisible(v))
    );

    up.setOnAction(_ -> {
      var i = root.getChildren().indexOf(treeItem);
      if (i > 0) {
        root.getChildren().add(i - 1, root.getChildren().remove(i));
      }
      FXGL.<GameApp>getAppCast().update();
    });

    down.setOnAction(_ -> {
      var i = root.getChildren().indexOf(treeItem);
      if (i < root.getChildren().size() - 1) {
        root.getChildren().add(i + 1, root.getChildren().remove(i));
      }
      FXGL.<GameApp>getAppCast().update();
    });

    return treeItem;
  }

}
