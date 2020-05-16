#version 400
#extension GL_ARB_explicit_uniform_location : require

layout (location = 0) in vec3 aBackColor;
layout (location = 1) in vec3 aForeColor;
layout (location = 2) in float aTileID;
layout (location = 3) in float aRenderID;
layout (location = 4) in float count;
layout (location = 5) in vec4 aOverColor;

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
uniform int usedRenderModes[3]; // the modes used for the given batch, make sure to change the max number as necessary
uniform float frameTime;
uniform float totalTime;

uniform int perm[512];
uniform int permMod12[512];

uniform vec3 grad3[12];
uniform vec4 grad4[32];

uniform float F2;
uniform float G2;
uniform float F3;
uniform float G3;
uniform float F4;
uniform float G4;

const float pi = 3.14159265359;

subroutine vec3[2] colorShader(vec3 back, vec3 fore, vec2 pos);

subroutine uniform colorShader colorShaders[3]; // NOTE: Remember to shift indices by -1.

vec3[2] pack(vec3 back, vec3 fore) {
    vec3 cols[2] = vec3[2](back, fore);
    return cols;
}

float simplexNoise2d(float xin, float yin) {
    float n0, n1, n2;
    float s = (xin + yin) * F2;
    int i = int(floor(xin + s));
    int j = int(floor(yin + s));
    float t = (i + j) * G2;

    float X0 = i - t; // Unskew the cell origin back to (x, y) space
    float Y0 = j - t;
    float x0 = xin - X0; // The (x, y) distance from the cell origin
    float y0 = yin - Y0;

    // For the 2D case, the simplex shape is an equilateral triangle.
    // Determine which simplex we are in

    int i1, j1; // Offsets for second (middle) corner of simplex in (i, j) coords

    i1 = x0 > y0 ? 1 : 0;
    j1 = x0 > y0 ? 0 : 1;

    float x1 = x0 - i1 + G2;
    float y1 = y0 - j1 + G2;
    float x2 = x0 - 1.0 + 2.0 * G2;
    float y2 = y0 - 1.0 + 2.0 * G2;

    int ii = i & 255;
    int jj = j & 255;
    int gi0 = permMod12[ii + perm[jj]];
    int gi1 = permMod12[ii + i1 + perm[jj + j1]];
    int gi2 = permMod12[ii + 1 + perm[jj + 1]];

    float t0 = 0.5 - x0 * x0 - y0 * y0;
    n0 = t0 < 0 ? 0.0 : (t0 * t0 * t0 * t0 * dot(grad3[gi0].xy, vec2(x0, y0)));

    float t1 = 0.5 - x1 * x1 - y1 * y1;
    n1 = t1 < 0 ? 0.0 : (t1 * t1 * t1 * t1 * dot(grad3[gi1].xy, vec2(x1, y1)));

    float t2 = 0.5 - x2 * x2 - y2 * y2;
    n2 = t2 < 0 ? 0.0 : (t2 * t2 * t2 * t2 * dot(grad3[gi2].xy, vec2(x2, y2)));

    return 70.0 * (n0 + n1 + n2);
}

