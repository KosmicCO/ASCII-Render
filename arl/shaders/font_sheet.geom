#version 400

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in VS_OUT {
    vec3 gBackColor;
    vec3 gForeColor;
    int gTileID;
} gs_in[];

out vec3 fBackColor;
out vec3 fForeColor;
out vec2 fTexPos;

uniform int screenWidth;
uniform int screenHeight;
uniform int sheetCols;
uniform int sheetRows;

void main() {
    vec2 unit = vec2(2.0 / screenWidth, 2.0 / screenHeight);

    float x = floor(mod(gs_in[0].gTileID + 0.01, sheetCols));
    float y = sheetRows - ((gs_in[0].gTileID - x) / sheetCols) - 1;
    vec2 texUnit = vec2(1.0 / sheetCols, 1.0 / sheetRows);
    vec2 texPos = vec2(x / sheetCols, y / sheetRows);

    //vec2 texUnit = vec2(1, 1) * .5;
    //vec2 texPos = vec2(0, 0);

    fBackColor = gs_in[0].gBackColor;
    fForeColor = gs_in[0].gForeColor;

    gl_Position = gl_in[0].gl_Position;
    fTexPos = texPos;
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(unit * vec2(1, 0), 0, 0);
    fTexPos = texPos + (texUnit * vec2(1, 0));
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(unit * vec2(0, 1), 0, 0);
    fTexPos = texPos + (texUnit * vec2(0, 1));
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(unit * vec2(1, 1), 0, 0);
    fTexPos = texPos + (texUnit * vec2(1, 1));
    EmitVertex();

    EndPrimitive();
}