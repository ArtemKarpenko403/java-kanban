package manager;

import com.sun.net.httpserver.HttpExchange;
import tasks.Epic;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetEpics(exchange);
                    break;
                case "POST":
                    handlePostEpic(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpic(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(exchange, gson.toJson(epics), 200);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic epic = readRequest(exchange, Epic.class);
        if (epic == null) {
            sendText(exchange, "Неверный формат эпика", 400);
            return;
        }
        taskManager.createEpic(epic);
        sendText(exchange, "Эпик успешно создан", 201);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteAllEpics();
            sendText(exchange, "Все эпики удалены", 200);
            return;
        }
        int id = Integer.parseInt(query.split("=")[1]);
        taskManager.deleteEpic(id);
        sendText(exchange, "Эпик удален", 200);
    }
}