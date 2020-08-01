package us.kosdt.arl.graphics_test.gui_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.Button;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics_test.gui_test.test_components.TestButton1;
import us.kosdt.arl.util.math.Vec2i;

public class GuiButtonTest1 {

    public static void main(String[] args) {
        Core.init();
        GuiManager.GUI_MANAGER.setComponent(new TestButton1(new Vec2i(5, 5), new Vec2i(4, 4), Button.UNPRESSED, "Pressed"));
        Core.onStep(() -> {
            Render.startRender();
            GuiManager.GUI_MANAGER.render();
            Render.finishRender();
        });
        Core.run();
    }
}
