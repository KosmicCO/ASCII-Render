package us.kosdt.arl.encodings_test.text_editor_test;

import org.w3c.dom.Text;
import us.kosdt.arl.engine.Core;
import us.kosdt.arl.event.Message;
import us.kosdt.arl.event.messages.gui.CharSubmit;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.Button;
import us.kosdt.arl.graphics.gui.components.text_editors.DoubleBufferEditor;
import us.kosdt.arl.graphics.gui.components.text_editors.TextEditor;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics_test.gui_test.test_components.TestButton1;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec2i;

import java.awt.*;

public class DoubleBufferTest {

    public static void main(String[] args) {
        Core.init();
        GuiManager.GUI_MANAGER.setComponent(new PrintEditor(new DoubleBufferEditor()));
        Core.onStep(() -> {
            Render.startRender();
            GuiManager.GUI_MANAGER.render();
            Render.finishRender();
        });
        Core.run();
    }
}
