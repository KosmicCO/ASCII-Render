package us.kosdt.arl.graphics.gui.components.text.text_renderers;

import us.kosdt.arl.encoding.TileChar;
import us.kosdt.arl.encoding.UnicodeMap;
import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.graphics.Window;
import us.kosdt.arl.graphics.gui.components.text.text_editors.TextEditor;
import us.kosdt.arl.graphics.tile_render.RenderTile;
import us.kosdt.arl.util.math.Vec2d;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import static java.lang.Character.*;

public abstract class SingleTextLine implements EditorRenderer {

    public static final int DEFAULT_ANALYSIS_START_BUFFER = 30;

    private static final Map<Byte, Character> REPLACE = new HashMap();

    static{
        REPLACE.put(DIRECTIONALITY_LEFT_TO_RIGHT, 'A');
        REPLACE.put(DIRECTIONALITY_RIGHT_TO_LEFT, (char) 0x5D0); // Aleph
        REPLACE.put(DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC, (char) 0x627); // Alef
        REPLACE.put(DIRECTIONALITY_EUROPEAN_NUMBER, '0');
        REPLACE.put(DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR, '+');
        REPLACE.put(DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR, '$');
        REPLACE.put(DIRECTIONALITY_ARABIC_NUMBER, (char) 0x600); // Arabic Number Sign
        REPLACE.put(DIRECTIONALITY_COMMON_NUMBER_SEPARATOR, ',');
        REPLACE.put(DIRECTIONALITY_NONSPACING_MARK, (char) 0x300); // Grave Accent
        REPLACE.put(DIRECTIONALITY_BOUNDARY_NEUTRAL, (char) 0x5); // Enquiry
        REPLACE.put(DIRECTIONALITY_PARAGRAPH_SEPARATOR, (char) 0x2029); // Paragraph Separator
        REPLACE.put(DIRECTIONALITY_SEGMENT_SEPARATOR, (char) 0x1F); // Segment Separator
        REPLACE.put(DIRECTIONALITY_WHITESPACE, ' ');
        REPLACE.put(DIRECTIONALITY_OTHER_NEUTRALS, '!');
        
        REPLACE.put(DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING, (char) 0x202A); // LRE code
        REPLACE.put(DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING, (char) 0x202B); // RLE code
        REPLACE.put(DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE, (char) 0x202D); // LRO code
        REPLACE.put(DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE, (char) 0x202E); // RLO code
        REPLACE.put(DIRECTIONALITY_POP_DIRECTIONAL_FORMAT, (char) 0x202C); // PDF code
        REPLACE.put(DIRECTIONALITY_LEFT_TO_RIGHT_ISOLATE, (char) 0x2066); // LRI code
        REPLACE.put(DIRECTIONALITY_RIGHT_TO_LEFT_ISOLATE, (char) 0x2067); // RLI code
        REPLACE.put(DIRECTIONALITY_FIRST_STRONG_ISOLATE, (char) 0x2068); // FSI code
        REPLACE.put(DIRECTIONALITY_POP_DIRECTIONAL_ISOLATE, (char) 0x2069); // PDI code
    }

    public static char toDirectionallyEquivalentBMP(int codepoint){
        if(codepoint <= 0xFFFF){
            return (char) codepoint;
        }
        Character nCode = REPLACE.get(Character.getDirectionality(codepoint));
        if(nCode == null){
            throw new RuntimeException("Unexpected higher codepoint directionality");
        }
        return nCode;
    }

    private static final int HIGHLIGHT_NON_CURRENT = 0;
    private static final int HIGHLIGHT_CURRENT_CURSOR = 1;
    private static final int HIGHLIGHT_CURRENT_SECTION = 2;

    private static final double HIGHLIGHTING_DISTANCE_SQUARED = .25; // .5 squared

    public static final int HIGHLIGHT_LEVEL_NONE = 0;
    public static final int HIGHLIGHT_LEVEL_CURSOR = 1;
    public static final int HIGHLIGHT_LEVEL_SECTION = 2;

    private TextEditor editor;
    private List<Integer> string;
    private List<RenderTile> rendered;
    private List<Integer> indices;

    private int analysisStart;
    private int analysisStartBuffer;
    
    private Vec2d highlightPos;
    private Integer highlightStart;
    private Integer highlightEnd;

    boolean validCache;
    int[] cachedHighlightSelection;

    private int start;
    private int size;

    private boolean leftToRight;

    public SingleTextLine(TextEditor editor, int start, int size, boolean leftToRight){
        this(editor, start, size, leftToRight, DEFAULT_ANALYSIS_START_BUFFER);
    }

    public SingleTextLine(List<Integer> string, int start, int size, boolean leftToRight){
        this(string, start, size, leftToRight, DEFAULT_ANALYSIS_START_BUFFER);
    }

