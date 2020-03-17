package us.kosdt.arl.graphics_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;

public class InitialRenderTest1 {

    public static void main(String[] args) {
        Core.init();
        Window.window().setResizable(true);
        Core.onStep(() -> {
            Render.startRender();
            int c = 0;
            for(int x = 0; x < Render.getRenderDim().x; x++) {
                for(int y = 0; y < Render.getRenderDim().y; y++) {
                    Render.setTile(new RenderTile(c % 512, Color.WHITE, Color.BLACK, c % 3), x, y);
                    c++;
                }
            }
            Render.finishRender();
        });
        Core.run();
    }
}
