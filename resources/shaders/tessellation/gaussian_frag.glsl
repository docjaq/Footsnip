  #version 400

//From vertex shader
in vec4 gDiffuseColor;
in vec3 gVertexNormal;
in vec3 gPosition;
in vec3 gUvPosition;

in vec3 gPatchDistance;

out vec4 outputColor;

//From CPU
uniform vec4 lightIntensity;
uniform vec4 ambientIntensity;
uniform vec3 cameraSpaceLightPos;
uniform float lightAttenuation;
const vec4 specularColor = vec4(0.25, 0.25, 0.25, 1.0);
uniform float shininessFactor;

uniform sampler2D normalMapA;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float calcAttenuation(in vec3 gPosition, out vec3 lightDirection)
{
	vec3 lightDifference = cameraSpaceLightPos - gPosition;
	float lightDistanceSqr = dot(lightDifference, lightDifference);
	lightDirection = lightDifference * inversesqrt(lightDistanceSqr);
	
	return 1.0 / (1.0 + lightAttenuation * sqrt(lightDistanceSqr));
}

void main()
{
	vec3 lightDir = vec3(0, 0, 0);
	float atten = calcAttenuation(gPosition, lightDir);
	vec4 attenIntensity = atten * lightIntensity;
	
	vec3 surfaceNormal = gVertexNormal;
    
    surfaceNormal+= (2*(texture(normalMapA, gUvPosition.xy).xyz)-1);
    surfaceNormal = normalize(surfaceNormal);
    
	float cosAngIncidence = dot(surfaceNormal, lightDir);
	cosAngIncidence = clamp(cosAngIncidence, 0.0, 1.0);
	
	vec3 viewDirection = normalize(-gPosition);
	
	vec3 halfAngle = normalize(lightDir + viewDirection);
	float angleNormalHalf = acos(dot(halfAngle, surfaceNormal));
	float exponent = angleNormalHalf / shininessFactor;
	exponent = -(exponent * exponent);
	float gaussianTerm = exp(exponent);
	
	gaussianTerm = cosAngIncidence != 0.0 ? gaussianTerm : 0.0;
	
	outputColor = gDiffuseColor  * attenIntensity * cosAngIncidence +
    specularColor * attenIntensity * gaussianTerm +
    gDiffuseColor  * ambientIntensity;
}
