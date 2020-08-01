package us.kosdt.arl.graphics.tile_render.render_modes;

import static us.kosdt.arl.util.math.MathUtils.EPSILON;

public abstract class WaveMode implements ModeData{

    private double angle;
    private double frequency;
    private double speed;
    private double waveDepth;
    private double scale;

    public WaveMode(double ang, double freq, double speed, double depth, double scale){
        if(freq < EPSILON){
            throw new IllegalArgumentException("The frequency cannot be set to 0");
        }
        angle = ang;
        frequency = freq;
        this.speed = speed;
        waveDepth = depth;
        this.scale = scale;
    }

    public double getAngle() {
        return angle;
    }

    public WaveMode setAngle(double angle) {
        this.angle = angle;
        return this;
    }

    public double getFrequency() {
        return frequency;
    }

    public WaveMode setFrequency(double frequency) {
        this.frequency = frequency;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public WaveMode setSpeed(double freq) {
        this.speed = freq;
        return this;
    }

    public double getWaveDepth() {
        return waveDepth;
    }

    public WaveMode setWaveDepth(double depth) {
        this.waveDepth = depth;
        return this;
    }

    public double getScale() {
        return scale;
    }

    public WaveMode setScale(double scale) {
        this.scale = scale;
        return this;
    }

    public WaveMode(){
        this(0, 1, 1, 1, 1);
    }
}
