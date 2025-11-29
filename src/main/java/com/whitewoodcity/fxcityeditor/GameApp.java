package com.whitewoodcity.fxcityeditor;

import module com.almasb.fxgl.all;
import module com.whitewoodcity.fxcity;
import module java.base;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.node.EditableRectangle;
import com.whitewoodcity.node.arrows.Arrow;

public class GameApp extends GameApplication {

  int HEIGHT = 1000;
  int WIDTH = (int) (Screen.getPrimary().getBounds().getWidth() / Screen.getPrimary().getBounds().getHeight() * 1000);

  public Entity entity;

  private final Map<EditableRectangle, Arrow> arrowMap = new HashMap<>();
  private EditableRectangle currentRect = null;

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

  public void clear(){
    for(var v:entity.getViewComponent().getChildren()){
      var rect = EditableRectangle.getRectByNode(v);
      if(rect==null) continue;
      deSelectRect(rect);
      entity.getViewComponent().removeDevChild(rect);
    }
    entity.getViewComponent().clearChildren();
  }

  public void update(){
    clear();
    var frame = EditorApp.getEditorApp().bottomPane.currentFrame;
    for(var item:EditorApp.getEditorApp().leftColumn.getTreeItems().reversed()){
      var rect = frame.getRectBiMap().get(item);
      entity.getViewComponent().addChild(rect.getNode());
      entity.getViewComponent().addDevChild(rect);
      rect.setOnMousePressed(_ -> selectRect(rect));
    }
    EditorApp.getEditorApp().rightColumn.update();
  }

  public EditableRectangle getCurrentRect() {
    return currentRect;
  }

  public void selectSubRect(EditableRectangle rect){
    rect.getStrokeDashArray().addAll(5d);
    rect.setStroke(Color.web("#039ED3"));
    for(var child:rect.getChildren())
      selectSubRect(child);
  }

  public void selectRect(EditableRectangle rect){
    deSelectRect();
    currentRect = rect;

    EditorApp.getEditorApp().leftColumn.select(EditorApp.getEditorApp().bottomPane.currentFrame.getRectBiMap().inverse().get(rect));

    rect.getStrokeDashArray().clear();
    rect.setStroke(Color.web("#039ED3"));
    for(var child:rect.getChildren())
      selectSubRect(child);

    var arrow = arrowMap.computeIfAbsent(rect, this::createRotateArrow);
    entity.getViewComponent().removeDevChild(arrow);
    entity.getViewComponent().addDevChild(arrow);

    rect.setOnMousePressed(e -> {
      switch (e.getButton()){
        case PRIMARY -> {
          var op = rect.clone().transform(new Point2D(e.getX(), e.getY()));//new Point2D(e.getX(), e.getY());
          var x = op.getX();
          var y = op.getY();
          var ax = rect.getRotation().getPivotX();
          var ay = rect.getRotation().getPivotY();
          var rx = rect.getX();
          var ry = rect.getY();

          rect.setOnMouseDragged(ee -> {
            var p = rect.clone().transform(new Point2D(ee.getX(), ee.getY()));//new Point2D(ee.getX(), ee.getY());
            var dx = p.getX() - x;
            var dy = p.getY() - y;
            rect.setX(rx + dx);
            rect.setY(ry + dy);
            rect.getRotation().setPivotX(ax + dx);
            rect.getRotation().setPivotY(ay + dy);

            rect.update();
          });
        }
        case SECONDARY -> deSelectRect(rect);
      }
    });

    var pivotX = rect.getRotation().getPivotX();
    var pivotY = rect.getRotation().getPivotY();
    if (pivotX < rect.getX()) pivotX = rect.getX();
    if (pivotX > rect.getX() + rect.getWidth()) pivotX = rect.getX() + rect.getWidth();
    if (pivotY < rect.getY()) pivotY = rect.getY();
    if (pivotY > rect.getY() + rect.getHeight()) pivotY = rect.getY() + rect.getHeight();
    rect.getRotation().setPivotX(pivotX);
    rect.getRotation().setPivotY(pivotY);

    arrow.getOrigin().setOnMousePressed(oe -> {
      var op = rect.clone().transform(new Point2D(oe.getX(), oe.getY()));//oe;
      var ox = op.getX();
      var oy = op.getY();
      var tx = arrow.getX1();
      var ty = arrow.getY1();
      arrow.getOrigin().setOnMouseDragged(e -> {
        var p = rect.clone().transform(new Point2D(e.getX(), e.getY()));//e;
        double dx = p.getX() - ox;
        double dy = p.getY() - oy;
        var x1 = tx + dx;
        var y1 = ty + dy;
        if (x1 < rect.getX()) x1 = rect.getX();
        if (x1 > rect.getX() + rect.getWidth()) x1 = rect.getX() + rect.getWidth();
        if (y1 < rect.getY()) y1 = rect.getY();
        if (y1 > rect.getY() + rect.getHeight()) y1 = rect.getY() + rect.getHeight();
        rect.getRotation().setPivotX(x1);
        rect.getRotation().setPivotY(y1);
        update(rect, arrow);
      });
    });

    arrow.getHeadB().setOnMousePressed(oe -> {
      var ox = oe.getX();
      arrow.getHeadB().setOnMouseDragged(e -> {
        double changeInX = e.getX() - ox;
        if (changeRectangleAngle(rect, changeInX))
          update(rect, arrow);
      });
    });

    arrow.getMainLine().setOnMousePressed(oe -> {
      var ox = oe.getX();
      arrow.getMainLine().setOnMouseDragged(e -> {
        double changeInX = e.getX() - ox;
        if (changeRectangleAngle(rect, changeInX))
          update(rect,  arrow);
      });
    });

    update(rect, arrow);

    EditorApp.getEditorApp().rightColumn.update();
  }

