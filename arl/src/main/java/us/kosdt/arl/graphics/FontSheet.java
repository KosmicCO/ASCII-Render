package us.kosdt.arl.graphics;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import us.kosdt.arl.encoding.UnicodeMap;
import us.kosdt.arl.encoding.exceptions.InvalidUnicodeMap;
import us.kosdt.arl.graphics.exceptions.InvalidFontSheetException;
import us.kosdt.arl.graphics.opengl.Texture;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class FontSheet {

    private static int nextID = 0;

    public final int id;
    public final Texture fontSheet;
    public final int tileWidth;
    public final int tileHeight;
    public final double ratio;
    public final String sheetName;
    public final String author;
    public final String version;
    public final int sheetWidth;
    public final int sheetHeight;
    public final int maxTileID;

    public final int tileColumns;
    public final int tileRows;

    public final UnicodeMap unicodeMap;

    public FontSheet(String sheetDir, String infoDir, String mapDir) throws FileNotFoundException, YamlException, InvalidFontSheetException, InvalidUnicodeMap {
        YamlReader reader = new YamlReader(new FileReader(infoDir));
        FontSheetInfoYAML info = reader.read(FontSheetInfoYAML.class);

        fontSheet = Texture.load(sheetDir);

        if(fontSheet.getWidth() % info.tile_width != 0 || fontSheet.getHeight() % info.tile_height != 0) {
            throw new InvalidFontSheetException("The tile width or height do not divide the font sheet width or height evenly");
        }

        sheetWidth = fontSheet.getWidth();
        sheetHeight = fontSheet.getHeight();
        tileColumns = sheetWidth / info.tile_width;
        tileRows = sheetHeight / info.tile_height;

        if(info.max_tile_id + 1 > tileColumns * tileRows) {
            throw new InvalidFontSheetException("Max tile id allowed by font sheet larger than rows and columns of sheet");
        }

        tileWidth = info.tile_width;
        tileHeight = info.tile_height;
        ratio = ((double) tileWidth) / ((double) tileHeight);
        sheetName = info.name;
        author = info.author;
        version = info.version;
        maxTileID = info.max_tile_id;

        if(info.map != null){
            unicodeMap = new UnicodeMap(info.map.toArray(new String[0]));
        }else {
            unicodeMap = new UnicodeMap(mapDir);
        }

        id = nextID;
        nextID++;
    }

    public FontSheet(String dirName) throws FileNotFoundException, YamlException, InvalidFontSheetException, InvalidUnicodeMap {
        this(dirName + ".png", dirName + ".yml", dirName + ".map.yml");
    }

    public int defaultSize(){
        return tileHeight;
    }

    public int getHeight(){
        return tileHeight;
    }

    public int getWidth(){
        return tileWidth;
    }

    public String toString() {
        return (new StringBuilder())
                .append("name: ").append(sheetName == null ? "unknown" : sheetName)
                .append("\nid: ").append(id)
                .append("\nauthor: ").append(author == null ? "unknown" : author)
                .append("\nversion: ").append(version == null ? "unknown" : version)
                .append("\ntile width: ").append(tileWidth)
                .append("\ntile height: ").append(tileHeight)
                .append("\nratio: ").append(ratio)
                .append("\nsheet width: ").append(sheetWidth)
                .append("\nsheet height: ").append(sheetHeight)
                .append("\ncolumns: ").append(tileColumns)
                .append("\nrows: ").append(tileRows)
                .append("\nmax tile id: ").append(maxTileID)
                .toString();
    }

    public static class FontSheetInfoYAML {
        public int tile_width;
        public int tile_height;
        public String name;
        public String author;
        public String version;
        public int max_tile_id;

        public List<String> map;
    }
}
