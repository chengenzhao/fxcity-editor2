package com.whitewoodcity.control;

import module io.vertx.core;
import module java.base;
import module javafx.controls;
import com.almasb.fxgl.dsl.FXGL;
import com.whitewoodcity.fxcityeditor.EditorApp;
import com.whitewoodcity.fxcityeditor.GameApp;
import com.whitewoodcity.fxgl.transition.RotateJsonKeys;
import com.whitewoodcity.fxgl.vectorview.JVG;
import com.whitewoodcity.node.EditableRectangle;

public class MainMenu extends MenuBar {
  Menu menu = new Menu("File");
  MenuItem save = new MenuItem("Save");
  MenuItem load = new MenuItem("Load");
  MenuItem clear = new MenuItem("Clear");

  public static final String DELETE_BUTTON_PREFIX = "deleteButton";
  public static final String NAME = "name";
  public static final String JSON = "json";
  public static final String ITEMS = "items";
  public static final String KEY_FRAMES = "keyFrames";

  public MainMenu() {
    menu.getItems().addAll(save, load, clear);
    getMenus().add(menu);

    load.setOnAction(_ -> {
      var fileChooser = new FileChooser();
      fileChooser.setTitle("What file would you like to load?");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jvg files", "*.jvg", "*.ajvg"));
      var window = this.getScene().getWindow();
      var file = fileChooser.showOpenDialog(window);
      if (file != null) {
        try {
          switch (file.getName().substring(file.getName().indexOf(".") + 1)) {
            case "jvg" -> {
              var jsonString = Files.readString(Paths.get(file.getPath()));
              buildItem(file.getName(), jsonString, true);
            }
            case "ajvg" -> {
              clear();
              var jsonString = Files.readString(Paths.get(file.getPath()));
              var json = new JsonObject(jsonString);
              var items = json.getJsonArray(ITEMS);
              var frames = json.getJsonArray(KEY_FRAMES);
              for(var obj:items){
                if(obj instanceof JsonObject object){
                  buildItem(object.getString(NAME),object.getJsonArray(JSON).toString());
                }
              }
              frames.getJsonArray(0).stream().skip(1).filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast).forEach(e -> {
                  var time = e.getNumber("time").doubleValue();
                  EditorApp.getEditorApp().bottomPane.buildKeyFrame(time);
                });
              for(int i=0;i<frames.size();i++){
                var kfs = frames.getJsonArray(i);
                var item = EditorApp.getEditorApp().leftColumn.getTreeItems().get(i);
                for(int j=0;j<kfs.size();j++){
                  var frame = EditorApp.getEditorApp().bottomPane.keyFrames.get(j);
                  var rect = frame.getRectBiMap().get(item);
                  var jsonObject = kfs.getJsonObject(j);
                  rect.setX(jsonObject.getNumber(RotateJsonKeys.X.key()).doubleValue());
                  rect.setY(jsonObject.getNumber(RotateJsonKeys.Y.key()).doubleValue());
                  var r = jsonObject.getJsonArray(RotateJsonKeys.ROTATES.key()).getJsonObject(0);
                  rect.getRotation().setPivotX(r.getNumber(RotateJsonKeys.PIVOT_X.key()).doubleValue());
                  rect.getRotation().setPivotY(r.getNumber(RotateJsonKeys.PIVOT_Y.key()).doubleValue());
                  rect.getRotation().setAngle(r.getNumber(RotateJsonKeys.ANGLE.key()).intValue());
                  rect.update();
                }
              }

              EditorApp.getEditorApp().bottomPane.getChildren().stream().filter(Button.class::isInstance)
                .map(Button.class::cast).filter(b-> b.getId()!=null && b.getId().startsWith(DELETE_BUTTON_PREFIX)).toList()
                .getLast().fire();
            }
            default -> {
            }
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    clear.setOnAction(_ -> clear());

    save.setOnAction(_ -> {
      var fileChooser = new FileChooser();
      fileChooser.setTitle("What file would you like to save?");
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ajvg files", "*.ajvg"));
      fileChooser.setInitialFileName("config");
      var file = fileChooser.showSaveDialog(this.getScene().getWindow());
      if (file == null) return;
      try {
        var json = new JsonObject();
        json.put(ITEMS,buildItemJson());
        json.put(KEY_FRAMES, EditorApp.getEditorApp().bottomPane.buildTransitionJson());
        Files.write(Paths.get(file.getPath()), json.toString().getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void buildItem(String itemName, String jsonString) {
    buildItem(itemName, jsonString, false);
  }

  public void buildItem(String itemName, String jsonString, boolean trim) {
    var app = EditorApp.getEditorApp();
    var item = app.leftColumn.addNode(itemName);
    app.bottomPane.keyFrames.forEach(f -> {
      var jvg = new JVG(jsonString);
      if (trim) jvg.trim();
      f.getRectBiMap().put(item, createRect(jvg));
    });
    FXGL.<GameApp>getAppCast().update();
  }

  JsonArray buildItemJson() {
    var arrayNode = new JsonArray();

    for (var item : EditorApp.getEditorApp().leftColumn.getTreeItems()) {
      var node = EditorApp.getEditorApp().bottomPane.keyFrames.getFirst().getRectBiMap().get(item).getNode();
      switch (node) {
        case JVG jvg -> {
          var json = new JsonObject();
          json.put(JSON,new JsonArray(jvg.toJsonString()));
          json.put(NAME,EditorApp.getEditorApp().leftColumn.getText(item));
          arrayNode.add(json);
        }
        case ImageView view -> {
          //todo
        }
        default -> {
        }
      }
    }

    return arrayNode;
  }

  public void clear() {
    var editor = EditorApp.getEditorApp();
    var list = new ArrayList<Button>();

    var stream0 = editor.bottomPane.getChildren().stream();
    var stream1 = editor.leftColumn.getTreeItems().stream().map(TreeItem::getValue)
      .filter(HBox.class::isInstance).map(HBox.class::cast)
      .flatMap(e -> e.getChildren().stream());

    Stream.concat(stream0, stream1)
      .filter(Button.class::isInstance).map(Button.class::cast)
      .filter(b -> b.getId() != null & b.getId().startsWith(DELETE_BUTTON_PREFIX))
      .forEach(list::add);

    list.forEach(Button::fire);
  }

  public EditableRectangle createRect(Node node) {
    var rect = new EditableRectangle(node);

    switch (node) {
      case JVG jvg -> {
        var d = jvg.getDimension();
        rect.setWidth(d.getWidth());
        rect.setHeight(d.getHeight());

        var xy = jvg.getXY();
        rect.setX(xy.getX());
        rect.setY(xy.getY());
      }
      case ImageView imageView -> {
        rect.setWidth(imageView.getFitWidth());
        rect.setHeight(imageView.getFitHeight());
        rect.setX(imageView.getX());
        rect.setY(imageView.getY());
      }
      default -> {
      }
    }

    return rect;
  }

  ;
}
