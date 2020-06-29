package us.kosdt.arl.graphics.gui.components;

import us.kosdt.arl.encoding.TileCharList;
import us.kosdt.arl.event.Message;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.util.math.Vec2d;

import java.util.Iterator;

public abstract class Label implements Component {

    private TileCharList label;

    public Label(TileCharList label){
        this.label = label;
    }

    public Label(Iterator<Integer> codepoints) {
        this.label = Window.window().getUnicodeMap().mapCodePointString(codepoints);
    }

    public TileCharList getLabel(){
        return label;
    }

    public void setLabel(TileCharList label){
        this.label = label;
    }

    public void setLabel(Iterator<Integer> codepoints) {
        this.label = Window.window().getUnicodeMap().mapCodePointString(codepoints);
    }

    @Override
    public boolean contains(Vec2d v) {
        return false;
    }

    @Override
    public boolean handleMessage(Message message, boolean first) {
        return false;
    }
}
