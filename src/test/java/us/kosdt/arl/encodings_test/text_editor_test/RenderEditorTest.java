package us.kosdt.arl.encodings_test.text_editor_test;

import us.kosdt.arl.encodings_test.text_rederer_test.SingleLineRender;
import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.gui.components.text.EditorComponent;
import us.kosdt.arl.graphics.gui.components.text.text_editors.DoubleBufferEditor;
import us.kosdt.arl.graphics.gui.components.text.text_editors.TextEditor;
import us.kosdt.arl.graphics.gui.components.text.text_renderers.EditorRenderer;
import us.kosdt.arl.util.math.Vec2d;

public class RenderEditorTest {

    public static void main(String[] args) {
        Core.init();
        Core.setGuiControl();
        TextEditor te = new DoubleBufferEditor();
        EditorComponent ec = new RenderEditor(te, new SingleLineRender(te, 0, 20, new Vec2d(1, 1), true));
        GuiManager.GUI_MANAGER.setComponent(ec);
        Core.run();
    }

    public static class RenderEditor extends EditorComponent {

        public RenderEditor(TextEditor editor, EditorRenderer renderer) {
            super(editor, renderer);
        }

        @Override
        public boolean contains(Vec2d v) {
            return true;
        }
    }
}
