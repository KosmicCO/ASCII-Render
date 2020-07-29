package us.kosdt.arl.graphics.tile_render;

import us.kosdt.arl.graphics.Camera;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec2i;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import static us.kosdt.arl.graphics.tile_render.RenderTile.MAX_RFUNC_ID;
import static us.kosdt.arl.graphics.tile_render.RenderTile.RFUNC_NONE;

public abstract class Render {

    private static Vec2i renderDim;
    private static int[] renderModes;
    private static RenderTile[][] lastTileBuffer;
    private static RenderTile[][] tileBuffer;

    public static final RenderTile DEFAULT_TILE = new RenderTile(0, Color.WHITE, Color.BLACK, RFUNC_NONE);

    private static boolean rendering = false;

    public static Vec2i getRenderDim() {
        return renderDim;
    }

    private static Vec2i windowTileDim() {
        return new Vec2i(Window.window().getTileWidth(), Window.window().getTileHeight());
    }

    public static void startRender() {
        Vec2i dim = windowTileDim();
        if(!dim.equals(renderDim)){
            lastTileBuffer = null;
        }
        renderDim = dim;
        tileBuffer = new RenderTile[renderDim.x][renderDim.y];
        rendering = true;
        fill(DEFAULT_TILE);
    }

    public static boolean inBounds(int x, int y){
        return 0 <= x && 0 <= y && x < renderDim.x && y < renderDim.y;
    }

    public static void fill(RenderTile t) {
        if(!rendering){
            throw new RuntimeException("Attempting to draw to buffer while not in render mode");
        }
        for (int x = 0; x < tileBuffer.length; x++) {
            Arrays.fill(tileBuffer[x], t);
        }
    }

    public static void fill(RenderTile t, BiPredicate<Integer, Integer> replace){
        if(!rendering){
            throw new RuntimeException("Attempting to draw to buffer while not in render mode");
        }
        for (int x = 0; x < tileBuffer.length; x++) {
            for(int y = 0; y < tileBuffer[x].length; y++) {
                if(replace.test(x, y)) {
                    tileBuffer[x][y] = t;
                }
            }
        }
    }

    private static void drawTileUnchecked(RenderTile t, int x, int y){
        tileBuffer[x][y] = tileBuffer[x][y].blend(t);
    }

    private static void drawPermeableTileUnchecked(RenderTile t, int x, int y) {
        RenderTile bot = tileBuffer[x][y];
        tileBuffer[x][y] = new RenderTile(bot.id, bot.fore.alphaMix(t.back), bot.back.alphaMix(t.back), t.rFunc);
    }

    private static void drawOverColorUnchecked(Color c, int x, int y){
        tileBuffer[x][y] = tileBuffer[x][y].setOver(c);
    }

    private static void blendOverColorUnchecked(Color c, int x, int y) {
        tileBuffer[x][y] = tileBuffer[x][y].blendOver(c);
    }

    public static void drawTile(RenderTile t, int x, int y) {
        if(!rendering){
            throw new RuntimeException("Attempting to draw to buffer while not in render mode");
        }
        if(0 > x || 0 > y || x >= renderDim.x || y >= renderDim.y) {
            throw new IllegalArgumentException("SetTile parameters are out of bounds");
        }
        drawTileUnchecked(t, x, y);
    }

    public static void drawOverColor(Color c, int x, int y) {
        if(!rendering){
            throw new RuntimeException("Attempting to draw to buffer while not in render mode");
        }
        if(0 > x || 0 > y || x >= renderDim.x || y >= renderDim.y) {
            throw new IllegalArgumentException("SetTile parameters are out of bounds");
        }
        drawOverColorUnchecked(c, x, y);
    }

