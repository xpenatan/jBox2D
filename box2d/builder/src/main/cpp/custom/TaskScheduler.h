#pragma once

#include <cstdint>

// The Box2D sample application normally uses enkiTS. The Java sample host runs
// physics on its render thread on every supported platform, so a synchronous
// scheduler is sufficient and avoids pulling a platform-specific thread pool
// into JNI, FFM, Android, and WebAssembly builds.
namespace enki {

struct TaskSetPartition {
    std::uint32_t start;
    std::uint32_t end;
};

class ITaskSet {
public:
    ITaskSet() : m_SetSize(0), m_MinRange(1) {
    }

    virtual ~ITaskSet() {
    }

    virtual void ExecuteRange(TaskSetPartition range, std::uint32_t threadIndex) = 0;

    std::uint32_t m_SetSize;
    std::uint32_t m_MinRange;
};

class TaskScheduler {
public:
    void Initialize(std::uint32_t) {
    }

    void AddTaskSetToPipe(ITaskSet* task) {
        if(task != nullptr && task->m_SetSize > 0) {
            TaskSetPartition range = {0, task->m_SetSize};
            task->ExecuteRange(range, 0);
        }
    }

    void WaitforTask(ITaskSet*) {
    }
};

} // namespace enki
