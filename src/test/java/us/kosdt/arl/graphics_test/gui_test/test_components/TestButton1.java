package us.kosdt.arl.graphics_test.gui_test.test_components;

import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.gui.components.Button;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec2i;

public class TestButton1 extends Button {

    private final Vec2i position;
    private final Vec2i size;
    private final String onPress;

    /**
     * Creates a new button with the given state.
     *
     * @param initialState The int id of the initial state of the button.
     */
    public TestButton1(Vec2i pos, Vec2i size, int initialState, String onPressMessage) {
        super(initialState);
        position = pos;
        this.size = size;
        onPress = onPressMessage;
    }

    @Override
    public void pressed() {
        System.out.println(onPress);
    }

    @Override
    public void unpressed() {
    }

    @Override
    public boolean contains(Vec2d v) {
        return size.toVec2d().containsExclusive(v.sub(position.toVec2d()));
    }

    @Override
    public void render() {
        Render.drawRectExclusive(new RenderTile(0, Color.WHITE, Color.BLUE.multRGB(getState() * 0.2 + 0.4)), position, size);
    }
}
