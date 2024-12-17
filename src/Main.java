import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = manager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = manager.createTask(new Task("Task 2", "Description 2"));

        Epic epic1 = manager.createEpic(new Epic("Epic 1", "Epic Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1", "Subtask Description", epic1.getId()));

        // Получаем задачи
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());

        // Печатаем историю
        System.out.println("History:");
        manager.getHistory().forEach(System.out::println);
    }
}
