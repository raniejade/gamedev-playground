in vec3 fragNormal;
in vec3 fragPosition;

out vec4 color;

uniform vec3 lightPosition = vec3(1.2f, 1.0f, 2.0f);
uniform vec3 lightColor = vec3(1.0f);
uniform vec3 objectColor = vec3(1.0f, 0.5f, 0.31f);

void main() {
    // ambient
    float ambientStrength = 0.1f;
    vec3 ambient = ambientStrength * lightColor;

    // diffuse
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPosition - fragPosition);
    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightColor;

    // total
    vec3 result = (ambient + diffuse) * objectColor;
    color = vec4(result, 1.0f);
}
