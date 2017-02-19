layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;

out vec3 fragNormal;
out vec3 fragPosition;

layout (std140) uniform constants
{
    mat4 projection;        // 0
                            // 16
                            // 32
                            // 48
    mat4 view;              // 64
                            // 80
                            // 96
                            // 112
    vec3 cameraPosition;    // 128
};

uniform mat4 model;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0f);
    fragNormal = mat3(transpose(inverse(model))) * normal;
    fragPosition = vec3(model * vec4(position, 1.0f));
}
