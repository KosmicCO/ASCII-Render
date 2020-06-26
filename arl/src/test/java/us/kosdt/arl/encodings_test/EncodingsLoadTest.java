package us.kosdt.arl.encodings_test;

import com.esotericsoftware.yamlbeans.YamlException;
import us.kosdt.arl.encoding.TileChar;
import us.kosdt.arl.encoding.exceptions.InvalidUnicodeMap;
import us.kosdt.arl.engine.Game;
import us.kosdt.arl.event.Message;
import us.kosdt.arl.event.messages.gui.CharSubmit;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.exceptions.InvalidFontSheetException;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.graphics.tile_render.Render;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.util.math.Vec2d;

import java.io.FileNotFoundException;

public class EncodingsLoadTest {

    public static void main(String[] args) throws FileNotFoundException, YamlException, InvalidFontSheetException, InvalidUnicodeMap {
        Game.init();
        //Window.window().setFontSheet(new FontSheet("resources/fontsheets/GNU-Unifont-CJK-A-Supplement-14"));
        GuiManager.GUI_MANAGER.setComponent(new PrintLastCharComp());
        System.out.println(Window.window().getFont());
        Game.run();
    }

    private static class PrintLastCharComp implements Component {

        private TileChar prev = null;

        @Override
        public boolean contains(Vec2d v) {
            return true;
        }

        @Override
        public boolean handleMessage(Message message) {
            if(message instanceof CharSubmit) {
                prev = Window.window().getFont().unicodeMap.mapCodePoint(((CharSubmit) message).key);
            }
            return true;
        }

        @Override
        public void render() {
            if(prev != null){
                Render.fill(new RenderTile(prev.id, Color.WHITE, Color.BLACK), (x, y) -> x % 2 == 0);
                Render.fill(new RenderTile(prev.doubId(), Color.WHITE, Color.BLACK), (x, y) -> x % 2 == 1);
            }
        }
    }
}
