#version 400

in vec3 fForeColor;
in vec3 fBackColor;
in vec2 fTexPos;

uniform sampler2D tex;

layout(location=0) out vec4 fragColor;

void main() {
    float ink = texture(tex, fTexPos).x;
    vec3 color = (fForeColor * (1 - ink)) + (fBackColor * (ink));
    fragColor = vec4(color, 1);
}