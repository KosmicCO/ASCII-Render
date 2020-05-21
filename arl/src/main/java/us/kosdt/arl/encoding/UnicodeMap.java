package us.kosdt.arl.encoding;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import us.kosdt.arl.encoding.exceptions.InvalidUnicodeMap;
import us.kosdt.arl.graphics.FontSheet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UnicodeMap {

    private final UnicodeMapEntry[] sortedMap;

    public UnicodeMap(String file) throws FileNotFoundException, YamlException, InvalidUnicodeMap {
        YamlReader reader = new YamlReader(new FileReader(file));
        reader.getConfig().setClassTag("ent", UnicodeMapEntry.class);
        List<UnicodeMapEntry> loadedMap = reader.read(MapLoadYAML.class).entries;

        for (UnicodeMapEntry mapEntryYAML : loadedMap) {
            mapEntryYAML.fillInfo();
        }

        sortedMap = loadedMap.toArray(new UnicodeMapEntry[loadedMap.size()]);
        Arrays.sort(sortedMap);
    }

    private TileChar interpolateTileChar(UnicodeMapEntry ume, int codepoint){
        if(codepoint >= ume.len + ume.ch){
            return null;
        }
        return new TileChar((codepoint - ume.ch) * (ume.doub ? 2 : 1) + ume.pos, ume.doub);
    }
    
    public TileChar getMappedTileChar(int codepoint){
        int index = Arrays.binarySearch(sortedMap, new UnicodeMapEntry(codepoint));
        
        if(index >= 0){ // exact key found
            return interpolateTileChar(sortedMap[index], codepoint);
        }else{
            index = -index - 1; // non-exact matches are of the form (-(insertionPoint) - 1)
            if(index == 0) { // less than smallest entry: Not Mapped
                return null;
            }
            return interpolateTileChar(sortedMap[index - 1], codepoint);
        }
    }

    //TODO: Make string parser for characters with supplement unicode characters.

    public static class MapLoadYAML {
        public List<UnicodeMapEntry> entries;
    }

    public static class UnicodeMapEntry implements Comparable<UnicodeMapEntry> {
        public Integer ch;
        public Integer pos;
        public Integer len;
        public Integer end;
        public boolean doub;

        public UnicodeMapEntry(){
            ch = null;
            pos = null;
            len = null;
            end = null;
            doub = false;
        }

        public UnicodeMapEntry(int c){
            ch = c;
            pos = Integer.MAX_VALUE;
            len = Integer.MAX_VALUE;
            end = Integer.MAX_VALUE;
            doub = true;
        }

        public void fillInfo() throws InvalidUnicodeMap {
            if(ch == null){
                throw new InvalidUnicodeMap("Character not specified in map entry");
            }
            if(pos == null){
                pos = ch;
            }
            if(len == null && end == null){
                len = 1;
            }
            if(len != null){
                end = pos + len * (doub ? 2 : 1);
            } else {
                if(doub && (end - pos) % 2 != 0){
                    System.out.println(end + " " + pos);
                    throw new InvalidUnicodeMap("Double-character definition does not allocate an even number of slots");
                }
                len = (end - pos) / (doub ? 2 : 1);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UnicodeMapEntry that = (UnicodeMapEntry) o;

            if (!ch.equals(that.ch)) return false;
            if (!pos.equals(that.pos)) return false;
            if (!len.equals(that.len)) return false;
            if (!end.equals(that.end)) return false;
            return doub == that.doub;
        }

        @Override
        public int hashCode() {
            int result = ch.hashCode();
            result = 31 * result + pos.hashCode();
            result = 31 * result + len.hashCode();
            result = 31 * result + end.hashCode();
            result = 31 * result + Boolean.hashCode(doub);
            return result;
        }

        @Override
        public int compareTo(UnicodeMapEntry me) {
            int chDiff = Integer.compare(ch, me.ch);
            if(chDiff != 0){
                return chDiff;
            }

            int posDiff = Integer.compare(pos, me.pos);
            if(posDiff != 0) {
                return posDiff;
            }

            int lenDiff = Integer.compare(len, me.len);
            if(lenDiff != 0) {
                return lenDiff;
            }

            int endDiff = Integer.compare(end, me.end);
            if(endDiff != 0 ){
                return endDiff;
            }

            return Boolean.compare(doub, me.doub);
        }
    }
}
