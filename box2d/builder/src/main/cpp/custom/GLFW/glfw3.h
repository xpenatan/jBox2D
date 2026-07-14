#pragma once

// Only the input constants and glfwGetKey used by the upstream sample logic
// are needed. Window creation and rendering are supplied by libGDX.
struct GLFWwindow {
};

int JBox2DSampleGetKeyState(int key);

inline int glfwGetKey(GLFWwindow*, int key) {
    return JBox2DSampleGetKeyState(key);
}

#define GLFW_RELEASE 0
#define GLFW_PRESS 1

#define GLFW_KEY_SPACE 32
#define GLFW_KEY_1 49
#define GLFW_KEY_2 50
#define GLFW_KEY_3 51
#define GLFW_KEY_4 52
#define GLFW_KEY_A 65
#define GLFW_KEY_B 66
#define GLFW_KEY_C 67
#define GLFW_KEY_D 68
#define GLFW_KEY_G 71
#define GLFW_KEY_S 83
#define GLFW_KEY_V 86
#define GLFW_KEY_W 87

#define GLFW_MOUSE_BUTTON_1 0
#define GLFW_MOD_SHIFT 0x0001
#define GLFW_MOD_CONTROL 0x0002
