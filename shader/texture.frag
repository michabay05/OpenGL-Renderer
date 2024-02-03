#version 330 core

in vec3 fragColor;
in vec2 texCoord;

uniform sampler2D tex;

void main()
{
    // gl_FragColor = vec4(fragColor, 1.0);
    gl_FragColor = texture(tex, texCoord);
}
