import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    @Test
    void testUpdateStatus() {
        Epic epic = new Epic("Epic 1", "Description");

        // Все подзадачи NEW
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        // Одна подзадача IN_PROGRESS
        subtask1.setStatus(TaskStatus.IN_PROGRESSES);
        epic.updateStatus();
        assertEquals(TaskStatus.IN_PROGRESSES, epic.getStatus());

        // Все подзадачи DONE
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        epic.updateStatus();
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void testUpdateTime() {
        Epic epic = new Epic("Epic 1", "Description");

        // Добавляем подзадачи
        LocalDateTime now = LocalDateTime.now();
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), now);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId(), Duration.ofMinutes(60), now.plusHours(1));
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        // Проверяем время эпика
        assertEquals(now, epic.getStartTime());
        assertEquals(now.plusHours(1).plusMinutes(60), epic.getEndTime());
        assertEquals(Duration.ofMinutes(90), epic.getDuration());

        // Удаляем подзадачу
        epic.removeSubtask(subtask1);
        assertEquals(now.plusHours(1), epic.getStartTime());
        assertEquals(now.plusHours(1).plusMinutes(60), epic.getEndTime());
        assertEquals(Duration.ofMinutes(60), epic.getDuration());

        // Очищаем подзадачи
        epic.clearSubtasks();
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
    }
}
