package tasks;

import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task 1", "Description");
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать 1 задачу.");
    }

    @Test
    void shouldLimitHistorySize() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "Description");
            task.setId(i);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(), "История должна содержать не более 10 задач.");
        assertEquals("Task 3", historyManager.getHistory().get(0).getName(),
                "История должна содержать последние 10 задач.");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать 1 задачу после удаления.");
        assertEquals(task2.getName(), historyManager.getHistory().get(0).getName(),
                "Оставшаяся задача должна быть Task 2.");
    }
}
