#version 400
#extension GL_ARB_explicit_uniform_location : require

layout (location = 0) in vec3 aBackColor;
layout (location = 1) in vec3 aForeColor;
layout (location = 2) in float aTileID;
layout (location = 3) in float aRenderID;
layout (location = 4) in float count;

out VS_OUT {
    vec3 gBackColor;
    vec3 gForeColor;
    int gTileID;
} vs_out;

out vec3 geBackColor;
out vec3 geForeColor;
out int geTileID;

uniform int screenWidth;
uniform int screenHeight;
uniform int numberUsedModes;
uniform int usedRenderModes[2]; // the modes used for the given batch, make sure to change the max number as necessary
// uniform int sheetCols;
// uniform int sheetRows;
uniform float frameTime;
uniform float totalTime;

subroutine vec3[2] colorShader(vec3 back, vec3 fore, float x, float y);

subroutine uniform colorShader colorShaders[2]; // NOTE: Remember to shift indices by -1.

vec3[2] pack(vec3 back, vec3 fore) {
    vec3 cols[2] = vec3[2](back, fore);
    return cols;
}

layout(index = 0) subroutine(colorShader) vec3[2] sinColor(vec3 back, vec3 fore, float x, float y){
    float m = 0.5 * sin(totalTime) + 0.5;
    return pack(m * back, m * fore);
}

layout(index = 1) subroutine(colorShader) vec3[2] waveColor(vec3 back, vec3 fore, float x, float y) {
    float m = 0.5 * sin(totalTime + x * 4.0) + 0.5;
    return pack(m * back, m * fore);
}

void main() {
    float x = floor(mod(count + 0.01, 1.0 * screenWidth));
    float y = (count - x) / screenWidth;
    float xPos = (x / (screenWidth) - .5) * 2;
    float yPos = ((y / screenHeight) - .5) * 2;
    gl_Position = vec4(xPos, yPos, 0, 1);

    vec3 cols[2] = pack(aBackColor, aForeColor);

    for(int i = 0; i < numberUsedModes; i++) {
        if(usedRenderModes[i] == int(aRenderID + 0.01)){
            cols = colorShaders[usedRenderModes[i] - 1](aBackColor, aForeColor, xPos, yPos);
        }
    }

    vs_out.gBackColor = cols[0];
    vs_out.gForeColor = cols[1];
    vs_out.gTileID = int(aTileID + 0.01);
}

//TODO: Implement render mods to be applied.