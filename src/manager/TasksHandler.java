package manager;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteTask(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Task task = readRequest(exchange, Task.class);
        if (task == null) {
            sendText(exchange, "Неверный формат задачи", 400);
            return;
        }
        try {
            taskManager.createTask(task);
            sendText(exchange, "Задача успешно создана", 201);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены", 200);
            return;
        }
        int id = Integer.parseInt(query.split("=")[1]);
        taskManager.deleteTask(id);
        sendText(exchange, "Задача удалена", 200);
    }
}