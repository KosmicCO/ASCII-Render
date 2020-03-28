package us.kosdt.arl.graphics_test.draw_modes_tests;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.tile_render.FontShader;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.graphics.tile_render.render_modes.WaterMode;
import us.kosdt.arl.graphics.tile_render.render_modes.WindMode;

import static java.lang.Math.PI;

public class WindModeTest {

    public static void main(String[] args) {
        Core.init();
        Window.window().setResizable(true);
        FontShader.setRenderModeUniforms(new WindMode(PI / 6, .0075, 1, 0.2, 1.2));
        RenderTile wheat = new RenderTile((int) '!', new Color(0.96, 0.87, 0.60).multRGB(1.1), (new Color(0.96, 0.87, 0.60)).multRGB(0.8), RenderTile.RFUNC_WIND_MODE);
        RenderTile grass = new RenderTile((int) '\'', Color.GREEN, Color.GREEN.multRGB(0.7), RenderTile.RFUNC_WIND_MODE);
        Core.onStep(() -> {
            Render.startRender();
            Render.fill(wheat);
            Render.drawRect(grass, 0, 0, Window.window().getTileWidth() / 2, Window.window().getTileHeight());
            Render.finishRender();
        });
        Core.run();
    }

}
