package us.kosdt.arl.graphics_test;

import us.kosdt.arl.engine.Game;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.Button;
import us.kosdt.arl.graphics_test.gui_test.test_components.TestButton1;
import us.kosdt.arl.util.math.Vec2i;

public class GameTest {

    public static void main(String[] args) {
        Game.init();
        GuiManager.GUI_MANAGER.setComponent(new TestButton1(new Vec2i(5, 5), new Vec2i(4, 4), Button.UNPRESSED, "Pressed"));
        Game.run();
    }

}
