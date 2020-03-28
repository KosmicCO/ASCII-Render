package us.kosdt.arl.graphics.tile_render.render_modes;

import org.joml.Matrix2d;
import us.kosdt.arl.graphics.opengl.Shader;

public class WaterMode extends WaveMode{

    public WaterMode(double ang, double freq, double speed, double depth, double scale){
        super(ang, freq, speed, depth, scale);
    }

    public WaterMode(){
        super();
    }

    @Override
    public void setUniforms(Shader shader) {
        double cosA = Math.cos(getAngle());
        double sinA = Math.sin(getAngle());
        shader.setUniform("waterMode_invDir", new Matrix2d(cosA, -sinA, sinA, cosA).scale(getFrequency()));
        shader.setUniform("waterMode_speed", (float) getSpeed());
        shader.setUniform("waterMode_wavDep", (float) getWaveDepth());
        shader.setUniform("waterMode_scale", (float) getScale());
    }
}
