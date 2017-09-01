attribute vec4 v_Position;
attribute vec2 a_TextureCoordinate;
uniform mat4 v_MVPMatrix;
varying vec2 v_TextureCoordinate;

void main() {
    v_TextureCoordinate = a_TextureCoordinate;
    gl_Position = v_MVPMatrix * v_Position;
}