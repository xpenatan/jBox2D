#pragma once

#include <cstdarg>
#include <cstdint>

struct ImVec2 {
    float x;
    float y;

    ImVec2(float xValue = 0.0f, float yValue = 0.0f) : x(xValue), y(yValue) {
    }
};

struct ImVec4 {
    float x;
    float y;
    float z;
    float w;

    ImVec4(float xValue = 0.0f, float yValue = 0.0f, float zValue = 0.0f, float wValue = 0.0f)
        : x(xValue), y(yValue), z(zValue), w(wValue) {
    }
};

struct ImColor {
    ImVec4 value;

    ImColor(int red, int green, int blue, int alpha = 255)
        : value(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f) {
    }
};

struct ImFont {
    float FontSize;

    explicit ImFont(float size = 16.0f) : FontSize(size) {
    }
};

typedef int ImGuiWindowFlags;
typedef int ImGuiCond;
typedef int ImGuiSliderFlags;
typedef std::uint32_t ImU32;

enum {
    ImGuiWindowFlags_NoTitleBar = 1 << 0,
    ImGuiWindowFlags_NoResize = 1 << 1,
    ImGuiWindowFlags_NoMove = 1 << 2,
    ImGuiWindowFlags_NoScrollbar = 1 << 3,
    ImGuiWindowFlags_NoInputs = 1 << 4,
    ImGuiWindowFlags_AlwaysAutoResize = 1 << 5,
    ImGuiCond_Once = 1 << 0,
    ImGuiSliderFlags_ClampOnInput = 1 << 0
};

#define IM_ARRAYSIZE(array) ((int)(sizeof(array) / sizeof((array)[0])))
#define IM_COL32(red, green, blue, alpha) \
    ((ImU32)(((alpha) << 24) | ((blue) << 16) | ((green) << 8) | (red)))
#define IM_COL32_WHITE IM_COL32(255, 255, 255, 255)

struct ImDrawList {
    void AddText(ImFont* font, float fontSize, ImVec2 position, ImU32 color, const char* text);
};

namespace ImGui {

bool Begin(const char* name, bool* open = nullptr, ImGuiWindowFlags flags = 0);
void End();
void SetNextWindowPos(ImVec2 position, ImGuiCond condition = 0);
void SetNextWindowSize(ImVec2 size, ImGuiCond condition = 0);
void SetNextWindowBgAlpha(float alpha);
void SetCursorPos(ImVec2 position);
float GetWindowWidth();
ImDrawList* GetWindowDrawList();
void PushFont(ImFont* font);
void PopFont();
void PushItemWidth(float width);
void PopItemWidth();
void SameLine();
void Separator();
void Text(const char* format, ...);
void TextColoredV(ImColor color, const char* format, std::va_list args);
bool Button(const char* label, ImVec2 size = ImVec2());
bool Checkbox(const char* label, bool* value);
bool Combo(const char* label, int* currentItem, const char* const items[], int itemCount);
bool RadioButton(const char* label, bool active);
bool RadioButton(const char* label, int* value, int buttonValue);
bool SliderFloat(const char* label, float* value, float minimum, float maximum,
                 const char* format = "%.3f", ImGuiSliderFlags flags = 0);
bool SliderInt(const char* label, int* value, int minimum, int maximum,
               const char* format = "%d", ImGuiSliderFlags flags = 0);

} // namespace ImGui
