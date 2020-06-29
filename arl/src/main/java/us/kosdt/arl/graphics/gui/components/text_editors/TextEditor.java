package us.kosdt.arl.graphics.gui.components.text_editors;

public interface TextEditor extends Iterable<Integer> {

    int codeAt(int index);

    void moveCursor(int index);

    int getCursor();

    boolean takeCodeInput(int codepoint);

    boolean takeKeyInput(int key);

    int length();
}
