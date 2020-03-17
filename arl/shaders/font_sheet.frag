#version 400

in vec3 fForeColor;
in vec3 fBackColor;
in vec2 fTexPos;

uniform sampler2D tex;

void main() {
    //TODO: colors are black, fix
    float ink = texture(tex, fTexPos).x;
    vec3 color = (fForeColor * (1 - ink)) + (fBackColor * (ink));
    gl_FragColor = vec4(color, 1);
    //gl_FragColor = vec4(fForeColor, 1) * texture(tex, fTexPos);
}