float simplexNoise4d(float x, float y, float z, float w) {
    float n0, n1, n2, n3, n4; // Noise contributions from the five corners
    // Skew the (x, y, z, w) space to determine which cell of 24 simplices we're in

    float s = (x + y + z + w) * F4;
    int i = int(floor(x + s));
    int j = int(floor(y + s));
    int k = int(floor(z + s));
    int l = int(floor(w + s));

    float t = (i + j + k + l) * G4;

    float X0 = i - t; // Unskew the cell origin back to (x, y, z, w) space
    float Y0 = j - t;
    float Z0 = k - t;
    float W0 = l - t;

    float x0 = x - X0; // The x, y, z, w distances from the cell origin
    float y0 = y - Y0;
    float z0 = z - Z0;
    float w0 = w - W0;

    // For the 4D case, the simplex is a 4D shape
    // To find out which of the 24 possible simplices we're in, we need to determine the magnitude ordering of x0, y0, z0, and w0.
    // Sic pair-wise comparisons are performed between each possible pair of the four coordinates, and the results are used to rank the numbers.

    int rankx = (x0 > y0 ? 1 : 0) + (x0 > z0 ? 1 : 0) + (x0 > w0 ? 1 : 0);
    int ranky = (x0 <= y0 ? 1 : 0) + (y0 > z0 ? 1 : 0) + (y0 > w0 ? 1 : 0);
    int rankz = (x0 <= z0 ? 1 : 0) + (y0 <= z0 ? 1 : 0) + (z0 > w0 ? 1 : 0);
    int rankw = (x0 <= w0 ? 1 : 0) + (y0 <= w0 ? 1 : 0) + (z0 <= w0 ? 1 : 0);

    int i1, j1, k1, l1;
    int i2, j2, k2, l2;
    int i3, j3, k3, l3;

    i1 = rankx >= 3 ? 1 : 0;
    j1 = ranky >= 3 ? 1 : 0;
    k1 = rankz >= 3 ? 1 : 0;
    l1 = rankw >= 3 ? 1 : 0;

    i2 = rankx >= 2 ? 1 : 0;
    j2 = ranky >= 2 ? 1 : 0;
    k2 = rankz >= 2 ? 1 : 0;
    l2 = rankw >= 2 ? 1 : 0;

    i3 = rankx >= 1 ? 1 : 0;
    j3 = ranky >= 1 ? 1 : 0;
    k3 = rankz >= 1 ? 1 : 0;
    l3 = rankw >= 1 ? 1 : 0;

    float x1 = x0 - i1 + G4; // Offsets for second corner in (x,y,z,w) coords
    float y1 = y0 - j1 + G4;
    float z1 = z0 - k1 + G4;
    float w1 = w0 - l1 + G4;
    float x2 = x0 - i2 + 2.0 * G4; // Offsets for third corner in (x,y,z,w) coords
    float y2 = y0 - j2 + 2.0 * G4;
    float z2 = z0 - k2 + 2.0 * G4;
    float w2 = w0 - l2 + 2.0 * G4;
    float x3 = x0 - i3 + 3.0 * G4; // Offsets for fourth corner in (x,y,z,w) coords
    float y3 = y0 - j3 + 3.0 * G4;
    float z3 = z0 - k3 + 3.0 * G4;
    float w3 = w0 - l3 + 3.0 * G4;
    float x4 = x0 - 1.0 + 4.0 * G4; // Offsets for last corner in (x,y,z,w) coords
    float y4 = y0 - 1.0 + 4.0 * G4;
    float z4 = z0 - 1.0 + 4.0 * G4;
    float w4 = w0 - 1.0 + 4.0 * G4;

    // Work out the hashed gradient indices of the five simplex corners
    int ii = i & 255;
    int jj = j & 255;
    int kk = k & 255;
    int ll = l & 255;
    int gi0 = perm[ii + perm[jj + perm[kk + perm[ll]]]] % 32;
    int gi1 = perm[ii + i1 + perm[jj + j1 + perm[kk + k1 + perm[ll + l1]]]] % 32;
    int gi2 = perm[ii + i2 + perm[jj + j2 + perm[kk + k2 + perm[ll + l2]]]] % 32;
    int gi3 = perm[ii + i3 + perm[jj + j3 + perm[kk + k3 + perm[ll + l3]]]] % 32;
    int gi4 = perm[ii + 1 + perm[jj + 1 + perm[kk + 1 + perm[ll + 1]]]] % 32;

    float t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
    n0 = t0 < 0 ? 0.0 : (t0 * t0 * t0 * t0 * dot(grad4[gi0], vec4(x0, y0, z0, w0)));

    float t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
    n1 = t1 < 0 ? 0.0 : (t1 * t1 * t1 * t1 * dot(grad4[gi1], vec4(x1, y1, z1, w1)));

    float t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
    n2 = t2 < 0 ? 0.0 : (t2 * t2 * t2 * t2 * dot(grad4[gi2], vec4(x2, y2, z2, w2)));

    float t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
    n3 = t3 < 0 ? 0.0 : (t3 * t3 * t3 * t3 * dot(grad4[gi3], vec4(x3, y3, z3, w3)));

    float t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
    n4 = t4 < 0 ? 0.0 : (t4 * t4 * t4 * t4 * dot(grad4[gi4], vec4(x4, y4, z4, w4)));

    return 27.0 * (n0 + n1 + n2 + n3 + n4);
}

