package us.kosdt.arl.graphics.tile_render;

import org.lwjgl.BufferUtils;
import us.kosdt.arl.graphics.FontSheet;
import us.kosdt.arl.graphics.opengl.BufferObject;
import us.kosdt.arl.graphics.opengl.Shader;
import us.kosdt.arl.graphics.opengl.VertexArrayObject;
import us.kosdt.arl.graphics.tile_render.render_modes.ModeData;
import us.kosdt.arl.graphics.tile_render.render_modes.WaterMode;
import us.kosdt.arl.graphics.tile_render.render_modes.WindMode;
import us.kosdt.arl.graphics.tile_render.render_modes.subfunctions.SimplexNoiseTile;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
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
        glBindFragDataLocation(FONT_SHEET_SHADER.id, 0, "fragColor");
        resetStartTime();

        SimplexNoiseTile.setUniforms(FONT_SHEET_SHADER);

        // Make sure each is at least initialized to something

        setRenderModeUniforms(new WaterMode());
        setRenderModeUniforms(new WindMode());
    }

    private static BufferObject FONT_SHEET_VBO;

    private static final int STRIDE = 52;
    private static final int FLOATS_PER_VERTEX = 13;

    private static final VertexArrayObject FONT_SHEET_VAO = VertexArrayObject.createVAO(() -> {

            FONT_SHEET_VBO = new BufferObject(GL_ARRAY_BUFFER);
            FONT_SHEET_VBO.bind();


            glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0); // back col
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, STRIDE, 12); // fore col
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(2, 1, GL_FLOAT, false, STRIDE, 24); // id
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(3, 1, GL_FLOAT, false, STRIDE, 28); // rFunc and flip
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(4, 1, GL_FLOAT, false, STRIDE, 32); // count
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(5, 4, GL_FLOAT, false, STRIDE, 36); // over col
            glEnableVertexAttribArray(5);
        });

    private static int vertCount = -1;

    public static void setFont(FontSheet fontSheet) {
        setFontUniforms = true;
        font = fontSheet;
    }

    public static void setRenderModeUniforms(ModeData data) {
        data.setUniforms(FONT_SHEET_SHADER);
    }

    public static FontSheet getFont() {
        return font;
    }

    public static void resetStartTime(){
        prevTime = System.nanoTime() * 1e-9;
        startTime = prevTime;
    }

    private static void fillVBO(RenderTile[][] vertData) {
        FONT_SHEET_VAO.bind();
        if (vertData.length == 0 || vertData[0].length == 0) {
            vertCount = 0;
        } else {
            vertCount = vertData.length * vertData[0].length;
        }
        FONT_SHEET_VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, ((long) vertCount) * ((long) STRIDE), GL_DYNAMIC_DRAW);

        FloatBuffer data = BufferUtils.createFloatBuffer(vertCount * FLOATS_PER_VERTEX); // 52 bytes per vertex [vec3][vec3][int][int][int][vec4]

        int count = 0;
        for (int y = 0; y < vertData[0].length; y++) {
            for (int x = 0; x < vertData.length; x++) {
                RenderTile t = vertData[x][y];
                data.put((float) t.back.r);
                data.put((float) t.back.g);
                data.put((float) t.back.b);
                data.put((float) t.fore.r);
                data.put((float) t.fore.g);
                data.put((float) t.fore.b);

                data.put((float) t.id);
                data.put((float) ((t.rFunc << 1) | (t.flip ? 1 : 0)));
                data.put((float) count);

                data.put((float) t.over.r);
                data.put((float) t.over.g);
                data.put((float) t.over.b);
                data.put((float) t.over.a);
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
                FONT_SHEET_SHADER.setUniform("sheetCols", font.tileColumns);
                FONT_SHEET_SHADER.setUniform("sheetRows", font.tileRows);
                setFontUniforms = false;
            }
            double curTime = System.nanoTime() * 1e-9;
            double frameTime = curTime - prevTime;
            prevTime = curTime;
            double totalTime = curTime - startTime;

            FONT_SHEET_SHADER.setUniform("frameTime", (float) frameTime);
            FONT_SHEET_SHADER.setUniform("totalTime", (float) totalTime);

            bindAll(font.fontSheet, FONT_SHEET_SHADER, FONT_SHEET_VAO);
            glDrawArrays(GL_POINTS, 0, vertCount);
        }
    }

    //TODO: Create methods for updating uniforms and for updating VBOs as necessary.

    //TODO: Create draw function.
}
