package us.kosdt.arl.graphics.tile_render;

import org.lwjgl.BufferUtils;
import us.kosdt.arl.graphics.FontSheet;
import us.kosdt.arl.graphics.opengl.BufferObject;
import us.kosdt.arl.graphics.opengl.GLState;
import us.kosdt.arl.graphics.opengl.Shader;
import us.kosdt.arl.graphics.opengl.VertexArrayObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.ARBShaderSubroutine.*;
import static us.kosdt.arl.graphics.opengl.GLObject.bindAll;
import static us.kosdt.arl.graphics.tile_render.RenderTile.MAX_RFUNC_ID;

public abstract class FontShader {

    private static FontSheet font;
    private static boolean setFontUniforms = false;

    private static double prevTime;
    private static double startTime;

    public static final Shader FONT_SHEET_SHADER;

    static{
        FONT_SHEET_SHADER = Shader.loadGeom("font_sheet");
        int[] validRoutines = new int[MAX_RFUNC_ID];
        for (int i = 0; i < validRoutines.length; i++){
            validRoutines[i] = i;
        }
        FONT_SHEET_SHADER.setUniformSubroutines(GL_VERTEX_SHADER, validRoutines);
        prevTime = System.nanoTime() * 1e-9;
        startTime = prevTime;
    }

    private static BufferObject FONT_SHEET_VBO;

    private static final VertexArrayObject FONT_SHEET_VAO = VertexArrayObject.createVAO(() -> {

            FONT_SHEET_VBO = new BufferObject(GL_ARRAY_BUFFER);
            FONT_SHEET_VBO.bind();


            glVertexAttribPointer(0, 3, GL_FLOAT, false, 36, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 36, 12);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(2, 1, GL_FLOAT, false, 36, 24);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(3, 1, GL_FLOAT, false, 36, 28);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(4, 1, GL_FLOAT, false, 36, 32);
            glEnableVertexAttribArray(4);

        });

    private static int vertCount = -1;

    public static void setFontSheet(FontSheet fontSheet) {
        setFontUniforms = true;
        font = fontSheet;
    }

    public static FontSheet getFontSheet() {
        return font;
    }

    private static void fillVBO(RenderTile[][] vertData) {
        FONT_SHEET_VAO.bind();
        if(vertData.length == 0 || vertData[0].length == 0) {
            vertCount = 0;
        } else {
            vertCount = vertData.length * vertData[0].length;
        }
        FONT_SHEET_VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, ((long) vertCount) * 36L, GL_DYNAMIC_DRAW);

        FloatBuffer data = BufferUtils.createFloatBuffer(vertCount * 9);// 32 bytes per vertex [vec3][vec3][int][int]

        int count = 0;
        for(RenderTile[] ta : vertData) {
            for(RenderTile t : ta) {
                data.put((float) t.back.r);
                data.put((float) t.back.g);
                data.put((float) t.back.b);
                data.put((float) t.fore.r);
                data.put((float) t.fore.g);
                data.put((float) t.fore.b);

                data.put((float) t.id);
                data.put((float) t.rFunc);
                data.put((float) count);
                count++;
            }
        }
        data.flip();

        glBindBuffer(GL_ARRAY_BUFFER, FONT_SHEET_VBO.id);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }

    //TODO: Add random and delta values to be provided by the cpu.
    public static void drawTiles(RenderTile[][] vertData, int[] usedModes) {
        fillVBO(vertData);

        if(vertCount <= 0){
            return;
        }

        FONT_SHEET_SHADER.setUniform("screenWidth", vertData.length);
        if(vertData.length == 0){
            FONT_SHEET_SHADER.setUniform("screenHeight", 0);
        } else {
            FONT_SHEET_SHADER.setUniform("screenHeight", vertData[0].length);
        }

        FONT_SHEET_SHADER.setUniform("numberUsedModes", usedModes.length);
        FONT_SHEET_SHADER.setUniform("usedRenderModes", usedModes, MAX_RFUNC_ID);

        drawTiles();
    }

    public static void drawTiles() {
        if(vertCount > 0) {

            //TODO: Add random and delta values as uniforms.

            if(setFontUniforms) {
                FONT_SHEET_SHADER.setUniform("sheetCols", font.getTileColumns());
                FONT_SHEET_SHADER.setUniform("sheetRows", font.getTileRows());
                setFontUniforms = false;
            }
            double curTime = System.nanoTime() * 1e-9;
            double frameTime = curTime - prevTime;
            prevTime = curTime;
            double totalTime = curTime - startTime;

            FONT_SHEET_SHADER.setUniform("frameTime", (float) frameTime);
            FONT_SHEET_SHADER.setUniform("totalTime", (float) totalTime);

            bindAll(font.getFontSheet(), FONT_SHEET_SHADER, FONT_SHEET_VAO);
            glDrawArrays(GL_POINTS, 0, vertCount);
        }
    }

    //TODO: Create methods for updating uniforms and for updating VBOs as necessary.

    //TODO: Create draw function.
}
