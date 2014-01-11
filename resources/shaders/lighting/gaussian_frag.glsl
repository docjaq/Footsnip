#version 330

in vec4 diffuseColor;
in vec3 vertexNormal;
in vec3 cameraSpacePosition;

out vec4 outputColor;

uniform vec3 modelSpaceLightPos;

uniform vec4 lightIntensity;
uniform vec4 ambientIntensity;

uniform vec3 cameraSpaceLightPos;

uniform float lightAttenuation;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

const vec4 specularColor = vec4(0.25, 0.25, 0.25, 1.0);
uniform float shininessFactor;

//The problem here (I think), is that the cameraSpaceLightPos is not actually in camera space... just in it's own co-ordinates. So, here, we are stupidly transforming it for every fragment. Really, we need to compute the transformation once before being passed to the fragment shader, but I can't figure out how to get access to the view and projection matricies outside of these shaders.

//Attenuate light strength based on distance of a fragment from the light
float CalcAttenuation(in vec3 cameraSpacePosition, out vec3 lightDirection)
{
    //Compute the vector between the light and fragment positions, in camera space
	vec3 lightDifference =  (projectionMatrix * viewMatrix * vec4(cameraSpaceLightPos, 1)).xyz - cameraSpacePosition;
	float lightDistanceSqr = dot(lightDifference, lightDifference);
	lightDirection = lightDifference * inversesqrt(lightDistanceSqr);
	
	return (1 / ( 1.0 + lightAttenuation * sqrt(lightDistanceSqr)));
}

void main()
{
	vec3 lightDir = vec3(0.0);
	float atten = CalcAttenuation(cameraSpacePosition, lightDir);
	vec4 attenIntensity = atten * lightIntensity;
	
	vec3 surfaceNormal = normalize(vertexNormal);
	float cosAngIncidence = dot(surfaceNormal, lightDir);
	cosAngIncidence = clamp(cosAngIncidence, 0, 1);
	
	vec3 viewDirection = normalize(-cameraSpacePosition);
	
	vec3 halfAngle = normalize(lightDir + viewDirection);
	float angleNormalHalf = acos(dot(halfAngle, surfaceNormal));
	float exponent = angleNormalHalf / shininessFactor;
	exponent = -(exponent * exponent);
	float gaussianTerm = exp(exponent);
    
	gaussianTerm = cosAngIncidence != 0.0 ? gaussianTerm : 0.0;
    
	outputColor = (diffuseColor * attenIntensity * cosAngIncidence) +
    (specularColor * attenIntensity * gaussianTerm) +
    (diffuseColor * ambientIntensity);
}
