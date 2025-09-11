package com.whitewoodcity.fxcityeditor;

import module com.almasb.fxgl.all;
import module com.whitewoodcity.fxcity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

public class GameApp extends GameApplication {

  int HEIGHT = 1000;
  int WIDTH = (int) (Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight() * 1000);

  public Entity entity;

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
    var rect = new Rectangle();
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

  public void selectRect(Rectangle rect){
//    rect.getStrokeDashArray().addAll(5d);
    rect.setStroke(Color.web("#039ED3"));

    rect.setOnMousePressed(e -> {
      switch (e.getButton()){
        case PRIMARY -> {
          var x = rect.getX();
          var y = rect.getY();
          var ex = e.getX();
          var ey = e.getY();

          rect.setOnMouseDragged(ee -> {
            var dx = ee.getX() - ex;
            var dy = ee.getY() - ey;
            rect.setX(x + dx);
            rect.setY(y + dy);
          });
        }
        case SECONDARY -> deSelectRect(rect);
      }
    });
  }

  public void deSelectRect(Rectangle rect){
    rect.setStroke(null);
    rect.setOnMouseDragged(null);
    rect.setOnMousePressed(_ -> selectRect(rect));
  }
}
