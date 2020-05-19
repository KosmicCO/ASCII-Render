package us.kosdt.arl.engine;

import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.util.math.Vec2d;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static us.kosdt.arl.engine.Input.Type.*;

public class Input {

    private static final List<ReactiveInputListener> RI_LISTENER = new ArrayList();

    private static BitSet keys = new BitSet();
    private static BitSet prevKeys = new BitSet();

    private static Vec2d mouse = new Vec2d(0, 0);
    private static Vec2d prevMouse = new Vec2d(0, 0);

    private static BitSet buttons = new BitSet();
    private static BitSet prevButtons = new BitSet();

    private static Vec2d mouseWheel = new Vec2d(0, 0);
    private static Vec2d prevMouseWheel = new Vec2d(0, 0);

    static void init() {
        Window.window().setCursorPosCallback((window, xpos, ypos) -> {
            Vec2d nMouse = new Vec2d(((double) xpos) / Window.window().getActualWidth(), 1 - ((double) ypos) / Window.window().getActualHeight());
            RI_LISTENER.forEach(ril -> ril.receiveInput(MOUSE, nMouse, nMouse.sub(mouse), 0, false, false, 0));
            mouse = nMouse;
        });

        Window.window().setKeyCallback((window, key, scancode, action, mods) -> {
            if(key >= 0){
                boolean nks =  action != GLFW_RELEASE;
                RI_LISTENER.forEach(ril -> ril.receiveInput(KEY, null, null, key, nks, nks != keys.get(key), mods));
                keys.set(key, nks);
            }
        });

        Window.window().setCharModsCallback((window, codepoint, mods) -> {
            RI_LISTENER.forEach(ril -> ril.receiveInput(CHAR, null, null, codepoint, false, false, mods));
        });

        Window.window().setMouseButtonCallback((window, button, action, mods) -> {
            if(button >= 0) {
                boolean nbs = action != GLFW_RELEASE;
                RI_LISTENER.forEach(ril -> ril.receiveInput(MOUSE_BUTTON, mouse, null, button, nbs, nbs != buttons.get(button), mods));
                buttons.set(button, nbs);
            }
        });

        Window.window().setScrollCallback((Window, xoffset, yoffset) -> {
            Vec2d nwo = new Vec2d(xoffset, yoffset);
            RI_LISTENER.forEach(ril -> ril.receiveInput(MOUSE_WHEEL, nwo, nwo.sub(mouseWheel), 0, false, false, 0));
            mouseWheel = nwo;
        });
    }

    public static void addListener(ReactiveInputListener ril){
        RI_LISTENER.add(ril);
    }

    static void nextFrame() {
        prevKeys = (BitSet) keys.clone();
        prevMouse = mouse;
        prevButtons = (BitSet) buttons.clone();
        prevMouseWheel = mouseWheel;
    }

    public static boolean keyDown(int key) {
        return keys.get(key);
    }

    public static boolean keyJustPressed(int key) {
        return keys.get(key) && !prevKeys.get(key);
    }

    public static boolean keyJustReleased(int key) {
        return !keys.get(key) && prevKeys.get(key);
    }

    public static Vec2d mouse() {
        return mouse;
    }

    public static Vec2d mouseDelta(){
        return mouse.sub(prevMouse);
    }

    public static boolean mouseDown(int button) {
        return buttons.get(button);
    }

    public static boolean mouseJustPressed(int button) {
        return buttons.get(button) && !prevButtons.get(button);
    }

    public static boolean mouseJustReleased(int button) {
        return !buttons.get(button) && prevButtons.get(button);
    }

    public static Vec2d mouseWheel(){
        return mouseWheel;
    }

    public interface ReactiveInputListener{
        public void receiveInput(Type type, Vec2d mouse, Vec2d deltaMouse, int key, boolean pressed, boolean changed, int mods);
    }

    public enum Type{
        MOUSE,
        KEY,
        CHAR,
        MOUSE_BUTTON,
        MOUSE_WHEEL;
    }
}
