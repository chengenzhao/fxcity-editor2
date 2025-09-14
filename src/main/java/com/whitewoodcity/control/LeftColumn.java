package com.whitewoodcity.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LeftColumn extends VBox {

  private TreeView<Node> treeView = new TreeView<>();

  public LeftColumn(){
    this.getChildren().add(treeView);

    var root = new TreeItem<Node>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);

//    root.getChildren().add(generateNodeItem(root));
//    root.getChildren().add(generateNodeItem(root));
  }

  public void addNode(String name){
    var root = treeView.getRoot();
    var item = generateNodeItem(name, root);
    root.getChildren().add(item);
  }

  private TreeItem<Node> generateNodeItem(String name, TreeItem<Node> root){
    var hBox = new HBox();
    var treeItem = new TreeItem<Node>(hBox);

    var textField = new TextField(name);

    textField.focusedProperty().addListener((_,_,n)->{
      if(n){
        treeView.getSelectionModel().select(treeItem);
      }
    });

    //change the order of items
    var up = new Button("↑");
    var down = new Button("↓");
    var upNDown = new HBox(up, down);
    upNDown.setAlignment(Pos.BASELINE_LEFT);
    hBox.getChildren().addAll(upNDown, textField);

    up.setOnAction(_ -> {
      var i = root.getChildren().indexOf(treeItem);
      if (i > 0) {
        root.getChildren().add(i - 1, root.getChildren().remove(i));
      }
//      selectTreeItem(hBox);
//      fireEvent(keyFrames.get(currentKeyFrame));
    });

    down.setOnAction(_ -> {
      var i = root.getChildren().indexOf(treeItem);
      if (i < root.getChildren().size() - 1) {
        root.getChildren().add(i + 1, root.getChildren().remove(i));
      }
//      selectTreeItem(hBox);
//      fireEvent(keyFrames.get(currentKeyFrame));
    });

    return treeItem;
  }

}
