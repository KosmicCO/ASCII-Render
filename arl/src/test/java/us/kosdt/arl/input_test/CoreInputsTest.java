package us.kosdt.arl.input_test;

import org.lwjgl.glfw.GLFW;
import us.kosdt.arl.engine.Core;
import us.kosdt.arl.engine.Input;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.util.math.Vec2i;

public class CoreInputsTest {

    public static void main(String[] args) {
        Core.init();
        Window.window().setResizable(true);
        RenderTile mouseTile = new RenderTile('a', Color.RED, Color.RED, RenderTile.RFUNC_NONE);
        RenderTile mouseTileBright = new RenderTile('a', Color.gray(0.4).setR(1), Color.gray(0.4).setR(1), RenderTile.RFUNC_NONE);
        Core.onStep(() -> {
            Render.startRender();
            Vec2i m = Window.window().toClampedDiscreteWindowScale(Input.mouse());
            Render.drawTile(Input.mouseDown(GLFW.GLFW_MOUSE_BUTTON_1) ? mouseTileBright : mouseTile, m.x, m.y);
            Render.finishRender();
        });
        Core.run();
    }
}
