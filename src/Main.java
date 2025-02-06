import manager.FileBackedTaskManager;
import tasks.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));

        Task task = new Task("Купить молоко", "Сходить в магазин");
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        System.out.println(loadedManager.getAllTasks());

    }
}
