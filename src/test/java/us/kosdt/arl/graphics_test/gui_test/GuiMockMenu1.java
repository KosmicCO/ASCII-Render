package us.kosdt.arl.graphics_test.gui_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.gui.Container;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.Button;
import us.kosdt.arl.graphics.tile_render.FontShader;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.graphics.tile_render.render_modes.WaterMode;
import us.kosdt.arl.graphics.tile_render.render_modes.WindMode;
import us.kosdt.arl.graphics_test.gui_test.test_components.TestButton1;
import us.kosdt.arl.util.math.Vec2i;

import static java.lang.Math.PI;

public class GuiMockMenu1 {

    public static void main(String[] args) {
        Core.init();

        FontShader.setRenderModeUniforms(new WindMode(PI / 6, .0075, 1, 0.2, 1.2));
        RenderTile wheat = new RenderTile('!', new Color(0.96, 0.87, 0.60).multRGB(1.1), (new Color(0.96, 0.87, 0.60)).multRGB(0.8), RenderTile.RFUNC_WIND_MODE);
        RenderTile grass = new RenderTile('\'', Color.GREEN, Color.GREEN.multRGB(0.7), RenderTile.RFUNC_WIND_MODE);

        FontShader.setRenderModeUniforms(new WaterMode(PI / 6.0, .25, 1, 0.4, 1.4));
        Color ocean = new Color(0, .467, .745);
        RenderTile water = new RenderTile('~', ocean, ocean.multRGB(0.7), RenderTile.RFUNC_WATER_MODE);

        GuiManager.GUI_MANAGER.setComponent(new Container()
                .addTop(new TestButton1(new Vec2i(1, 1), new Vec2i(10, 1), Button.UNPRESSED, "Button 1"))
                .addTop(new TestButton1(new Vec2i(1, 3), new Vec2i(10, 1), Button.UNPRESSED, "Button 2"))
                .addTop(new TestButton1(new Vec2i(1, 5), new Vec2i(10, 1), Button.UNPRESSED, "Button 3")));
        Core.onStep(() -> {
            Render.startRender();
            Render.drawRect(new RenderTile(0, Color.WHITE, Color.BLACK), 0, 0, 12, Window.window().getTileHeight());
            Render.drawRect(wheat, 12, 0, 32, Window.window().getTileHeight());
            Render.drawRect(grass, 32, 0, 64, Window.window().getTileHeight());
            Render.drawRect(water, 64, 0, Window.window().getTileWidth(), Window.window().getTileHeight());
            GuiManager.GUI_MANAGER.render();
            Render.finishRender();
        });

        Core.run();
    }
}
