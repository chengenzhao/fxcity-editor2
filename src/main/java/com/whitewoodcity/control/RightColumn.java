package com.whitewoodcity.control;

import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.fxgl.vectorview.JVG;
import com.whitewoodcity.node.EditableRectangle;
import com.whitewoodcity.node.NumberField;
import javafx.util.StringConverter;

public class RightColumn extends GridPane {

  private EditableRectangle rect = null;

  ChoiceBox<TreeItem<Node>> choiceBox = new ChoiceBox<>();
  CheckBox visible = new CheckBox();
  Button multiple = new Button("*");
  Button divsion = new Button("/");
  NumberField factor = new NumberField(1, 100);

  private final int unchangedRows = 3;

  public RightColumn() {
    this.setPadding(new Insets(10));
    this.setVgap(10);
    this.setHgap(10);
    this.add(new Label("Parent:"), 0, 0);
    this.add(choiceBox, 1, 0);
    this.add(new Label("Visible:"), 0, 1);
    this.add(visible, 1, 1);
    this.add(new HBox(multiple, divsion), 0, 2);
    this.add(factor, 1, 2);

    factor.setText("1.1");
    factor.setPrefWidth(100);
  }

  public void unbind() {

    choiceBox.setOnAction(null);
    choiceBox.getItems().clear();
    choiceBox.setValue(null);

    if (rect != null) {
      rect.mouseTransparentProperty().unbind();
      visible.selectedProperty().unbindBidirectional(rect.getNode().visibleProperty());
    }

    this.getChildren().remove(unchangedRows * 2, this.getChildren().size());

    multiple.setOnAction(null);
    divsion.setOnAction(null);
  }

  public void update() {
    unbind();

    rect = FXGL.<GameApp>getAppCast().getCurrentRect();
    if (rect == null) {
      choiceBox.setDisable(true);
      visible.setDisable(true);
      visible.setSelected(false);
      multiple.setDisable(true);
      divsion.setDisable(true);
      return;
    } else {
      choiceBox.setDisable(false);
      visible.setDisable(false);
      multiple.setDisable(false);
      divsion.setDisable(false);
    }

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
      update();
    });

    visible.selectedProperty().bindBidirectional(rect.getNode().visibleProperty());
    rect.mouseTransparentProperty().bind(visible.selectedProperty().map(s -> !s));

    multiple.setOnAction(_ -> multiply(factor.getDouble()));
    divsion.setOnAction(_ -> multiply(1.0 / factor.getDouble()));

    this.add(new Label("PivotX:"), 0, unchangedRows);
    var pivotX = new NumberField(Integer.MIN_VALUE, Integer.MAX_VALUE);
    pivotX.setPrefWidth(100);
    pivotX.valueProperty().bindBidirectional(rect.getRotation().pivotXProperty());
    pivotX.valueProperty().addListener((_, _, newV) -> {
      if (newV.doubleValue() > rect.getX() + rect.getWidth()) pivotX.valueProperty().set(rect.getX() + rect.getWidth());
      if (newV.doubleValue() < rect.getX()) pivotX.valueProperty().set(rect.getX());
    });
    this.add(pivotX, 1, unchangedRows);

    this.add(new Label("PivotY:"), 0, unchangedRows + 1);
    var pivotY = new NumberField(Integer.MIN_VALUE, Integer.MAX_VALUE);
    pivotY.prefWidthProperty().bind(pivotX.prefWidthProperty());
    pivotY.valueProperty().bindBidirectional(rect.getRotation().pivotYProperty());
    pivotY.valueProperty().addListener((_, _, newV) -> {
      if (newV.doubleValue() > rect.getY() + rect.getHeight())
        pivotY.valueProperty().set(rect.getY() + rect.getHeight());
      if (newV.doubleValue() < rect.getY()) pivotY.valueProperty().set(rect.getY());
    });
    this.add(pivotY, 1, unchangedRows + 1);

    this.add(new Label("Angle:"), 0, unchangedRows + 2);
    var angle = new NumberField(0, 720);
    angle.prefWidthProperty().bind(pivotX.prefWidthProperty());
    angle.valueProperty().bindBidirectional(rect.getRotation().angleProperty());
    angle.setOnAction(_ -> rect.update());
    this.add(angle, 1, unchangedRows + 2);

    for (int j = 1; j < rect.getRotates().size(); j++) {

      var rotate = rect.getRotates().get(j);
      int i = j - 1;

      this.add(new Separator(), 0, unchangedRows + 3 + 4 * i, 2, 1);

      this.add(new Label("PivotX:"), 0, unchangedRows + 4 + i * 4);
      var px = new TextField();
      px.prefWidthProperty().bind(pivotX.prefWidthProperty());
      px.textProperty().bind(rotate.pivotXProperty().map(p -> p.doubleValue() + ""));
      px.setEditable(false);
      this.add(px, 1, unchangedRows + 4 + i * 4);

      this.add(new Label("PivotY:"), 0, unchangedRows + 5 + i * 4);
      var py = new TextField();
      py.prefWidthProperty().bind(px.prefWidthProperty());
      py.textProperty().bind(rotate.pivotYProperty().map(p -> p.doubleValue() + ""));
      py.setEditable(false);
      this.add(py, 1, unchangedRows + 5 + i * 4);

      this.add(new Label("Angle:"), 0, unchangedRows + 6 + i * 4);
      var af = new TextField();
      af.prefWidthProperty().bind(px.prefWidthProperty());
      af.textProperty().bind(rotate.angleProperty().map(a -> a.doubleValue() + ""));
      af.setEditable(false);
      this.add(af, 1, unchangedRows + 6 + i * 4);
    }
  }

  private void removeTextureFromItems(ObservableList<TreeItem<Node>> items, EditableRectangle rect) {
    var map = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap();
    for (var child : rect.children()) {
      removeTextureFromItems(items, child);
    }
    var item = map.inverse().get(rect);
    items.remove(item);
  }

  private void multiply(double f) {
    var item = EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap().inverse().get(rect);
    for (var kf : EditorApp.getEditorApp().bottomPane.keyFrames) {
      var rect = kf.getRectBiMap().get(item);
      var node = rect.getNode();
      switch (node) {
        case JVG jvg -> {
          var xy = jvg.getXY();
          jvg.trim().zoom(f).move(xy);
          var d = jvg.getDimension();
          rect.setWidth(d.getWidth());
          rect.setHeight(d.getHeight());
        }
        case ImageView imageView -> {
          imageView.setFitWidth(imageView.getFitWidth() * f);
          imageView.setFitHeight(imageView.getFitHeight() * f);
          rect.setWidth(imageView.getFitWidth());
          rect.setHeight(imageView.getFitHeight());
        }
        default -> {
        }
      }
      var r = rect.getRotation();
      r.setPivotX((r.getPivotX() - rect.getX()) * f + rect.getX());
      r.setPivotY((r.getPivotY() - rect.getY()) * f + rect.getY());
      rect.update();
    }

  }
}
