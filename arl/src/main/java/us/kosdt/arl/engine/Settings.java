package us.kosdt.arl.engine;

import us.kosdt.arl.graphics.Color;

public abstract class Settings {

    public static boolean SHOW_OPENGL_DEBUG_INFO = true;
    public static boolean ENABLE_VSYNC = false;

    public static boolean CLOSE_ON_X = true;
    public static Color BACKGROUND_COLOR = Color.BLACK;
    public static double MIN_FRAME_TIME = 0.001;
    public static double MAX_FRAME_TIME = 0.1;
}
