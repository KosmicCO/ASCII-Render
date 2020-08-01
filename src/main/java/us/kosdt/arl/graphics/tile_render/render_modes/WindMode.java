package us.kosdt.arl.graphics.tile_render.render_modes;

import org.joml.Matrix2d;
import us.kosdt.arl.graphics.opengl.Shader;

public class WindMode extends WaveMode{

    public WindMode(double ang, double freq, double speed, double depth, double scale){
        super(ang, freq, speed, depth, scale);
    }

    public WindMode(){
        super();
    }

    @Override
    public void setUniforms(Shader shader) {
        double cosA = Math.cos(getAngle());
        double sinA = Math.sin(getAngle());
        shader.setUniform("windMode_invDir", new Matrix2d(cosA, -sinA, sinA, cosA).scale(getFrequency()));
        shader.setUniform("windMode_speed", (float) getSpeed());
        shader.setUniform("windMode_wavDep", (float) getWaveDepth());
        shader.setUniform("windMode_scale", (float) getScale());
    }
}
