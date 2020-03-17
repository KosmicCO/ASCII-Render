package us.kosdt.arl.graphics;

import com.esotericsoftware.yamlbeans.YamlException;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import us.kosdt.arl.engine.Settings;

import us.kosdt.arl.graphics.exceptions.InvalidFontSheetException;
import us.kosdt.arl.graphics.opengl.GLState;
import us.kosdt.arl.graphics.tile_render.FontShader;

import java.io.FileNotFoundException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

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

        // main window initializers
        /*
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(handle, pWidth, pHeight);
            glfwSetWindowPos(handle, 0, 0);
        }*/

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
            window.setFontSheet(new FontSheet("resources/fontsheets/Courier-12"));
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



        if(font.getTileWidth() > aWidth || font.getTileHeight() > aHeight) {
            nWidth = Math.max(width * font.getTileWidth(), font.getTileWidth());
            nHeight = Math.max(height * font.getTileHeight(), font.getTileHeight());
            resizeAgain = true;
        }

        /*
        if(nWidth % font.getTileWidth() != 0 || nHeight % font.getTileHeight() != 0){
            nWidth += font.getTileWidth() - (nWidth % font.getTileWidth());
            nHeight += font.getTileHeight() - (nHeight % font.getTileHeight());
            resizeAgain = true;
        }*/

        width = nWidth / font.getTileWidth();
        height = nHeight / font.getTileHeight();


        if(resizeAgain) {
            glfwSetWindowSize(handle, nWidth, nHeight);
        }

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
        resizeWindow(width * font.getTileWidth(), height * font.getTileHeight());
        FontShader.setFontSheet(this.font);
    }

    private int calculateActualWidth() {
        return width * font.getTileWidth();
    }

    private int calculateActualHeight() {
        return height * font.getTileHeight();
    }

    public int getActualWidth() {
        return calculateActualWidth();
    }

    public int getActualHeight() {
        return calculateActualHeight();
    }

    public int getTileWidth() {
        return width;
    }

    public int getTileHeight() {
        return height;
    }
}
