package us.kosdt.arl.graphics.gui.components.text.text_editors;

public interface TextEditor{

    int codeAt(int index);

    void moveCursor(int index);

    int getCursor();

    boolean takeCodeInput(int codepoint);

    boolean takeKeyInput(int key);

    int size();

    int earliestChange();

    boolean isChanged();

    void resetChanged();

    void deleteRange(int start, int end);

    boolean takeCodeInput(int codepoint, int[] highlight);

    boolean takeKeyInput(int codepoint, int[] highlight);
}
