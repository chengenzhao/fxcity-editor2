package com.whitewoodcity.node;

import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.whitewoodcity.fxcityeditor.GameApp;
import javafx.scene.shape.Rectangle;

public class KeyFrame extends Rectangle {
  private final double width, height;

  public KeyFrame(double width, double height) {
    super(width, height);
    this.width = width;
    this.height = height;
  }

  private final BiMap<TreeItem<Node>, EditableRectangle> rectBiMap = HashBiMap.create();
  private final ObjectProperty<Duration> time = new SimpleObjectProperty<>();

  public Duration getTime() {
    return time.get();
  }

  public double getTimeInSeconds(){
    return getTime().toSeconds();
  }

  public ObjectProperty<Duration> timeProperty() {
    return time;
  }

  public KeyFrame setTime(Duration time) {
    this.time.set(time);
    return this;
  }

  public KeyFrame setColor(Paint fill){
    super.setFill(fill);
    return this;
  }

  public KeyFrame setCenterX(double x){
    super.setX(x - width/2);
    return this;
  }

  public KeyFrame setCenterY(double y){
    super.setY(y - height/2);
    return this;
  }

  public KeyFrame bindCenterX(ObservableValue<Number> x){
    super.xProperty().bind(x.map(Number::doubleValue).map(v -> v - width/2));
    return this;
  }

  public double getCenterX(){
    return getX() + width/2;
  }

  public KeyFrame bindCenterY(ObservableValue<Number> y){
    super.yProperty().bind(y.map(Number::doubleValue).map(v -> v - height/2));
    return this;
  }

  public void select(){
    this.setStroke(Color.web("#039ED3"));
  }
  public void deSelect(){
    this.setStroke(null);
  }

  public BiMap<TreeItem<Node>, EditableRectangle> getRectBiMap() {
    return rectBiMap;
  }

  public void copyFrom(KeyFrame keyFrame){
    var keySet = keyFrame.rectBiMap.keySet();
    var gameApp = FXGL.<GameApp>getAppCast();
    for(var hBox:keySet){
      var texture = keyFrame.rectBiMap.get(hBox).clone();
      this.rectBiMap.put(hBox, texture);

//      texture.setOnMouseClicked(_ -> selectTreeItem(hBox));
//      texture.children().addListener((ListChangeListener<RotateTransit2DTexture>) _ -> selectTreeItem(hBox));

//      gameApp.rectMaps.get(hBox).put(this, createSelectionRectangle(texture));
//      gameApp.arrowMaps.get(hBox).put(this, createRotateArrow(texture));
    }

    for(var texture:keyFrame.rectBiMap.values()){
      if(texture.parent()!=null){
        var child = rectBiMap.get(keyFrame.rectBiMap.inverse().get(texture));
        var parent = rectBiMap.get(keyFrame.rectBiMap.inverse().get(texture.parent()));
        child.setParent(parent);
      }
    }

    for(var texture: rectBiMap.values())
      texture.update();
  }


}
