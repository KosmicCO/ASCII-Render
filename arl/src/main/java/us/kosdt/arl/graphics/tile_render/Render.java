package us.kosdt.arl.graphics.tile_render;

import us.kosdt.arl.graphics.Camera;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec2i;

import java.util.Set;
import java.util.TreeSet;

import static us.kosdt.arl.graphics.tile_render.RenderTile.MAX_RFUNC_ID;
import static us.kosdt.arl.graphics.tile_render.RenderTile.RFUNC_NONE;

public abstract class Render {

    private static Vec2i renderDim;
    private static int[] renderModes;
    private static RenderTile[][] lastTileBuffer;
    private static RenderTile[][] tileBuffer;
    //TODO: Record the list of unique graphical functions used

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
    }

    public static void fill(RenderTile t) {
        if(!rendering) {
            return;
        }
        for (int x = 0; x < tileBuffer.length; x++) {
            for(int y = 0; y < tileBuffer[x].length; y++) {
                tileBuffer[x][y] = t;
            }
        }
    }

    public static void setTile(RenderTile t, int x, int y) {
        if(0 > x || 0 > y || x >= renderDim.x || y >= renderDim.y) {
            throw new IllegalArgumentException("SetTile parameters are out of bounds");
        }
        tileBuffer[x][y] = t;
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
        Set<Integer> modes = new TreeSet();
        for(RenderTile[] ta : tileBuffer){
            for(RenderTile t : ta){
                if(t.rFunc >= 0 && t.rFunc != RFUNC_NONE && t.rFunc <= MAX_RFUNC_ID){
                    modes.add(t.rFunc);
                }
            }
        }
        Integer[] classInts = (Integer[]) modes.toArray(new Integer[0]);
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
