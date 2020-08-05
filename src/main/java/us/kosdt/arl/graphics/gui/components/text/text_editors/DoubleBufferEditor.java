package us.kosdt.arl.graphics.gui.components.text.text_editors;

import us.kosdt.arl.graphics.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.lwjgl.glfw.GLFW.*;

public class DoubleBufferEditor implements TextEditor{

    public static final Predicate<Integer> SINGLE_LINE_VALID = c -> c == 0x08 || c == 0x7F || Window.window().getUnicodeMap().isMapped(c);
    public static final Predicate<Integer> MULTI_LINE_VALID = SINGLE_LINE_VALID.or(c -> c == 0x0A);

    private int cursor;
    private final List<Integer> firstBuffer;
    private final List<Integer> secondBuffer;
    private final Predicate<Integer> validCodepoint;

    private int earliestChange;
    private boolean changed;

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
        firstBuffer = new ArrayList<>();
        secondBuffer = new ArrayList<>();
        if(string != null){
            firstBuffer.addAll(string);
        }

        if(cursor > firstBuffer.size() || cursor < 0) {
            throw new IndexOutOfBoundsException("Index to put cursor is out of bounds");
        }
        this.cursor = cursor;

        if(validCodepoint == null){
            this.validCodepoint = SINGLE_LINE_VALID;
        } else{
            this.validCodepoint = validCodepoint;
        }

        earliestChange = 0;
        changed = true;
    }

    @Override
    public int codeAt(int index) {
        if(index >= size() || index < 0){
            throw new IndexOutOfBoundsException("Index to get code at is out of bounds");
        }
        if(index < firstBuffer.size()){
            return firstBuffer.get(index);
        }
        return secondBuffer.get(secondBuffer.size() - index + firstBuffer.size() - 1);
    }

    @Override
    public List<Integer> getText() {
        List<Integer> text = new ArrayList<>();
        text.addAll(firstBuffer);
        for (int i = secondBuffer.size() - 1; i >= 0; i--){
            text.add(secondBuffer.get(i));
        }
        return text;
    }

    @Override
    public void moveCursor(int index) {
        if(index > size() || index < 0){
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

    private void updateEarliestChange(){
        changed = true;
        if(cursor < earliestChange){
            earliestChange = cursor;
        }
    }

    private void insert(int codepoint) {
        firstBuffer.add(codepoint);
        updateEarliestChange();
        cursor++;
    }

    private void backspace() {
        if (firstBuffer.size() > 0) {
            firstBuffer.remove(firstBuffer.size() - 1);
            cursor--;
            updateEarliestChange();
        }
    }

    private void delete() {
        if (secondBuffer.size() > 0) {
            secondBuffer.remove(secondBuffer.size() - 1);
            updateEarliestChange();
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
        shiftBuffers();
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
    public int size() {
        return firstBuffer.size() + secondBuffer.size();
    }

    @Override
    public int earliestChange() {
        return earliestChange;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void resetChanged() {
        earliestChange = size();
        changed = false;
    }

    @Override
    public void deleteRange(int start, int end) {
        if(start < 0) {
            throw new IllegalArgumentException("The start of the range cannot be less than zero");
        }
        if(start > end){
            throw new IllegalArgumentException("The start index cannot be greater than end index");
        }
        moveCursor(Math.min(start, size()));
        shiftBuffers();
        if(end >= size()){
            secondBuffer.clear();
        }else{
            for (int i = 0; i < end - start + 1; i++){
                if(secondBuffer.isEmpty()){
                    break;
                }
                secondBuffer.remove(secondBuffer.size() - 1);
            }
        }
    }

    @Override
    public boolean takeKeyInput(int key, int[] highlight) {
        if(key != GLFW_KEY_BACKSPACE && key != GLFW_KEY_DELETE){
            return false;
        }
        switch (highlight.length){
            case 2:
                if(highlight[0] < 0 || highlight[1] < highlight[0]){
                    throw new IllegalArgumentException("The given highlight is not a valid highlight range");
                }
                deleteRange(highlight[0], highlight[1]);
                return takeKeyInput(key);
            case 1:
                if (highlight[0] < 0){
                    throw new IllegalArgumentException("The first index of highlight is less than zero");
                }
                moveCursor(Math.min(size(), highlight[0]));
            case 0:
                return takeKeyInput(key);
        }
        return false;
    }

    @Override
    public boolean takeCodeInput(int codepoint, int[] highlight){
        if(!validCodepoint.test(codepoint)){
            return false;
        }
        switch (highlight.length){
            case 2:
                if(highlight[0] < 0 || highlight[1] < highlight[0]){
                    throw new IllegalArgumentException("The given highlight is not a valid highlight range");
                }
                deleteRange(highlight[0], highlight[1]);
                return takeCodeInput(codepoint);
            case 1:
                if (highlight[0] < 0){
                    throw new IllegalArgumentException("The first index of highlight is less than zero");
                }
                moveCursor(Math.min(size(), highlight[0]));
            case 0:
                return takeCodeInput(codepoint);
        }
        return false;
    }
}