    public SingleTextLine(TextEditor editor, int start, int size, boolean leftToRight, int analysisStartBuffer){
        if(editor == null){
            throw new IllegalArgumentException("editor cannot be null");
        }
        this.editor = editor;
        this.start = start;
        this.size = size;
        this.leftToRight = leftToRight;
        this.analysisStartBuffer = analysisStartBuffer;

        string = null;
        rendered = null;
        indices = null;
        analysisStart = 0;
        
        highlightPos = null;
        highlightStart = null;
        highlightEnd = null;

        validCache = false;
        cachedHighlightSelection = null;
        
        update();
    }

    public SingleTextLine(List<Integer> string, int start, int size, boolean leftToRight, int analysisStartBuffer){
        if(string == null){
            throw new IllegalArgumentException("string cannot be null");
        }
        this.string = string;
        this.start = start;
        this.size = size;
        this.leftToRight = leftToRight;
        this.analysisStartBuffer = analysisStartBuffer;

        editor = null;
        rendered = null;
        indices = null;
        analysisStart = 0;

        highlightPos = null;
        highlightStart = null;
        highlightEnd = null;

        validCache = false;
        cachedHighlightSelection = null;
        
        update();
    }

    protected List<RenderTile> getRendered() {
        return rendered;
    }

    protected List<Integer> getIndices() {
        return indices;
    }

    public int getViewStart() {
        return start;
    }

