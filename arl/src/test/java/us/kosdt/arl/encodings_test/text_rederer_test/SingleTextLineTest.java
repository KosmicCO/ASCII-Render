package us.kosdt.arl.encodings_test.text_rederer_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.util.math.Vec2d;

import java.util.ArrayList;
import java.util.List;

public class SingleTextLineTest {

    public static void main(String[] args) {
        Core.init();
        int[] codepoints = {'t', 'e', 's', 't', '1', '2', '3', ' ', '\n', '(', '\"', 0x5D0, ')', '1', '2', '3', 0x5D1, '!', 0x200F, '\"', '.', 0x3042};
        List<Integer> cps = new ArrayList();
        for (int c : codepoints){
            cps.add(c);
        }
        SingleLineRender slr = new SingleLineRender(cps, 0, 30, new Vec2d(1, 1), false);
        GuiManager.GUI_MANAGER.setComponent(slr);
        Core.setGuiControl();
        Core.run();
    }
}
