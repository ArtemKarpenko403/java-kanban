package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldCreateTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description"));
        assertNotNull(task, "Задача должна быть создана.");
        assertEquals("Task 1", task.getName(), "Имя задачи должно совпадать.");
    }

    @Test
    void shouldCreateEpicAndSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Epic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));

        assertNotNull(epic, "Эпик должен быть создан.");
        assertNotNull(subtask, "Подзадача должна быть создана.");
        assertEquals(1, taskManager.getSubtasksByEpic(epic.getId()).size(), "Эпик должен содержать 1 подзадачу.");
    }

    @Test
    void shouldUpdateTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description"));
        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Task 1", updatedTask.getName(), "Имя задачи должно обновиться.");
    }

    @Test
    void shouldDeleteTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description"));
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Задача должна быть удалена.");
    }

    @Test
    void shouldReturnHistory() {
        Task task = taskManager.createTask(new Task("Task 1", "Description"));
        taskManager.getTask(task.getId());

        assertEquals(1, taskManager.getHistory().size(), "История должна содержать 1 задачу.");
    }

}
