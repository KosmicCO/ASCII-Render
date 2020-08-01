package us.kosdt.arl.encodings_test.text_editor_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.text.text_editors.DoubleBufferEditor;
import us.kosdt.arl.graphics.tile_render.Render;

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
