package us.kosdt.arl.encodings_test.text_editor_test;

import us.kosdt.arl.event.Message;
import us.kosdt.arl.event.messages.gui.CharSubmit;
import us.kosdt.arl.event.messages.gui.KeyPress;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.components.text.text_editors.TextEditor;
import us.kosdt.arl.util.math.Vec2d;

public class PrintEditor implements Component {

    private boolean changed;
    private TextEditor editor;

    public PrintEditor(TextEditor te){
        changed = false;
        this.editor = te;
    }

    @Override
    public boolean contains(Vec2d v) {
        return true;
    }

    @Override
    public boolean handleMessage(Message message, boolean first) {
        Message.onMessageType(message, CharSubmit.class, cs -> {
            changed |= editor.takeCodeInput(cs.key);
        });
        Message.onMessageType(message, KeyPress.class, kp -> {
            if(kp.state) {
                changed |= editor.takeKeyInput(kp.key);
            }
        });
        return true;
    }

    @Override
    public void render() {
        if(changed) {
            int sz = editor.size();
            for (int i = 0; i < sz; i++){
                System.out.print((char) editor.codeAt(i));
            }
            System.out.println();
            changed = false;
        }
    }
}
