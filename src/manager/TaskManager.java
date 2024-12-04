package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Task createTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }
        return subtask;
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        if (epics.containsKey(task.getId())) {

            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask);
            }
        }
    }

    public void deleteTask(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpic(Integer id) {
        epics.remove(id);
    }

    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
        }

    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return List.copyOf(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return List.copyOf(subtasks.values());
    }

    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return (epic != null) ? epic.getSubtasks() : List.of();
    }
public void deleteAllTasks(){
        tasks.clear();
}
public void deleteAllEpics(){
        epics.clear();
        subtasks.clear();
}
public void deleteAllSubtasks(){
        subtasks.clear();
        //Обновляем статус эпиков из-за потери подзадач
    for (Epic epic : epics.values()) {
        epic.clearSubtasks();
        epic.updateStatus();
    }

}
}
