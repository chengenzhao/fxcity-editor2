package com.whitewoodcity.fxcityeditor;

import module com.almasb.fxgl.all;
import module com.whitewoodcity.fxcity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.node.EditableRectangle;
import com.whitewoodcity.node.arrows.Arrow;

import java.util.HashMap;
import java.util.Map;

public class GameApp extends GameApplication {

  int HEIGHT = 1000;
  int WIDTH = (int) (Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight() * 1000);

  public Entity entity;

  private Map<Rectangle, Arrow> arrowMap = new HashMap<>();

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setHeight(HEIGHT);
    settings.setWidth(WIDTH);
  }

  @Override
  protected void initUI() {
    FXGL.getGameScene().setCursor(Cursor.DEFAULT);
  }

  @Override
  protected void initGame() {
    entity = new Entity();
    FXGL.getGameWorld().addEntities(entity);
    var redDot = new Circle(3);
    redDot.setFill(Color.RED);
    entity.getViewComponent().addDevChild(redDot);
    entity.setX(200);
    entity.setY(200);
  }

  public void addNode(Node node) {
    switch (node) {
      case JVG jvg -> jvg.trim();
      default -> {
      }
    }
    node.setMouseTransparent(true);
    entity.getViewComponent().addChild(node);
    var rect = new EditableRectangle();
    switch (node) {
      case JVG jvg -> {
        var d = jvg.getDimension();
        rect.setWidth(d.getWidth());
        rect.setHeight(d.getHeight());

        var xy = jvg.getXY();
        rect.setX(xy.getX());
        rect.setY(xy.getY());
      }
      case ImageView imageView-> {
        rect.setWidth(imageView.getFitWidth());
        rect.setHeight(imageView.getFitHeight());
        rect.setX(imageView.getX());
        rect.setY(imageView.getY());
      }
      default -> {
      }
    }

    rect.setFill(Color.TRANSPARENT);

    entity.getViewComponent().addDevChild(rect);

    ChangeListener<Number> c = (_, _, _) -> {
      switch (node) {
        case JVG jvg -> jvg.set(rect.getX(), rect.getY());
        case ImageView imageView -> {
          imageView.setX(rect.getX());
          imageView.setY(rect.getY());
        }
        default -> {
        }
      }
    };
    rect.xProperty().addListener(c);
    rect.yProperty().addListener(c);

    rect.setOnMousePressed(_ -> selectRect(rect));
  }

  public void selectRect(EditableRectangle rect){
//    rect.getStrokeDashArray().addAll(5d);
    rect.setStroke(Color.web("#039ED3"));

    var arrow = createRotateArrow(rect);
    entity.getViewComponent().addDevChild(arrow);
    arrowMap.put(rect, arrow);

    rect.setOnMousePressed(e -> {
      switch (e.getButton()){
        case PRIMARY -> {
          var x = rect.getX();
          var y = rect.getY();
          var ax = rect.getRotation().getPivotX();
          var ay = rect.getRotation().getPivotY();
          var ex = e.getX();
          var ey = e.getY();

          rect.setOnMouseDragged(ee -> {
            var dx = ee.getX() - ex;
            var dy = ee.getY() - ey;
            rect.setX(x + dx);
            rect.setY(y + dy);
            rect.getRotation().setPivotX(ax + dx);
            rect.getRotation().setPivotY(ay + dy);
          });
        }
        case SECONDARY -> deSelectRect(rect);
      }
    });
  }

  public void deSelectRect(EditableRectangle rect){
    rect.setStroke(null);
    rect.setOnMouseDragged(null);
    rect.setOnMousePressed(_ -> selectRect(rect));
    var arrow = arrowMap.get(rect);
    if(arrow!=null)
      entity.getViewComponent().removeDevChild(arrow);
  }

  private Arrow createRotateArrow(EditableRectangle rect) {
    var arrow = new Arrow(0, 0, 0, rect.getHeight());
    arrow.x1Property().bind(rect.getRotation().pivotXProperty());
    arrow.y1Property().bind(rect.getRotation().pivotYProperty());
    arrow.y2Property().bind(XBindings.reduceDoubleValue(arrow.y1Property(), rect.heightProperty(), (y, h) -> y + Math.max(70, h)));
    arrow.x2Property().bind(arrow.x1Property());
    rect.getTransforms().addListener((ListChangeListener<Transform>) _ -> {
      arrow.getTransforms().clear();
      arrow.getTransforms().addAll(rect.getTransforms());
    });
    return arrow;
  }
}
