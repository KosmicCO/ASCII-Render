package us.kosdt.arl.graphics.gui.components.text.text_renderers;

import us.kosdt.arl.graphics.gui.components.text.text_editors.TextEditor;

public interface EditorRenderer extends TextRenderer {

    void setEditor(TextEditor editor);

    void updateFromEditor();

    int getViewStart();

    int getViewSize();

    void setViewStart(int index);

    void setViewSize(int size);

    void directionInput(Direction dir);

    enum Direction {
        BACK_LINE,
        FORWARD_LINE,
        BACK,
        FORWARD
    }
}
