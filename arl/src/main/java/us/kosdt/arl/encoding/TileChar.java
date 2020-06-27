package us.kosdt.arl.encoding;

public class TileChar {

    public final int id;
    public final boolean doub;
    public final int codepoint;

    public TileChar(int id, boolean doub, int codepoint){
        this.id = id;
        this.doub = doub;
        this.codepoint = codepoint;
    }

    public int doubId(){
        return id + (doub ? 1 : 0);
    }
}
