package com.whitewoodcity.node;

import module javafx.controls;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.whitewoodcity.fxgl.texture.Texture;
import com.whitewoodcity.fxgl.vectorview.JVG;
import io.vertx.core.json.JsonObject;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class EditableRectangle extends Rectangle {

  private final static BiMap<EditableRectangle, Node> biMap = HashBiMap.create();

  public static EditableRectangle getRectByNode(Node node){
    return biMap.inverse().get(node);
  }

  private final ObservableList<Rotate> rotates = FXCollections.observableArrayList();
  private final Rotate rotate;

  private EditableRectangle parent;
  private final ObservableList<EditableRectangle> children = FXCollections.observableArrayList();

  private final Node node;

  public EditableRectangle(Node node, Rotate rotate) {
    this.rotate = rotate;
    this.node = node;

    addRotate(rotate);

    ChangeListener<Number> c = (_, _, _) -> {
      switch (node) {
        case JVG jvg -> jvg.set(getX(), getY());
        case ImageView imageView -> {
          imageView.setX(getX());
          imageView.setY(getY());
        }
        default -> {
        }
      }
    };
    xProperty().addListener(c);
    yProperty().addListener(c);

    biMap.put(this, node);
    this.setFill(Color.TRANSPARENT);
  }

  public EditableRectangle(Node node) {
    this(node,new Rotate(360));
  }

  public Node getNode() {
    return node;
  }

  public ObservableList<EditableRectangle> getChildren() {
    return children;
  }

  public List<Rotate> getRotates() {
    return rotates;
  }

  public void update() {
    List<Rotate> rotations = this.getTransforms().stream().map(Rotate.class::cast).toList();
    assert rotations.size() == rotates.size();

    for (var child : children) {
      child.update();
    }

    for (int i = 0; i < rotates.size(); i++) {
      var rotate = rotates.get(i);
      var point = new Point2D(rotate.getPivotX(), rotate.getPivotY());
      //find the current position of transformed coordinates
      for (int j = i - 1; j >= 0; j--) {
        try {
          point = rotates.get(j).inverseTransform(point);//critical action, current position of transformed coordinates is inverse transformed position
        } catch (NonInvertibleTransformException e) {
          throw new RuntimeException(e);
        }
      }
      updateRotate((Rotate)this.getTransforms().get(i), point, rotate.getAngle());
      updateRotate((Rotate)node.getTransforms().get(i), point, rotate.getAngle());
    }
  }

  private Rotate updateRotate(Rotate r, Point2D pivot, double angle){
    return this.updateRotate(r, pivot.getX(), pivot.getY(), angle);
  }
  private Rotate updateRotate(Rotate r, double pivotX, double pivotY, double angle){
    r.setPivotX(pivotX);
    r.setPivotY(pivotY);
    r.setAngle(angle);
    return r;
  }

  public void addRotate(Rotate rotate) {
    for (var child : children)
      child.addRotate(rotate);
    this.rotates.add(rotate);
    updateTransforms();
  }

  public void removeRotate(Rotate rotate) {
    for (var child : children)
      child.removeRotate(rotate);
    this.rotates.remove( rotate);
    updateTransforms();
  }

  private void updateTransforms() {
    this.getTransforms().clear();
    node.getTransforms().clear();
    for (var r : rotates) {
      this.getTransforms().add(r.clone());
      node.getTransforms().add(r.clone());
    }
  }

  public void addRotates(Rotate... rs) {
    for (var r : rs) addRotate(r);
  }

  public Rotate getRotation() {
    return rotate;
  }

  public Point2D transform(Point2D point) {
    for (var t : this.getTransforms()) {
      point = t.transform(point);
    }
    return point;
  }

  public Point2D inverseTransform(Point2D point) {
    for (int i = getTransforms().size() - 1; i >= 0; i--) {
      var t = getTransforms().get(i);
      try {
        point = t.inverseTransform(point);
      } catch (NonInvertibleTransformException e) {
        throw new RuntimeException(e);
      }
    }
    return point;
  }

  public void setParent(EditableRectangle parent) {
    //remove parent
    if (this.parent != null) {
      this.parent.children().remove(this);
      removeAncestorsRotations(this, this.parent.getRotation());
    }

    this.parent = parent;
    if (parent != null) {
      this.parent.children().add(this);
      var rs = this.parent.getRotates().toArray(new Rotate[0]);
      addAncestorsRotations(this, rs);
    }
    updateTransforms();
  }

  private void removeAncestorsRotations(EditableRectangle texture, Rotate rotate) {
    for (var child : texture.children)
      removeAncestorsRotations(child, rotate);
    var i = texture.rotates.indexOf(rotate);
    texture.rotates.subList(i, texture.rotates.size()).clear();
    texture.updateTransforms();
  }

  private void removeAncestorsRotations(EditableRectangle texture) {
    removeRotate(texture.getRotation());
    if (texture.parent != null)
      removeAncestorsRotations(texture.parent);
  }

  private void addAncestorsRotations(EditableRectangle texture, Rotate... rotates) {
    for (var child : texture.children)
      addAncestorsRotations(child, rotates);
    texture.rotates.addAll(rotates);
    texture.updateTransforms();
  }

  public EditableRectangle parent() {
    return this.parent;
  }

  public ObservableList<EditableRectangle> children() {
    return children;
  }

  @Override
  public EditableRectangle clone() {
    var n = switch (node){
      case JVG jvg -> jvg.copy();
      case Texture texture -> texture.copy();
      case ImageView view -> {
        var v = new ImageView(view.getImage());
        v.setFitWidth(view.getFitWidth());
        v.setFitHeight(view.getFitHeight());
        v.setX(view.getX());
        v.setY(view.getY());
        yield v;
      }
      default -> throw new RuntimeException("Unsupported Node type, only Texture, ImageView, JVG are supported");
    };
    var rect = new EditableRectangle(n, rotate.clone());
    rect.setTranslateX(this.getTranslateX());
    rect.setTranslateY(this.getTranslateY());
    rect.setTranslateZ(this.getTranslateZ());
    rect.setX(this.getX());
    rect.setY(this.getY());
    rect.setWidth(this.getWidth());
    rect.setHeight(this.getHeight());
    return rect;
  }

  public EditableRectangle cloneWithTransforms() {
    var rect = this.clone();
    rect.rotates.clear();
    rotates.forEach(r -> rect.addRotate(r.clone()));
    return rect;
  }

  public void show(String string) {
    var json = new JsonObject(string);

    this.setX(json.getDouble("x"));
    this.setY(json.getDouble("y"));

    if(json.containsKey("translateX")){
      this.setTranslateX(json.getDouble("translateX"));
    }
    if(json.containsKey("translateY")){
      this.setTranslateY(json.getDouble("translateY"));
    }

    var rs = json.getJsonArray("rotates");

    var r = rs.getJsonObject(0);
    var rotate = rotates.getFirst();
    rotate.setPivotX(r.getDouble("pivotX"));
    rotate.setPivotY(r.getDouble("pivotY"));
    rotate.setAngle(r.getDouble("angle"));

    //spread the rotation to the children and parent textures
    update();
  }

}
