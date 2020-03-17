package us.kosdt.arl.graphics_test;

import us.kosdt.arl.engine.Core;
import us.kosdt.arl.engine.Settings;

public class CoreTest {

    public static void main(String[] args) {
        //Settings.CLOSE_ON_X = false;
        Core.init();
        Core.run();
    }
}
