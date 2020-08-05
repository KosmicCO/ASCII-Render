package us.kosdt.arl.graphics.gui.components.text;

import us.kosdt.arl.event.Message;
import us.kosdt.arl.event.messages.gui.CharSubmit;
import us.kosdt.arl.event.messages.gui.KeyPress;
import us.kosdt.arl.event.messages.gui.MouseButton;
import us.kosdt.arl.event.messages.gui.MousePosition;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.components.text.text_editors.TextEditor;
import us.kosdt.arl.graphics.gui.components.text.text_renderers.EditorRenderer;
import us.kosdt.arl.util.math.Vec2d;

import static org.lwjgl.glfw.GLFW.*;
import static us.kosdt.arl.graphics.gui.components.text.text_renderers.EditorRenderer.Direction.*;

public abstract class EditorComponent implements Component { // TODO: Finish

    private TextEditor editor;
    private EditorRenderer renderer;

    private boolean selected;

    public EditorComponent(TextEditor editor, EditorRenderer renderer){
        this.editor = editor;
        this.renderer = renderer;

        selected = false;
    }

    private void updateCursorPos(){
        if(renderer.getHighlighted().length == 1){
            editor.moveCursor(renderer.getHighlighted()[0]);
        }
        if(renderer.getHighlighted().length == 0){
            renderer.setCursor(editor.getCursor());
        }
    }

    @Override
    public boolean handleMessage(Message message, boolean first) {
        boolean caught = Message.onMessageType(message, MousePosition.class, mp -> {
            if(first){
                return renderer.takeMouse(mp.position);
            }
            return false;
        });

        caught |= Message.onMessageType(message, MouseButton.class, mb -> {
            if(first && contains(mb.position)) {
                selected = true;
                return renderer.takePress(mb.state, mb.position);
            }
            selected = false;
            return false;
        });

        caught |= Message.onMessageType(message, CharSubmit.class, cs -> {
            if(first && selected){
                boolean changed = editor.takeCodeInput(cs.key, renderer.getHighlighted());
                if(changed){
                    renderer.setCursor(editor.getCursor());
                }
                return changed;
            }
            return false;
        });

        caught |= Message.onMessageType(message, KeyPress.class, kp -> {
            if(first && selected) {
                if(kp.state) {
                    switch(kp.key){
                        case GLFW_KEY_LEFT:
                            renderer.directionInput(renderer.isLeftToRight() ? BACK : FORWARD);
                            break;
                        case GLFW_KEY_RIGHT:
                            renderer.directionInput(renderer.isLeftToRight() ? FORWARD : BACK);
                            break;
                        case GLFW_KEY_UP:
                            renderer.directionInput(BACK_LINE);
                            break;
                        case GLFW_KEY_DOWN:
                            renderer.directionInput(FORWARD_LINE);
                            break;
                        default:
                            boolean changed = editor.takeKeyInput(kp.key, renderer.getHighlighted());
                            if(changed){
                                renderer.setCursor(editor.getCursor());
                            }
                            return changed;
                    }
                    return true;
                }
            }
            return false;
        });

        return !caught;
    }

    @Override
    public void render() {
        updateCursorPos();
        renderer.updateFromEditor();
        renderer.render();
    }
}