vec3 overScale(float scale, vec3 color) {
    return (scale >= 1) ? (2 - scale) * color + (scale - 1) * vec3(1, 1, 1) : scale * color;
}

uniform mat2    waterMode_invDir;
uniform float   waterMode_speed;
uniform float   waterMode_wavDep;
uniform float   waterMode_scale;

layout(index = 0) subroutine(colorShader) vec3[2] sinColor(vec3 back, vec3 fore, vec2 pos){ // WaterMode
    vec2 poll = waterMode_invDir * vec2(pos.x, pos.y);
    float waveDisp = simplexNoise4d(poll.x * 0.25, poll.y * 0.25, totalTime * 0.25, totalTime * 0.25) * 0.5;
    float waveHeight = 0.5 + 0.5 * simplexNoise4d(poll.x + (totalTime + waveDisp) * waterMode_speed, (poll.y + (totalTime + waveDisp) * waterMode_speed) * 0.1, totalTime * 0.1, totalTime * 0.1);
    float noise = simplexNoise4d(pos.x * 0.68 + 39.2934, pos.y * 0.68 + 392.348294, totalTime + 492.43, 482.2) * 0.5 + 0.5;
    waveHeight = waveHeight * waveHeight;
    float wave = sin((poll.x + (totalTime + waveDisp) * waterMode_speed) * pi) * waveHeight + noise;
    float scale = waterMode_wavDep * (0.5 * (wave + 0.5)) + (1 - waterMode_wavDep);
    return pack(overScale(scale * waterMode_scale, back), overScale(noise * 0.4 + scale * waterMode_scale, fore));
}

uniform mat2    windMode_invDir;
uniform float   windMode_speed;
uniform float   windMode_wavDep;
uniform float   windMode_scale;

layout(index = 1) subroutine(colorShader) vec3[2] waveColor(vec3 back, vec3 fore, vec2 pos) {
    vec2 poll = windMode_invDir * vec2(pos.x, pos.y);
    float waveDisp = simplexNoise4d(poll.x * 0.05, poll.y * 0.05, totalTime * 0.25, totalTime * 0.25) * 0.5;
    float noise = simplexNoise4d(pos.x * 0.68 + 39.2934, pos.y * 0.68 + 392.348294, totalTime + 492.43, 482.2) * 0.5 + 0.5;
    float wave = sin((poll.x + (totalTime + waveDisp) * windMode_speed) * pi) + noise;
    float scale = windMode_wavDep * (0.5 * (wave + 0.5)) + (1 - windMode_wavDep);
    return pack(overScale(scale * windMode_scale, back), overScale(noise * 0.5 + scale * windMode_scale, fore));
}

layout(index = 2) subroutine(colorShader) vec3[2] simplexGradientColor(vec3 back, vec3 fore, vec2 pos) {
    float m = 0.5 * simplexNoise4d(pos.x * 4, pos.y * 4, totalTime * 0.25, totalTime * 0.25) + 0.5;
    return pack(m * back, m * fore);
}

void main() {
    float x = floor(mod(count + 0.01, 1.0 * screenWidth));
    float y = (count - x) / screenWidth;
    vec2 pos = vec2((x / (screenWidth) - .5) * 2, ((y / screenHeight) - .5) * 2);
    gl_Position = vec4(pos.xy, 0, 1);

    vec3 cols[2] = pack(aBackColor, aForeColor);

    for(int i = 0; i < numberUsedModes; i++) {
        if(usedRenderModes[i] == int(aRenderID + 0.01)){
            cols = colorShaders[usedRenderModes[i] - 1](aBackColor, aForeColor, vec2(pos.x * screenWidth, pos.y * screenHeight));
        }
    }

    vs_out.gBackColor = mix(cols[0], aOverColor.xyz, aOverColor.w);
    vs_out.gForeColor = mix(cols[1], aOverColor.xyz, aOverColor.w);
    vs_out.gTileID = int(aTileID + 0.01);
}

//TODO: Implement render mods to be applied.