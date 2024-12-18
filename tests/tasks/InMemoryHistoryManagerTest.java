package tasks;


import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add_shouldAddTaskToHistory() {
        // Проверяет, что метод add добавляет задачу в историю.
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task));
    }

    @Test
    void add_shouldNotAddNullTaskToHistory() {
        // Проверяет, что метод add не добавляет null в историю.
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void add_shouldRemoveOldestTaskWhenHistoryLimitIsReached() {
        // Проверяет, что при достижении лимита истории (10 задач), самая старая задача удаляется.
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        //assertFalse(history.contains(new Task("Task 1", "Description 1"))); // Task 1 должна быть удалена
        boolean task1Present = false;
        for (Task task : history) {
            if (task.getId() == 1) {
                task1Present = true;
                break;
            }
        }
        assertFalse(task1Present, "Task with ID 1 should not be present in history");
        assertTrue(history.stream().anyMatch(task -> task.getId() == 2));
        assertTrue(history.stream().anyMatch(task -> task.getId() == 11));
    }

    @Test
    void getHistory_shouldReturnEmptyListWhenNoTasksAdded() {
        // Проверяет, что метод getHistory возвращает пустой список, если задачи не добавлялись.
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_shouldReturnListOfAddedTasks() {
        // Проверяет, что метод getHistory возвращает список добавленных задач в правильном порядке.
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void getHistory_shouldReturnCopyOfHistoryList() {
        // Проверяет, что метод getHistory возвращает копию списка истории, а не сам список.
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        historyManager.add(task1);

        List<Task> history1 = historyManager.getHistory();
        List<Task> history2 = historyManager.getHistory();

        assertNotSame(history1, history2);
    }

    @Test
    void add_shouldUpdateExistingTaskInHistory() {
        // Проверяет, что при добавлении задачи с существующим ID, обновляется имя существующей задачи в истории.
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        historyManager.add(task1);

        task1.setName("Updated Task 1");
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals("Updated Task 1", history.get(0).getName());
    }
}