package manager;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetSubtasks(exchange);
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    handleDeleteSubtask(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Subtask subtask = readRequest(exchange, Subtask.class);
        if (subtask == null) {
            sendText(exchange, "Неверный формат подзадачи", 400);
            return;
        }
        try {
            taskManager.createSubtask(subtask);
            sendText(exchange, "Подзадача успешно создана", 201);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteAllSubtasks();
            sendText(exchange, "Все подзадачи удалены", 200);
            return;
        }
        int id = Integer.parseInt(query.split("=")[1]);
        taskManager.deleteSubtask(id);
        sendText(exchange, "Подзадача удалена", 200);
    }
}