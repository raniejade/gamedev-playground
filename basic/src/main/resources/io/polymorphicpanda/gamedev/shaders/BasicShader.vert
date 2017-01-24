layout (location = 0) in vec3 position;

uniform int foo = 1;
uniform int bar = 2;

void main() {
    gl_Position = vec4(position.x, position.y, position.z, 1.0);
}
