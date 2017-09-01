attribute vec4 v_Position;
uniform mat4 v_MVPMatrix;

void main() {
    gl_Position = v_MVPMatrix * v_Position;
}