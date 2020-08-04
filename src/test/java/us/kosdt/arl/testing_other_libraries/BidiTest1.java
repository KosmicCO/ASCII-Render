package us.kosdt.arl.testing_other_libraries;

import java.text.Bidi;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.*;

public class BidiTest1 {

    private static final Map<Byte, Character> REPLACE = new HashMap<>();

    static{
        REPLACE.put(DIRECTIONALITY_UNDEFINED, (char) 0x300); // Same as non-spacing mark since these should be ignored.
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
    }

    public static char toDirectionalityImportant(int codepoint){
        if(codepoint <= 0xFFFF){
            return (char) codepoint;
        }
        Character nCode = REPLACE.get(Character.getDirectionality(codepoint));
        if(nCode == null){
            throw new RuntimeException("Unexpected higher codepoint directionality");
        }
        return nCode;
    }

    public static void main(String[] args) {
        System.out.println(Character.getDirectionality(0x20000));
        int[] codepoints = {'t', 'e', 's', 't', ' ', 0x2067, 0x5D0, ' ', 0x5D1, '!', 0x2069, ' ', 0x20000, '.'};
        Integer[] indices = new Integer[codepoints.length];
        for (int i = 0; i < indices.length; i++){
            indices[i] = i;
        }
        System.out.println(Arrays.toString(codepoints));
        char[] chars = new char[codepoints.length];
        for (int i = 0; i < chars.length; i++){
            chars[i] = toDirectionalityImportant(codepoints[i]);
        }

        Bidi bd = new Bidi(chars, 0, null, 0, chars.length, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        System.out.println(bd.getRunCount());
        byte[] ilevels = new byte[codepoints.length];
        for (int i = 0; i < bd.getRunCount(); i++){
            System.out.println("s: " + bd.getRunStart(i) + ", e: " + bd.getRunLimit(i) + ", l:" + bd.getRunLevel(i));
        }

        for (int i = 0; i < bd.getRunCount(); i++){
            byte l = (byte) bd.getRunLevel(i);
            for (int j = bd.getRunStart(i); j < bd.getRunLimit(i); j++){
                ilevels[j] = l;
            }
        }

        System.out.println(Arrays.toString(ilevels));
        Bidi.reorderVisually(ilevels, 0, indices, 0, indices.length);
        System.out.println(Arrays.toString(indices));
    }
}
