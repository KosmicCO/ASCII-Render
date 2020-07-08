package us.kosdt.arl.graphics.gui.components.text.text_renderers;

import us.kosdt.arl.util.math.Vec2d;

public interface TextRenderer {

    boolean takePress(boolean pressed, Vec2d mouse);

    boolean takeMouse(Vec2d mouse);

    void render();

    int[] getHighlighted();

    void clearHighlighted();

    void setCursor(int index);

    void setHighlight(int startIndex, int endIndex);
}
