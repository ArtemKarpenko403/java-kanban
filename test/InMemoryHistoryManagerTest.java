import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void shouldNotAddDuplicateTasksToHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        historyManager.add(task1);
        historyManager.add(task1); // Добавляем ту же задачу повторно

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1); // Удаляем первую задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void shouldRemoveLastTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(2); // Удаляем последнюю задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void shouldHandleEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldAddAndRemoveMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем задачу с ID = 2

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldKeepCorrectOrderAfterMultipleOperations() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.add(task1); // Повторный просмотр первой задачи
        historyManager.remove(3); // Удаляем последнюю задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0)); // В порядке добавления
        assertEquals(task1, history.get(1));
    }
}