package us.kosdt.arl.encoding;

public class TileChar {

    public final int id;
    public final boolean doub;

    public TileChar(int id, boolean doub){
        this.id = id;
        this.doub = doub;
    }

    public int doubId(){
        return id + (doub ? 1 : 0);
    }
}