    public static void blendOverColor(Color c, int x, int y) {
        if(!rendering){
            throw new RuntimeException("Attempting to draw to buffer while not in render mode");
        }
        if(0 > x || 0 > y || x >= renderDim.x || y >= renderDim.y) {
            throw new IllegalArgumentException("SetTile parameters are out of bounds");
        }
        blendOverColorUnchecked(c, x, y);
    }

    public static void drawRect(RenderTile t, int x1, int y1, int x2, int y2) {
        Vec2i botLeft = new Vec2i(Math.max(0, Math.min(x1, x2)), Math.max(0, Math.min(y1, y2)));
        Vec2i topRight = new Vec2i(Math.min(renderDim.x - 1, Math.max(x1, x2)), Math.min(renderDim.y - 1, Math.max(y1, y2)));

        for (int x = botLeft.x; x <= topRight.x; x++) {
            for(int y = botLeft.y; y <= topRight.y; y++){
                drawTileUnchecked(t, x, y);
            }
        }
    }

    public static void drawForeground(RenderTile t, int x, int y) {
        drawTile(t.setBack(Color.CLEAR), x, y);
    }

    public static void drawRect(RenderTile t, Vec2i pos, Vec2i dim){
        Vec2i corner = pos.add(dim);
        drawRect(t, pos.x, pos.y, corner.x, corner.y);
    }

    public static void drawRectExclusive(RenderTile t, Vec2i pos, Vec2i dim){
        Vec2i corner = pos.add(dim.add(new Vec2i(Integer.compare(0, dim.x), Integer.compare(0, dim.y))));
        drawRect(t, pos.x, pos.y, corner.x, corner.y);
    }

    public static void drawPermeableRect(RenderTile t, int x1, int y1, int x2, int y2) {
        Vec2i botLeft = new Vec2i(Math.max(0, Math.min(x1, x2)), Math.max(0, Math.min(y1, y2)));
        Vec2i topRight = new Vec2i(Math.min(renderDim.x - 1, Math.max(x1, x2)), Math.min(renderDim.y - 1, Math.max(y1, y2)));

        for (int x = botLeft.x; x <= topRight.x; x++) {
            for(int y = botLeft.y; y <= topRight.y; y++){
                drawPermeableTileUnchecked(t, x, y);
            }
        }
    }

    public static void drawPermeableRect(RenderTile t, Vec2i pos, Vec2i dim){
        Vec2i corner = pos.add(dim);
        drawPermeableRect(t, pos.x, pos.y, corner.x, corner.y);
    }

    private static boolean different() {
        if(lastTileBuffer == null || lastTileBuffer.length != tileBuffer.length
                || (lastTileBuffer.length != 0 && lastTileBuffer[0].length != tileBuffer[0].length)) {
            return true;
        }
        for(int x = 0; x < tileBuffer.length; x++) {
            for(int y = 0; y < tileBuffer[x].length; y++) {
                if(!tileBuffer[x][y].equals(lastTileBuffer[x][y])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int[] calculateRenderModes() {
        Set<Integer> modes = new TreeSet<>();
        for(RenderTile[] ta : tileBuffer){
            for(RenderTile t : ta){
                if(t.rFunc >= 0 && t.rFunc != RFUNC_NONE && t.rFunc <= MAX_RFUNC_ID){
                    modes.add(t.rFunc);
                }
            }
        }
        Integer[] classInts = modes.toArray(new Integer[0]);
        int[] ints = new int[classInts.length];
        for(int i = 0; i < classInts.length; i++) {
            ints[i] = classInts[i];
        }
        return ints;
    }

    public static void finishRender() {
        rendering = false;
        Vec2i dim = windowTileDim();
        if(!dim.equals(renderDim)){
            return;
        }
        Camera.camera2d.setCenterSize(new Vec2d(0, 0), dim.toVec2d());
        if(different()) {
            renderModes = calculateRenderModes();
            FontShader.drawTiles(tileBuffer, renderModes);
        }else{
            FontShader.drawTiles();
        }
        lastTileBuffer = tileBuffer;
    }
}