  public void deSelectSubRect(EditableRectangle rect){
    rect.setStroke(null);
    for(var child:rect.getChildren())
      deSelectSubRect(child);
  }

  public void deSelectRect(){
    if(currentRect!=null)
      deSelectRect(currentRect);
    currentRect = null;
  }

  public void deSelectRect(EditableRectangle rect){
    currentRect = null;
    if(rect == null) return;
    rect.setStroke(null);
    rect.setOnMouseDragged(null);
    rect.setOnMousePressed(_ -> selectRect(rect));
    var arrow = arrowMap.get(rect);
    if(arrow!=null){
      entity.getViewComponent().removeDevChild(arrow);

      arrow.getOrigin().setOnMousePressed(null);
      arrow.getOrigin().setOnMouseDragged(null);
      arrow.getHeadB().setOnMousePressed(null);
      arrow.getHeadB().setOnMouseDragged(null);
      arrow.getMainLine().setOnMousePressed(null);
      arrow.getMainLine().setOnMouseDragged(null);
    }

    for(var child:rect.getChildren())
      deSelectSubRect(child);

    EditorApp.getEditorApp().rightColumn.update();
  }

  private Arrow createRotateArrow(EditableRectangle rect) {
    var arrow = new Arrow(0, 0, 0, rect.getHeight());
    arrow.x1Property().bind(rect.getRotation().pivotXProperty());
    arrow.y1Property().bind(rect.getRotation().pivotYProperty());
    arrow.y2Property().bind(XBindings.reduceDoubleValue(arrow.y1Property(), rect.heightProperty(), (y, h) -> y + Math.max(70, h)));
    arrow.x2Property().bind(arrow.x1Property());
    rect.getTransforms().forEach(e -> arrow.getTransforms().add(e.clone()));
    return arrow;
  }

  private boolean changeRectangleAngle(EditableRectangle rect, double changeInX) {
    var angle = rect.getRotation().getAngle();
    if (changeInX > 0) rect.getRotation().setAngle(angle - 1);
    if (changeInX < 0) rect.getRotation().setAngle(angle + 1);

    angle = rect.getRotation().getAngle();
    if(angle < 0) angle += 360;
    if(angle > 720) angle -= 360;

    rect.getRotation().setAngle(angle);

    return changeInX != 0;
  }

  private void update(EditableRectangle rect, Node... nodes) {
    if( rect.parent()!=null)
      update(rect.parent());
    else rect.update();
    for (var node : nodes) {
      node.getTransforms().clear();
      node.getTransforms().addAll(rect.getTransforms());
    }
  }

  public void delete(EditableRectangle rect){
    entity.getViewComponent().removeDevChild(rect);
    entity.getViewComponent().removeChild(rect.getNode());
  }
}
