#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoords;

out vec3 fragColor;
out vec3 texCoord;

void main()
{
    fragColor = aColor;
    texCoord = aTexCoord;
    gl_Position = vec4(aPos, 1.0, 1.0);
}
