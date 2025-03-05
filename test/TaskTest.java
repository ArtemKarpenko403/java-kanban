import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void testTaskCreation() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task("Task 1", "Description", Duration.ofMinutes(30), startTime);

        assertEquals("Task 1", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(30), task.getDuration());
        assertEquals(startTime, task.getStartTime());
        assertEquals(startTime.plusMinutes(30), task.getEndTime());
    }

    @Test
    void testSubtaskCreation() {
        LocalDateTime startTime = LocalDateTime.now();
        Subtask subtask = new Subtask("Subtask 1", "Description", 1, Duration.ofMinutes(30), startTime);

        assertEquals("Subtask 1", subtask.getName());
        assertEquals(1, subtask.getEpicId());
        assertEquals(startTime, subtask.getStartTime());
        assertEquals(startTime.plusMinutes(30), subtask.getEndTime());
    }

    @Test
    void testEpicTimeCalculation() {
        Epic epic = new Epic("Epic 1", "Description");
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusHours(1));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getEndTime(), epic.getEndTime());
        assertEquals(Duration.ofMinutes(90), epic.getDuration());
    }
}
