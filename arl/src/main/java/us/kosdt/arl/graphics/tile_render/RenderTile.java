package us.kosdt.arl.graphics.tile_render;

import us.kosdt.arl.graphics.Color;

public class RenderTile {

    public final int id;
    public final Color fore;
    public final Color back;
    public final Color over;
    public final int rFunc;
    public final boolean flip;

    public static final int RFUNC_NONE= 0;
    public static final int RFUNC_WATER_MODE = 1;
    public static final int RFUNC_WIND_MODE = 2;
    public static final int RFUNC_2D_SIMPLEX_NOISE = 3;

    public static int MAX_RFUNC_ID = 3;

    public RenderTile(int id, Color fore, Color back) {
        this(id, fore, back, RFUNC_NONE);
    }

    public RenderTile(int id, Color fore, Color back, int rFunc) {
        this(id, fore, back, rFunc, Color.CLEAR, false);
    }

    public RenderTile(int id, Color fore, Color back, int rFunc, Color over) {
        this(id, fore, back, rFunc, over, false);
    }

    public RenderTile(int id, Color fore, Color back, int rFunc, Color over, boolean flip) {
        if(fore == null || back == null || over == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if(id < 0){
            throw new IllegalArgumentException("Id cannot be less than 0");
        }
        if(id > FontShader.getFont().maxTileID){
            throw new IllegalArgumentException("Id cannot be greater than max tile id of the window");
        }

        if(rFunc < 0 || rFunc > MAX_RFUNC_ID){
            throw new IllegalArgumentException("Render function id cannot be greater than MAX_RENDER_FUNCTION_ID");
        }

        this.id = id;
        this.fore = fore;
        this.back = back;
        this.rFunc = rFunc;
        this.over = over;
        this.flip = flip;
    }

    public RenderTile setID(int nID) {
        return new RenderTile(nID, fore, back, rFunc, over);
    }

    public RenderTile setRFunc(int nRFunc) {
        return new RenderTile(id, fore, back, nRFunc, over);
    }

    public RenderTile setFore(Color nFore) {
        return new RenderTile(id, nFore, back, rFunc, over);
    }

    public RenderTile setBack(Color nBack) {
        return new RenderTile(id, fore, nBack, rFunc, over);
    }

    public RenderTile setOver(Color nOver) {
        return new RenderTile(id, fore, back, rFunc, nOver);
    }

    public RenderTile blend(RenderTile top) {
        Color blendBack = back.alphaMix(over);
        return new RenderTile(top.id, blendBack.alphaMix(top.fore), blendBack.alphaMix(top.back), top.rFunc, top.over);
    }

    public RenderTile blendOver(Color top) {
        return setOver(over.alphaMix(top));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenderTile that = (RenderTile) o;

        if (id != that.id) return false;
        if (rFunc != that.rFunc) return false;
        if (!fore.equals(that.fore)) return false;
        return back.equals(that.back);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + fore.hashCode();
        result = 31 * result + back.hashCode();
        result = 31 * result + rFunc;
        return result;
    }
}
