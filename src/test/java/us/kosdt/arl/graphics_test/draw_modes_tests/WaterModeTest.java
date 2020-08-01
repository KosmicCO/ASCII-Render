package us.kosdt.arl.graphics_test.draw_modes_tests;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.tile_render.FontShader;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.graphics.tile_render.render_modes.WaterMode;

import static java.lang.Math.PI;

public class WaterModeTest {

    public static void main(String[] args) {
        Core.init();
        Window.window().setResizable(true);
        FontShader.setRenderModeUniforms(new WaterMode(PI / 6.0, .25, 1, 0.4, 1.4));
        Color ocean = new Color(0, .467, .745);
        RenderTile water = new RenderTile('~', ocean, ocean.multRGB(0.7), RenderTile.RFUNC_WATER_MODE);
        Core.onStep(() -> {
            Render.startRender();
            Render.fill(water);
            Render.finishRender();
        });
        Core.run();
    }
}
