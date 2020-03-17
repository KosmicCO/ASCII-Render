package us.kosdt.arl.graphics_test;

import us.kosdt.arl.graphics.FontSheet;
import us.kosdt.arl.graphics.Window;

public class FontSheetWindowTest {

    public static void main(String[] args) {
        Window.initGLFW(10, 10);
        FontSheet font = Window.window().getFont();
        System.out.println(font);
    }
}
