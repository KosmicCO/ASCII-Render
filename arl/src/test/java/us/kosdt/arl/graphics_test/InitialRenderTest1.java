package us.kosdt.arl.graphics_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;

public class InitialRenderTest1 {

    private static double offset = 0;

    public static void main(String[] args) {
        Core.init();
        Window.window().setResizable(true);
        Core.onStep(() -> {
            Render.startRender();
            offset += Core.dt() * 20;
            int c = (int) offset;
            for(int y = 0; y < Render.getRenderDim().y; y++) {
                for(int x = 0; x < Render.getRenderDim().x; x++) {
                    Render.drawTile(new RenderTile(c % 512, Color.WHITE, Color.BLACK, 1), x, y);
                    c++;
                }
            }

            Render.drawPermeableRect(new RenderTile(0, Color.WHITE, Color.gray(0.5).setA(.75)), 10, 20, 40, 40);
            Render.finishRender();
        });
        Core.run();
    }
}
