#include "BaseModel.h"

extern bool checkGlError(const char *functionName);
extern GLuint loadShader(GLenum shaderType, const char *src);
extern GLuint loadTexture(const char* imagePath);
extern GLuint loadTextureColor(GLubyte rgba[]);
extern GLuint createProgram(const char *vertexShaderCode, const char *fragShaderCode);
extern BasicModel* createTriangle();

