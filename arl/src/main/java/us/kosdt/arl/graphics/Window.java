package us.kosdt.arl.graphics;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Configuration;
import us.kosdt.arl.engine.Settings;

import us.kosdt.arl.graphics.tile_render.FontShader;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec2i;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static us.kosdt.arl.util.math.MathUtils.*;

public class Window {

    private static Window window;

    private final long handle;
    private int width;
    private int height;
    private boolean resizable;
    private FontSheet font;

    public Window(int width, int height, FontSheet font) {
        this(width, height, font, "", true, true);
    }

    public Window(int width, int height, FontSheet font, String title, boolean resizable, boolean showCursor) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        this.resizable = resizable;

        if (this.resizable) {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }

        glfwWindowHint(GLFW_SAMPLES, GLFW_FALSE);

        this.width = width;
        this.height = height;
        this.font = font;

        if(this.font == null){
            handle = glfwCreateWindow(100, 100, title, NULL, NULL);
        } else {

            handle = glfwCreateWindow(calculateActualWidth(), calculateActualHeight(), title, NULL, NULL);
        }
        if (handle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        setCursorEnabled(showCursor);
    }

    public static void initGLFW(int width, int height) {
        if (Settings.SHOW_OPENGL_DEBUG_INFO) {
            GLFWErrorCallback.createThrow().set();
            Configuration.DEBUG.set(true);
        }

        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        try {
            window = new Window(width, height, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        window.createContext();
        if (Settings.ENABLE_VSYNC) {
            glfwSwapInterval(1);
        }

        glfwSetFramebufferSizeCallback(window.handle, (w, wd, ht) -> Window.window.resize(wd, ht));


        glfwShowWindow(window.handle);
        try {
            window.setFontSheet(new FontSheet("resources/fontsheets/Hack-12"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Window window() {
        return window;
    }

    public static void cleanupGLFW() {
        window.cleanup();
        glfwTerminate();
        if (Settings.SHOW_OPENGL_DEBUG_INFO) {
            glfwSetErrorCallback(null).free();
        }
    }

    private void cleanup() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public void setCursorEnabled(boolean enabled) {
        if (enabled) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }

    public void createContext() {
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        glfwSwapInterval(0);

        //GLState.enable(GL_BLEND); // GL_DEPTH_TEST,
        //GLState.setBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_BLEND);

        if (Settings.SHOW_OPENGL_DEBUG_INFO) {
            GLUtil.setupDebugMessageCallback();
        }
    }

    public void nextFrame() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void setTitle(String s) {
        glfwSetWindowTitle(handle, s);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public FontSheet getFont() {
        return font;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
    }

    private void resize(int aWidth, int aHeight) {

        if(font == null){
            return;
        }


        int nWidth = aWidth;
        int nHeight = aHeight;
        boolean resizeAgain = false;
        glViewport(0, 0, nWidth, nHeight);



        if(font.tileWidth > aWidth || font.tileHeight > aHeight) {
            nWidth = Math.max(width * font.tileWidth, font.tileWidth);
            nHeight = Math.max(height * font.tileHeight, font.tileHeight);
            resizeAgain = true;
        }

        /*
        if(nWidth % font.getTileWidth() != 0 || nHeight % font.getTileHeight() != 0){
            nWidth += font.getTileWidth() - (nWidth % font.getTileWidth());
            nHeight += font.getTileHeight() - (nHeight % font.getTileHeight());
            resizeAgain = true;
        }*/

        width = nWidth / font.tileWidth;
        height = nHeight / font.tileHeight;


        if(resizeAgain) {
            glfwSetWindowSize(handle, nWidth, nHeight);
        }

    }

    public long getHandle() {
        return handle;
    }

    public void resizeWindow(int width, int height) {
        if(width <= 0 || height <= 0) {
            throw new RuntimeException("Cannot set window width or height to less than or equal to 0");
        }
        glfwSetWindowSize(window.handle, width, height);
        resize(width, height);
    }

    public void setFontSheet(FontSheet font) {
        this.font = font;
        resizeWindow(width * font.tileWidth, height * font.tileHeight);
        FontShader.setFont(this.font);
    }

    public Vec2d toWindowScale(Vec2d v){
        return v.mul(new Vec2d(width, height));
    }

    public Vec2d toClampedWindowScale(Vec2d v) {
        Vec2d scaled = toWindowScale(v);
        return new Vec2d(clamp(scaled.x, 0, width - EPSILON), clamp(scaled.y, 0, height - EPSILON));
    }

    public Vec2i toDiscreteWindowScale(Vec2d v){
        Vec2d scaled = toWindowScale(v);
        return new Vec2i(floor(scaled.x), floor(scaled.y));
    }

    public Vec2i toClampedDiscreteWindowScale(Vec2d v){
        Vec2i scaled = toDiscreteWindowScale(v);
        return new Vec2i(clamp(scaled.x, 0, width - 1), clamp(scaled.y, 0, height - 1));
    }

    private int calculateActualWidth() {
        return width * font.tileWidth;
    }

    private int calculateActualHeight() {
        return height * font.tileHeight;
    }

    public int getActualWidth() {
        int[] h = new int[1];
        int[] w = new int[1];
        GLFW.glfwGetWindowSize(handle, w, h);
        return w[0];
    }

    public int getActualHeight() {
        int[] h = new int[1];
        int[] w = new int[1];
        GLFW.glfwGetWindowSize(handle, w, h);
        return h[0];
    }

    public int getTileWidth() {
        return width;
    }

    public int getTileHeight() {
        return height;
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI cursorPosCallback) {
        glfwSetCursorPosCallback(handle, cursorPosCallback);
    }

    public void setKeyCallback(GLFWKeyCallbackI keyCallback) {
        glfwSetKeyCallback(handle, keyCallback);
    }

    public void setCharModsCallback(GLFWCharModsCallbackI charModsCallback) {
        glfwSetCharModsCallback(handle, charModsCallback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI mouseButtonCallback) {
        glfwSetMouseButtonCallback(handle, mouseButtonCallback);
    }

    public void setScrollCallback(GLFWScrollCallbackI scrollCallback) {
        glfwSetScrollCallback(handle, scrollCallback);
    }
}
