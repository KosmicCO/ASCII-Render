package us.kosdt.arl.engine;

import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.tile_render.Render;

public class Game {

    public static void init(){
        Core.init();
    }

    public static void run() {
        Core.onStep(() -> {
            Render.startRender();
            GuiManager.GUI_MANAGER.render();
            Render.finishRender();
        });
        Core.run();
    }
}
