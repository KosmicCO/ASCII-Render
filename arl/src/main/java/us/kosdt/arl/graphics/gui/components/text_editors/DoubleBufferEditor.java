package us.kosdt.arl.graphics.gui.components.text_editors;

import us.kosdt.arl.graphics.Window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static org.lwjgl.glfw.GLFW.*;

public class DoubleBufferEditor implements TextEditor {

    public static final Predicate<Integer> DEFAULT_VALID = c -> c == 0x08 || c == 0x7F || Window.window().getUnicodeMap().isMapped(c);

    private int cursor;
    private final List<Integer> firstBuffer;
    private final List<Integer> secondBuffer;
    private final Predicate<Integer> validCodepoint;

    public DoubleBufferEditor() {
        this(null, 0);
    }

    public DoubleBufferEditor(List<Integer> string, int cursor){
        this(string, cursor, null);
    }

    public DoubleBufferEditor(Predicate<Integer> validCodepoint) {
        this(null, 0, validCodepoint);
    }

    public DoubleBufferEditor(List<Integer> string, int cursor, Predicate<Integer> validCodepoint) {
        firstBuffer = new ArrayList();
        secondBuffer = new ArrayList();
        if(string != null){
            firstBuffer.addAll(string);
        }

        if(cursor > firstBuffer.size() || cursor < 0) {
            throw new IndexOutOfBoundsException("Index to put cursor is out of bounds");
        }
        this.cursor = cursor;

        if(validCodepoint == null){
            this.validCodepoint = DEFAULT_VALID;
        } else{
            this.validCodepoint = validCodepoint;
        }
    }

    @Override
    public int codeAt(int index) {
        if(index >= length() || index < 0){
            throw new IndexOutOfBoundsException("Index to get code at is out of bounds");
        }
        if(index < firstBuffer.size()){
            return firstBuffer.get(index);
        }
        return secondBuffer.get(index - firstBuffer.size());
    }

    @Override
    public void moveCursor(int index) {
        if(index > length() || index < 0){
            throw new IndexOutOfBoundsException("Index to move cursor to is out of bounds");
        }
        cursor = index;
    }

    @Override
    public int getCursor() {
        return cursor;
    }

    private void shiftBuffers() {
        int diff = cursor - firstBuffer.size();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                firstBuffer.add(secondBuffer.remove(secondBuffer.size() - 1));
            }
        } else if (diff < 0) {
            for (int i = 0; i < -diff; i++) {
                secondBuffer.add(firstBuffer.remove(firstBuffer.size() - 1));
            }
        }
    }

    private void insert(int codepoint) {
        firstBuffer.add(codepoint);
        cursor++;
    }

    private void backspace() {
        if (firstBuffer.size() > 0) {
            firstBuffer.remove(firstBuffer.size() - 1);
            cursor--;
        }
    }

    private void delete() {
        if (secondBuffer.size() > 0) {
            secondBuffer.remove(secondBuffer.size() - 1);
        }
    }

    @Override
    public boolean takeCodeInput(int codepoint) {
        if(!validCodepoint.test(codepoint)){
            return false;
        }
        shiftBuffers();
        switch (codepoint) {
            case 0x8:
                backspace();
                break;
            case 0x7F:
                delete();
                break;
            default:
                insert(codepoint);
        }
        return true;
    }

    @Override
    public boolean takeKeyInput(int key){
        switch (key){
            case GLFW_KEY_BACKSPACE:
                backspace();
                return true;
            case GLFW_KEY_DELETE:
                delete();
                return true;
        }
        return false;
    }

    @Override
    public int length() {
        return firstBuffer.size() + secondBuffer.size();
    }

    @Override
    public Iterator<Integer> iterator() {
        List<Integer> complete = new ArrayList();
        complete.addAll(firstBuffer);
        complete.addAll(secondBuffer);
        return complete.iterator();
    }
}
