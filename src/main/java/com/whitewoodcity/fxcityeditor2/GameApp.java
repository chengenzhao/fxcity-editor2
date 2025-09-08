package com.whitewoodcity.fxcityeditor2;

import module com.almasb.fxgl.all;

public class GameApp extends GameApplication {

  public Entity entity;

  @Override
  protected void initSettings(GameSettings settings) {

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
}
