import exception.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldCreateTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now()));
        assertNotNull(task, "Задача должна быть создана.");
        assertEquals("Task 1", task.getName(), "Имя задачи должно совпадать.");
        assertEquals(Duration.ofMinutes(30), task.getDuration(), "Продолжительность задачи должна совпадать.");
        assertNotNull(task.getStartTime(), "Время начала задачи должно быть задано.");
    }

    @Test
    void shouldCreateEpicAndSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Epic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now()));

        assertNotNull(epic, "Эпик должен быть создан.");
        assertNotNull(subtask, "Подзадача должна быть создана.");
        assertEquals(1, taskManager.getSubtasksByEpic(epic.getId()).size(), "Эпик должен содержать 1 подзадачу.");
        assertEquals(Duration.ofMinutes(30), epic.getDuration(), "Продолжительность эпика должна совпадать.");
        assertNotNull(epic.getStartTime(), "Время начала эпика должно быть задано.");
    }

    @Test
    void shouldUpdateTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now()));
        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("Updated Task 1", updatedTask.getName(), "Имя задачи должно обновиться.");
    }

    @Test
    void shouldDeleteTask() {
        Task task = taskManager.createTask(new Task("Task 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now()));
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Задача должна быть удалена.");
    }

    @Test
    void shouldReturnHistory() {
        Task task = taskManager.createTask(new Task("Task 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now()));
        taskManager.getTask(task.getId());

        assertEquals(1, taskManager.getHistory().size(), "История должна содержать 1 задачу.");
    }

    @Test
    void testTaskOverlapping() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusMinutes(15));

        manager.createTask(task1);
        assertThrows(ManagerSaveException.class, () -> manager.createTask(task2));
    }

    @Test
    void testGetPrioritizedTasks() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task3 = new Task("Task 3", "Description", Duration.ofMinutes(30), null); // Без startTime

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size()); // task3 не учитывается
        assertEquals(task1, prioritizedTasks.get(0));
        assertEquals(task2, prioritizedTasks.get(1));
    }

    @Test
    void testEpicTimeAndStatus() {
        TaskManager manager = Managers.getDefault();

        Epic epic = manager.createEpic(new Epic("Epic 1", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusHours(1)));

        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время начала эпика должно совпадать с временем начала первой подзадачи.");
        assertEquals(subtask2.getEndTime(), epic.getEndTime(), "Время окончания эпика должно совпадать с временем окончания последней подзадачи.");
        assertEquals(Duration.ofMinutes(90), epic.getDuration(), "Продолжительность эпика должна быть суммой продолжительностей подзадач.");

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESSES, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если одна подзадача выполнена, а другая нет.");

        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи выполнены.");
    }

}
