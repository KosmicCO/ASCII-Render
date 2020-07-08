package us.kosdt.arl.encodings_test.text_rederer_test;

import org.lwjgl.glfw.GLFW;
import us.kosdt.arl.event.Message;
import us.kosdt.arl.event.messages.gui.MouseButton;
import us.kosdt.arl.event.messages.gui.MousePosition;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.components.text.text_renderers.SingleTextLine;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.util.math.Vec2d;

import java.util.List;

public class SingleLineRender extends SingleTextLine implements Component {

    private Vec2d position;

    public SingleLineRender(List<Integer> string, int start, int size, Vec2d pos, boolean leftToRight) {
        super(string, start, size, leftToRight);
        position = pos;
    }

    @Override
    public boolean contains(Vec2d v) {
        return (new Vec2d(getSize(), 1)).contains(v.sub(position));
    }

    @Override
    public int getIndexFromMouse(Vec2d mouse) {
        int visualIndex = Math.min(Math.max((isLeftToRight() ? 1 : -1) * ((int) (mouse.x - position.x - (isLeftToRight() ? 0 : getSize()))), 0), getRendered().size());
        if(visualIndex >= getIndices().size()){
            return getIndices().size();
        }
        return getIndices().get(visualIndex);
    }

    @Override
    public boolean handleMessage(Message message, boolean first) {
        boolean ret = Message.onMessageType(message, MouseButton.class, mb -> {
            if(mb.button == GLFW.GLFW_MOUSE_BUTTON_1 && mb.state && !(first && contains(mb.position))){
                clearHighlighted();
                return true;
            }
            return false;
        });

        if(ret){
            return false;
        }

        boolean caught = Message.onMessageType(message, MouseButton.class, mb -> {
            if(mb.button != GLFW.GLFW_MOUSE_BUTTON_1){
                return false;
            }
            if(mb.state){
                takePress(mb.state, mb.position);
            }else if(!mb.state && first){
                takePress(mb.state, mb.position);
                return true;
            }

            return false;
        });

        caught |= Message.onMessageType(message, MousePosition.class, mp ->{
            takeMouse(mp.position);
            return false;
        });

        return caught;
    }

    @Override
    public void render() {
        for (int i = 0; i < getRendered().size(); i++){
            int x = (int) Math.floor(position.x + (isLeftToRight() ? i : getSize() - i - 1));
            int y = (int) Math.floor(position.y);
            if(Render.inBounds(x, y)) {
                RenderTile toRender = getRendered().get(i).setFore(Color.WHITE);
                switch (getHighlightLevel(i)){
                    case HIGHLIGHT_LEVEL_CURSOR:
                        toRender = toRender.setBack(Color.RED);
                        break;
                    case HIGHLIGHT_LEVEL_SECTION:
                        toRender = toRender.setBack(Color.GREEN);
                        break;
                }
                Render.drawTile(toRender, x, y);
            }
        }
    }
}
