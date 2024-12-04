import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        //Создание задач
        Task task1 = manager.createTask(new Task("tasks.Task 1", "Description 1"));
        Task task2 = manager.createTask(new Task("tasks.Task 2", "Description 2"));

        //Создание эпиков и подзадач
        Epic epic = manager.createEpic(new Epic("tasks.Epic 1", "Description of Epic1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("tasks.Subtask 1", "Description of tasks.Subtask 1", epic.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("tasks.Subtask 2", "Description of tasks.Subtask 2", epic.getId()));

        //Изменить статус
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        //Печатать задачи
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        //Удалить задачу
        manager.deleteTask(task1.getId());
    }

}