    public int getViewSize() {
        return size;
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    private void update() {
        rendered = new ArrayList();
        indices = new ArrayList();
        if(editor == null){
            (new Generator(string)).update();
        }else{
            (new Generator(editor)).update();
        }
    }

    public void updateFromEditor() {
        if(editor != null){
            if(editor.earliestChange() <= analysisStart){
                analysisStart = 0;
            }
            editor.resetChanged();
            update();
        }
    }

    public void setString(List<Integer> string) {
        if(string == null){
            throw new IllegalArgumentException("string cannot be null");
        }
        analysisStart = 0;
        this.string = string;
        editor = null;
        update();
    }

    public void setEditor(TextEditor editor) {
        if(editor == null){
            throw new IllegalArgumentException("editor cannot be null");
        }
        analysisStart = 0;
        this.editor = editor;
        string = null;
        update();
    }

    public void setViewStart(int index){
        if(index < 0) {
            throw new IllegalArgumentException("index must be greater or equal to 0");
        }
        this.start = index;
        if(start < analysisStart){
            analysisStart = 0;
        }
        update();
    }

    public void setViewSize(int size){
        this.size = size;
        update();
    }

    public boolean isHighlighting() {
        return highlightPos != null;
    }

    public boolean placedCursor() {
        return highlightStart != null && highlightEnd == null;
    }

    public boolean highlightedSection() {
        return highlightEnd != null;
    }

    private int getHighlightState(){
        if(!isHighlighting()){
            return 0;
        }
        if(placedCursor()){
            return 1;
        }
        return 2; // isHighlighting and !placedCursor implies highlightedSection
    }

    public abstract boolean contains(Vec2d vec);

    public abstract int getIndexFromMouse(Vec2d mouse);


    @Override
    public boolean takePress(boolean pressed, Vec2d mouse) {
        if(!contains(mouse)){
            return false;
        }
        validCache = false;
        switch (getHighlightState()){
            case HIGHLIGHT_NON_CURRENT:
                if(pressed){
                    highlightPos = mouse;
                    highlightStart = getIndexFromMouse(mouse);
                    highlightEnd = null;
                }else{
                    return false;
                }
                break;
            case HIGHLIGHT_CURRENT_CURSOR:
            case HIGHLIGHT_CURRENT_SECTION:
                if(pressed){
                    highlightPos = mouse;
                    highlightStart = getIndexFromMouse(mouse);
                    highlightEnd = null;
                }else{
                    highlightPos = null;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean takeMouse(Vec2d mouse) {
        validCache = false;
        switch (getHighlightState()){
            case HIGHLIGHT_CURRENT_CURSOR:
                if(highlightPos.sub(mouse).lengthSquared() > HIGHLIGHTING_DISTANCE_SQUARED){
                    highlightEnd = getIndexFromMouse(mouse);
                }
                break;
            case HIGHLIGHT_CURRENT_SECTION:
                highlightEnd = getIndexFromMouse(mouse);
                break;
        }
        return false;
    }

    @Override
    public int[] getHighlighted() {
        if(validCache){
            return cachedHighlightSelection;
        }
        if(placedCursor()){
            int[] ret = new int[1];
            ret[0] = highlightStart;
            cachedHighlightSelection = ret;
            validCache = true;
            return ret;
        }
        if(highlightedSection()){
            int[] ret = new int[2];
            int rev = highlightStart > highlightEnd ? 1 : 0;
            ret[rev] = highlightStart;
            ret[1 - rev] = highlightEnd;
            cachedHighlightSelection = ret;
            validCache = true;
            return ret;
        }
        cachedHighlightSelection = new int[0];
        validCache = true;
        return cachedHighlightSelection;
    }

    @Override
    public void clearHighlighted(){
        validCache = false;
        highlightPos = null;
        highlightStart = null;
        highlightEnd = null;
    }

    @Override
    public void setCursor(int index) {
        validCache = false;
        highlightPos = null;
        highlightStart = index;
        highlightEnd = null;
    }

    @Override
    public void setHighlight(int startIndex, int endIndex) {
        validCache = false;
        highlightPos = null;
        highlightStart = startIndex;
        highlightEnd = endIndex;
    }

    protected int getHighlightLevel(int index) {
        int[] highlight = getHighlighted();
        if(0 > index || index > indices.size()){
            throw new IndexOutOfBoundsException("index is out of bounds of the length of the rendered string");
        }
        switch (highlight.length){
            case 0:
                return HIGHLIGHT_LEVEL_NONE;
            case 1:
                if(index == indices.size()){
                    return index == highlight[0] ? HIGHLIGHT_LEVEL_CURSOR : HIGHLIGHT_LEVEL_NONE;
                }
                return indices.get(index) == highlight[0] ? HIGHLIGHT_LEVEL_CURSOR : HIGHLIGHT_LEVEL_NONE;
            case 2:
                if(index == indices.size()){
                    return index == highlight[1] ? HIGHLIGHT_LEVEL_SECTION : HIGHLIGHT_LEVEL_NONE;
                }
                int ind = indices.get(index);
                return highlight[0] <= ind && ind <= highlight[1] ? HIGHLIGHT_LEVEL_SECTION : HIGHLIGHT_LEVEL_NONE;
        }
        return HIGHLIGHT_LEVEL_NONE;
    }

    private class Generator {

        IntUnaryOperator get;
        int stringSize;

        Generator(TextEditor e) {
            stringSize = e.size();
            get = e::codeAt;
        }

        Generator(List<Integer> s) {
            stringSize = s.size();
            get = s::get;
        }

        void addChar(RenderTile tile, int index) {
            if(rendered.size() < size) {
                rendered.add(tile);
                indices.add(index);
            }
        }

        void addCodepoint(int codepoint, int index, boolean flipped){
            if(rendered.size() >= size){
                return;
            }
            TileChar tc = Window.window().getUnicodeMap().mapCodePoint(codepoint);
            if(tc == null){
                return;
            }
            if(tc.doub){
                if(leftToRight ^ flipped){
                    addChar(new RenderTile(tc.id, Color.BLACK, Color.CLEAR, RenderTile.RFUNC_NONE, Color.CLEAR, flipped), index);
                    addChar(new RenderTile(tc.doubId(), Color.BLACK, Color.CLEAR, RenderTile.RFUNC_NONE, Color.CLEAR, flipped), index);
                }else {
                    addChar(new RenderTile(tc.doubId(), Color.BLACK, Color.CLEAR, RenderTile.RFUNC_NONE, Color.CLEAR, flipped), index);
                    addChar(new RenderTile(tc.id, Color.BLACK, Color.CLEAR, RenderTile.RFUNC_NONE, Color.CLEAR, flipped), index);
                }
            }else{
                addChar(new RenderTile(tc.id, Color.BLACK, Color.CLEAR, RenderTile.RFUNC_NONE, Color.CLEAR, flipped), index);
            }
        }

        void addLast(Integer prevStrong, int index){
            if(prevStrong != null){
                if(index - 1 >= start) {
                    addCodepoint(prevStrong, index - 1, Character.isMirrored(prevStrong) && !leftToRight);
                }
            }
        }

        void update() {
            int i = analysisStart;
            Integer prevStrong = null;

            while (i < stringSize && rendered.size() < size){
                int c = get.applyAsInt(i);
                byte directionality = Character.getDirectionality(c);

                switch (directionality) {
                    case DIRECTIONALITY_EUROPEAN_NUMBER:
                    case DIRECTIONALITY_LEFT_TO_RIGHT:
                        if (leftToRight) {
                            if(i <= start - analysisStartBuffer) {
                                analysisStart = i;
                            }
                            addLast(prevStrong, i);
                            prevStrong = c;
                            i++;
                        } else {
                            prevStrong = null;
                            i = bidiCheck(i);
                        }
                        break;
                    case DIRECTIONALITY_RIGHT_TO_LEFT:
                    case DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
                        if (!leftToRight) {
                            if(i <= start - analysisStartBuffer) {
                                analysisStart = i;
                            }
                            addLast(prevStrong, i);
                            prevStrong = c;
                            i++;
                        } else {
                            prevStrong = null;
                            i = bidiCheck(i);
                        }
                        break;
                    default:
                        prevStrong = null;
                        i = bidiCheck(i);
                }
            }
            addLast(prevStrong, i);
        }

        int bidiCheck(int triggerIndex) {
            int isolateStack = 0;
            int formatStack = 0;

            int s = Math.max(0, triggerIndex - 1);
            int e = s + 1;
            boolean finish = false;
            boolean analyze = false;

            while(e < stringSize && !finish) {
                int directionality = Character.getDirectionality(get.applyAsInt(e));
                switch (directionality){
                    case DIRECTIONALITY_LEFT_TO_RIGHT_ISOLATE:
                    case DIRECTIONALITY_RIGHT_TO_LEFT_ISOLATE:
                    case DIRECTIONALITY_FIRST_STRONG_ISOLATE:
                        isolateStack++;
                        analyze = true;
                        break;
                    case DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING:
                    case DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
                    case DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE:
                    case DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
                        if(isolateStack <= 0){
                            formatStack++;
                        }
                        analyze = true;
                        break;
                    case DIRECTIONALITY_POP_DIRECTIONAL_ISOLATE:
                        if(isolateStack > 0){
                            isolateStack--;
                        }
                        break;
                    case DIRECTIONALITY_POP_DIRECTIONAL_FORMAT:
                        if(isolateStack <= 0 && formatStack > 0){
                            formatStack--;
                        }
                        break;
                    case DIRECTIONALITY_LEFT_TO_RIGHT:
                        if(leftToRight){
                            if(isolateStack == 0 && formatStack == 0) {
                                finish = true;
                            }
                        }else{
                            analyze = true;
                        }
                        break;
                    case DIRECTIONALITY_RIGHT_TO_LEFT:
                    case DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
                        if(!leftToRight){
                            if(isolateStack == 0 && formatStack == 0) {
                                finish = true;
                            }
                        }else{
                            analyze = true;
                        }
                        break;
                    case DIRECTIONALITY_EUROPEAN_NUMBER:
                    case DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR:
                    case DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR:
                    case DIRECTIONALITY_COMMON_NUMBER_SEPARATOR:
                    case DIRECTIONALITY_NONSPACING_MARK:
                    case DIRECTIONALITY_BOUNDARY_NEUTRAL:
                        analyze |= !leftToRight;
                        break;
                    case DIRECTIONALITY_ARABIC_NUMBER:
                        analyze |= leftToRight;
                        break;
                }
                e++;
            }

            boolean strongEnd = false;
            switch(Character.getDirectionality(get.applyAsInt(e - 1))){
                case DIRECTIONALITY_LEFT_TO_RIGHT:
                    strongEnd = leftToRight;
                    break;
                case DIRECTIONALITY_RIGHT_TO_LEFT:
                case DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
                    strongEnd = !leftToRight;
                    break;
            }

            if(e <= start){
                return e - (strongEnd ? 1 : 0);
            }

            if(analyze){
                Integer[] inds = new Integer[e - s];
                char[] chars = new char[e - s];
                for (int i = s; i < e; i++){
                    inds[i - s] = i;
                    chars[i - s] = toDirectionallyEquivalentBMP(get.applyAsInt(i));
                }

                Bidi reorder = new Bidi(chars, 0, null, 0, chars.length, leftToRight ? Bidi.DIRECTION_LEFT_TO_RIGHT : Bidi.DIRECTION_RIGHT_TO_LEFT);
                byte[] levels = new byte[e - s];
                for (int i = 0; i < reorder.getRunCount(); i++){
                    byte level = (byte) Math.min(125, reorder.getRunLevel(i));
                    for (int j = reorder.getRunStart(i); j < reorder.getRunLimit(i); j++){
                        levels[j] = level;
                    }
                }

                Bidi.reorderVisually(levels, 0, inds, 0, e - s);

                int i = 0;
                int inS = Math.max(s, start);
                while(i < e - inS - (strongEnd ? 1 : 0) && rendered.size() < size) {
                    int arrayIndex = leftToRight ? i + inS - s : inds.length - i - inS + s - 1;
                    int ind = inds[arrayIndex];
                    int codepoint = get.applyAsInt(ind);
                    addCodepoint(codepoint, ind, Character.isMirrored(codepoint) && levels[ind - inS] % 2 == 1);
                    i++;
                }
            }else{
                int i = Math.max(s, start);
                while(i < e - (strongEnd ? 1 : 0) && rendered.size() < size) {
                    int codepoint = get.applyAsInt(i);
                    addCodepoint(codepoint, i, Character.isMirrored(codepoint) && !leftToRight);
                    i++;
                }
            }
            return e - (strongEnd ? 1 : 0);
        }
    }
}
