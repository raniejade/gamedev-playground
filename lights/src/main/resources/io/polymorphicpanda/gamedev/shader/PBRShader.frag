in vec3 fragNormal;
in vec3 fragPosition;

out vec4 fragColor;

const float PI = 3.14159265359;

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

// material properties
uniform vec3 albedo;
uniform float metallic;
uniform float roughness;
uniform float ao;

uniform vec3 lightPosition = vec3(0.0f, 2.0f, 2.0f);
uniform vec3 lightColor = vec3(1.0f);

float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 reflectanceEquation(vec3 N, vec3 V, vec3 F, vec3 kS, vec3 kD, vec3 lightPosition, vec3 lightColor,
                         vec3 fragPosition, float roughness) {
     // light direction
    vec3 L = normalize(lightPosition - fragPosition);
    // view direction
    vec3 H = normalize(V + L);
    // distance between current fragment and light
    float distance    = length(lightPosition - fragPosition);
    float attenuation = 1.0 / (distance * distance);
    vec3 radiance     = lightColor * attenuation;

    // cook-torrance brdf
    float NDF = DistributionGGX(N, H, roughness);
    float G   = GeometrySmith(N, V, L, roughness);

    vec3 nominator    = NDF * G * F;
    float denominator = 4 * max(dot(V, N), 0.0) * max(dot(L, N), 0.0) + 0.001;
    vec3 brdf = nominator / denominator;

    // add to outgoing radiance Lo
    float NdotL = max(dot(N, L), 0.0);

    return (kD * albedo / PI + brdf) * radiance * NdotL;
}

void main() {
    vec3 N = normalize(fragNormal);
    vec3 V = normalize(cameraPosition - fragPosition);

    vec3 F0 = vec3(0.04);
    F0      = mix(F0, albedo, metallic);
    vec3 F  = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;

    vec3 Lo = reflectanceEquation(N, V, F, kS, kD, lightPosition, lightColor, fragPosition, roughness);
    Lo += reflectanceEquation(N, V, F, kS, kD,  vec3(0.0f, 2.0f, -1.0f), lightColor, fragPosition, roughness);
    Lo += reflectanceEquation(N, V, F, kS, kD,  vec3(0.0f, 2.0f, 0.0f), lightColor, fragPosition, roughness);

    vec3 ambient = vec3(0.03) * albedo * ao;
    vec3 color = ambient + Lo;

    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));

    fragColor = vec4(color, 1.0);
}
