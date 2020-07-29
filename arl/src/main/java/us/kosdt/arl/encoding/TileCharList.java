package us.kosdt.arl.encoding;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.Stream;

public class TileCharList extends AbstractList<TileChar> {

    private final TileChar[] array;

    public TileCharList(TileChar[]... tca){
        array = Stream.of(tca).flatMap(Stream::of).toArray(TileChar[]::new);
    }

    public TileCharList(List<TileChar> tcl){
        array = tcl.toArray(new TileChar[0]);
    }

    @Override
    public TileChar get(int i) {
        return array[i];
    }

    @Override
    public int size() {
        return array.length;
    }
}
