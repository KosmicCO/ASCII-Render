package us.kosdt.arl.graphics.tile_render;

import us.kosdt.arl.graphics.Color;

import java.util.Objects;

public class RenderTile {

    public final int id;
    public final Color fore;
    public final Color back;
    public final int rFunc;

    public static final int RFUNC_NONE= 0;

    public static int MAX_RFUNC_ID = 2;

    public RenderTile(int id, Color fore, Color back) {
        this(id, fore, back, RFUNC_NONE);
    }

    public RenderTile(int id, Color fore, Color back, int rFunc) {
        if(fore == null || back == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if(id < 0){
            throw new IllegalArgumentException("Id cannot be less than 0");
        }

        if(rFunc < 0 || rFunc > MAX_RFUNC_ID){
            throw new IllegalArgumentException("Render function id cannot be greater than MAX_RENDER_FUNCTION_ID");
        }

        this.id = id;
        this.fore = fore;
        this.back = back;
        this.rFunc = rFunc;
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
