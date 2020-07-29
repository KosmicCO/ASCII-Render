package us.kosdt.arl.graphics.tile_render.render_modes.subfunctions;

import us.kosdt.arl.graphics.opengl.Shader;

import java.util.Random;

public class SimplexNoiseTile {

    private static final SimplexNoiseTile noise = new SimplexNoiseTile(new Random());

    private static final float F2 = (float)(0.5 * (Math.sqrt(3.0) - 1.0));
    private static final float G2 = (float) ((3.0 - Math.sqrt(3.0)) / 6.0);
    private static final float F3 = (float) (1.0 / 3.0);
    private static final float G3 = (float) (1.0 / 6.0);
    private static final float F4 = (float) ((Math.sqrt(5.0) - 1.0) / 4.0);
    private static final float G4 = (float) ((5.0 - Math.sqrt(5.0)) / 20.0);

    private static final float[] grad3 = {
            1, 1, 0,
            -1, 1, 0,
            1, -1, 0,
            -1, -1, 0,

            1, 0, 1,
            -1, 0, 1,
            1, 0, -1,
            -1, 0, -1,

            0, 1, 1,
            0, -1, 1,
            0, 1, -1,
            0, -1, -1
    };

    private static final float[] grad4 = {
            0, 1, 1, 1,
            0, 1, 1, -1,
            0, 1, -1, 1,
            0, 1, -1, -1,

            0, -1, 1, 1,
            0, -1, 1, -1,
            0, -1, -1, 1,
            0, -1, -1, -1,

            1, 0, 1, 1,
            1, 0, 1, -1,
            1, 0, -1, 1,
            1, 0, -1, -1,

            -1, 0, 1, 1,
            -1, 0, 1, -1,
            -1, 0, -1, 1,
            -1, 0, -1, -1,

            1, 1, 0, 1,
            1, 1, 0, -1,
            1, -1, 0, 1,
            1, -1, 0, -1,

            -1, 1, 0, 1,
            -1, 1, 0, -1,
            -1, -1, 0, 1,
            -1, -1, 0, -1,

            1, 1, 1, 0,
            1, 1, -1, 0,
            1, -1, 1, 0,
            1, -1, -1, 0,

            -1, 1, 1, 0,
            -1, 1, -1, 0,
            -1, -1, 1, 0,
            -1, -1, -1, 0
    };

    private final int[] perm = new int[512];
    private final int[] permMod12 = new int[512];

    private SimplexNoiseTile(Random random) {
        int[] p = new int[256];
        for (int i = 0; i < 256; i++){
            p[i] = i;
        }

        for (int i = 255; i > 0; i--){
            int index = random.nextInt(i + 1);
            int temp = p[index];
            p[index] = p[i];
            p[i] = temp;
        }

        for (int i = 0; i < 512; i++) {
            perm[i] = p[i & 255];
            permMod12[i] = perm[i] % 12;
        }
    }

    public static void setUniforms(Shader shader) {
        shader.setUniform("perm", noise.perm, 512);
        shader.setUniform("permMod12", noise.permMod12, 512);


        shader.setUniformV3("grad3", grad3, 12);
        shader.setUniformV4("grad4", grad4, 32);

        shader.setUniform("F2", F2);
        shader.setUniform("G2", G2);
        shader.setUniform("F3", F3);
        shader.setUniform("G3", G3);
        shader.setUniform("F4", F4);
        shader.setUniform("G4", G4);
    }
}
