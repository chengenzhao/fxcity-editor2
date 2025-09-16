package com.whitewoodcity.control;

import module java.base;
import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.fxgl.texture.TransitTexture;
import com.whitewoodcity.fxgl.transition.RotateJsonKeys;
import com.whitewoodcity.fxgl.vectorview.JVG;
import com.whitewoodcity.javafx.binding.XBindings;
import com.whitewoodcity.node.EditableRectangle;
import com.whitewoodcity.node.KeyFrame;
import com.whitewoodcity.node.NumberField;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class BottomPane extends Pane {
  public final List<KeyFrame> keyFrames = new ArrayList<>();
  public KeyFrame currentFrame;

  public NumberField totalTime = new NumberField(100);

  public BottomPane() {
    this.setPrefHeight(300);
    totalTime.setText("1");

    keyFrames.add(new KeyFrame(20, 50).setTime(new Duration(0)).setColor(Color.ORANGE));
    currentFrame = keyFrames.getFirst();

//    keyFrames.forEach(e -> this.getChildren().add(e));

    var hbox = new HBox(20);
    hbox.setAlignment(Pos.TOP_RIGHT);
    hbox.setPadding(new Insets(20));
    var objectButton = new Button("{ Frame Data }");
    var arrayButton = new Button("[ Transit Data ]");
    var loopButton = new Button("↻");
    var playButton = new Button("▶");
    var pauseButton = new Button("⏸");
    var stopButton = new Button("⏹");
    var addButton = new Button("+");

    hbox.layoutXProperty().bind(this.widthProperty().subtract(hbox.widthProperty()));
    hbox.getChildren().addAll(loopButton, playButton, pauseButton, stopButton, new Label("Total Time: "), totalTime, addButton, objectButton, arrayButton);

    var line = new Line();
    line.setStroke(Color.DARKCYAN);
    line.setStrokeWidth(10);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.startXProperty().bind(widthProperty().map(w -> w.doubleValue()/5));
    line.startYProperty().bind(hbox.layoutYProperty().add(hbox.heightProperty().multiply(2)));
    line.endXProperty().bind(widthProperty().map(w -> w.doubleValue()*4/5));
    line.endYProperty().bind(line.startYProperty());

    var anchor = new Line();
    anchor.setStrokeLineCap(StrokeLineCap.ROUND);
    anchor.setStrokeWidth(20);
    anchor.setStroke(Color.LIGHTBLUE);
    anchor.endXProperty().bind(anchor.startXProperty());
    anchor.startXProperty().bind(line.startXProperty());
    anchor.startYProperty().bind(line.startYProperty().subtract(25));
    anchor.endYProperty().bind(anchor.startYProperty().add(50));

    this.getChildren().addAll(hbox, line, anchor);

    bindKeyFrameTag(currentFrame, line, false);
    var timeField = buildTimeFieldForKeyFrame(currentFrame,false);
    getChildren().addAll(currentFrame, timeField);
//    select(currentFrame);
//    for (int i = 0; i < keyFrames.size(); i++) {
//      var kf = keyFrames.get(i);
//      bindKeyFrameTag(kf, line, i > 0);
//      var timeField = buildTimeFieldForKeyFrame(kf, i > 0);
//      getChildren().addAll(kf,timeField);//
//
//      if (i > 0) {
//        var delButton = buildDelButtonForKeyFrame(kf, timeField);
//        getChildren().add(delButton);
//      }
//    }

    addButton.setOnAction(_ -> {
      var kf = addKeyFrames(totalTime.getDouble() * 1000);

      bindKeyFrameTag(kf, line, true);
      var tf = buildTimeFieldForKeyFrame(kf,true);
      var delButton = buildDelButtonForKeyFrame(kf, tf);
      getChildren().addAll(kf,tf,delButton);

      select(kf);
    });

    objectButton.setOnAction(_->showFrameData());
  }

  public KeyFrame addKeyFrames(double timeInMillis) {
    var kf = generateKeyFrame(Duration.millis(timeInMillis));

    kf.copyFrom(keyFrames.getLast());
    keyFrames.add(kf);

    return kf;
  }

  KeyFrame generateKeyFrame(Duration duration) {
    return new KeyFrame(20, 50).setTime(duration).setColor(Color.ORANGE);//LIGHTSEAGREEN
  }

  private void select(KeyFrame keyFrame){
    keyFrames.forEach(KeyFrame::deSelect);
    keyFrame.select();
    currentFrame = keyFrame;
    FXGL.<GameApp>getAppCast().update();
  }

  private void bindKeyFrameTag(KeyFrame kf, Line line, boolean draggable) {
    var ox = kf.getX();

    kf.bindCenterX(XBindings.reduceDoubleValue(kf.timeProperty().map(Duration::toSeconds), totalTime.valueProperty().map(t -> Math.max(t.doubleValue(), 0.0001)),
      line.startXProperty(), line.endXProperty(),
      (keyFrameTime, totalTime, startX, endX) -> Math.min(startX + (endX - startX) * keyFrameTime / totalTime, endX)));
    kf.bindCenterY(line.startYProperty());

    kf.setOnMousePressed(_ -> select(kf));

    if (draggable) {
      kf.setOnMouseDragged(e -> {
        var cx = e.getX() - ox;
        var ex = ox + cx - line.getStartX();

        ex = Math.min(Math.max(0, ex), line.getEndX() - line.getStartX());

        kf.setTime(Duration.seconds(ex * totalTime.getDouble() / (line.getEndX() - line.getStartX())));
      });
    }
  }

  private Button buildDelButtonForKeyFrame(KeyFrame kf, TextField timeField) {
    var delButton = new Button("×");
    delButton.translateXProperty().bind(kf.xProperty());
    delButton.translateYProperty().bind(kf.yProperty().add(kf.heightProperty()).add(timeField.heightProperty()));
    var gameApp = FXGL.<GameApp>getAppCast();
    delButton.setOnAction(_ -> {
      keyFrames.remove(kf);
      getChildren().removeAll(kf, timeField, delButton);
      select(keyFrames.getLast());
    });
    return delButton;
  }

  public EditableRectangle delete(TreeItem<Node> item){
    var rect = currentFrame.getRectBiMap().get(item);

    if(rect.parent()!=null){
      EditorApp.getEditorApp().bottomPane.setParent(item,null);
    }
    if(!rect.children().isEmpty()){
      new ArrayList<>(rect.children()).forEach(child -> {
        var i = currentFrame.getRectBiMap().inverse().get(child);
        EditorApp.getEditorApp().bottomPane.setParent(i,null);
      });
    }

    keyFrames.forEach(f -> {
      var m = f.getRectBiMap();
      m.remove(item);
    });

    return rect;
  }

  public void setParent(TreeItem<Node> child, TreeItem<Node> parent){
    keyFrames.forEach(f -> {
      var m = f.getRectBiMap();
      var c = m.get(child);
      var p = m.get(parent);
      if(c!=null) c.setParent(p);
    });
  }

  private TextField buildTimeFieldForKeyFrame(KeyFrame kf, boolean editable) {
    var timeField = new NumberField(0, (int) totalTime.getDouble() + 1);
    timeField.translateXProperty().bind(kf.xProperty());
    timeField.translateYProperty().bind(kf.yProperty().add(kf.heightProperty()));
    timeField.setPrefWidth(kf.getWidth() * 2);
    timeField.textProperty().bind(kf.timeProperty().map(t -> t.toSeconds() + ""));
    if (editable) {
      Runnable onFocusAction = () -> {
        timeField.textProperty().unbind();
        timeField.setEditable(true);
        timeField.setMaxValue(totalTime.getDouble());
      };
      Runnable lostFocusAction = () -> {
        timeField.textProperty().unbind();
        kf.setTime(Duration.seconds(timeField.getDouble()));
        timeField.textProperty().bind(kf.timeProperty().map(t -> t.toSeconds() + ""));
        timeField.setEditable(false);
      };
      timeField.setOnMouseClicked(_ -> onFocusAction.run());
      timeField.setOnKeyPressed(e -> {
        if (e.getCode() == KeyCode.ENTER) {
          lostFocusAction.run();
        }
      });
      timeField.focusedProperty().addListener((_, _, newValue) -> {
        if (newValue)
          onFocusAction.run();
        else
          lostFocusAction.run();
      });
    } else {
      timeField.setDisable(true);
    }
    return timeField;
  }

  JsonObject extractJsonFromNode(Node node) {
    var json = new JsonObject();

    switch (node){
      case JVG jvg -> {
        var xy = jvg.getXY();
        json.put(RotateJsonKeys.X.key(), xy.getX());
        json.put(RotateJsonKeys.Y.key(), xy.getY());
      }
      case ImageView imageView -> {
        json.put(RotateJsonKeys.X.key(), imageView.getX());
        json.put(RotateJsonKeys.Y.key(), imageView.getY());
      }
      default -> {}
    }
    var rotates = new JsonArray();
    for (var rotateRaw : node.getTransforms()) {
      var rotate = (Rotate) rotateRaw;
      var rjson = new JsonObject();
      rjson.put(RotateJsonKeys.PIVOT_X.key(), rotate.getPivotX());
      rjson.put(RotateJsonKeys.PIVOT_Y.key(), rotate.getPivotY());
      rjson.put(RotateJsonKeys.ANGLE.key(), rotate.getAngle());
      rotates.add(rjson);
    }
    json.put(RotateJsonKeys.ROTATES.key(), rotates);
    return json;
  }

  private void showFrameData() {
    ButtonType okButtonType = ButtonType.OK;
    Dialog<ButtonType> dialog = new Dialog<>();

    var vbox = new VBox();
//    var kf = currentFrame;
    var map = currentFrame.getRectBiMap();
    vbox.setSpacing(5);

    for (var item : EditorApp.getEditorApp().leftColumn.getTreeItems()) {
      var rect = map.get(item);
      var json = extractJsonFromNode(rect.getNode());
      var textArea = new TextArea(json.toString());
      textArea.setWrapText(true);
      textArea.setEditable(false);
      textArea.setPrefHeight(100);
      var rotateNum = new TextField("" + json.getJsonArray(TransitTexture.JsonKeys.ROTATES.key()).size());
      rotateNum.setEditable(false);
      rotateNum.setPrefWidth(50);
      var hbox = new HBox(new Label("# of rotates in transforms:"), rotateNum);
      hbox.setSpacing(20);
      var s = new Separator();
      s.setPrefWidth(500);
      s.setOrientation(Orientation.HORIZONTAL);
      if(!vbox.getChildren().isEmpty())
        vbox.getChildren().add(s);
      vbox.getChildren().addAll( new Label(EditorApp.getEditorApp().leftColumn.getText(item)), hbox, textArea);
    }

    var scrollpane = new ScrollPane(vbox);
    dialog.getDialogPane().setContent(scrollpane);
    dialog.getDialogPane().getButtonTypes().add(okButtonType);
    dialog.getDialogPane().lookupButton(okButtonType);

    dialog.showAndWait();
  }
}
