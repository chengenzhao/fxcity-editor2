package com.whitewoodcity.control;

import com.whitewoodcity.node.KeyFrame;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class BottomPane extends Pane {
  public final List<KeyFrame> keyFrames = new ArrayList<>();
  public KeyFrame currentFrame;

  public BottomPane() {
    keyFrames.add(new KeyFrame(20, 50).setTime(new Duration(0)).setColor(Color.ORANGE));
    currentFrame = keyFrames.getFirst();

    keyFrames.forEach(e -> this.getChildren().add(e));
  }
}
