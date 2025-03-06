package manager;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/tasks/\\d+")) { // Обработка запросов на одну задачу (например, /tasks/1)
                switch (method) {
                    case "GET":
                        handleGetTask(exchange); // Получение одной задачи
                        break;
                    case "PUT":
                        handleUpdateTask(exchange); // Обновление задачи
                        break;
                    case "DELETE":
                        handleDeleteTask(exchange); // Удаление задачи
                        break;
                    default:
                        sendText(exchange, "Метод не поддерживается", 405);
                }
            } else if (path.equals("/tasks")) { // Обработка запросов на список задач
                switch (method) {
                    case "GET":
                        handleGetTasks(exchange); // Получение списка задач
                        break;
                    case "POST":
                        handlePostTask(exchange); // Создание задачи
                        break;
                    default:
                        sendText(exchange, "Метод не поддерживается", 405);
                }
            } else {
                sendText(exchange, "Некорректный путь", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        try {
            // Получаем ID задачи из пути запроса
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 3) {
                sendText(exchange, "Некорректный запрос", 400);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                sendText(exchange, "Некорректный ID задачи", 400);
                return;
            }

            // Получаем задачу по ID
            Task task = taskManager.getTask(id);
            if (task == null) {
                sendNotFound(exchange); // Возвращаем 404, если задача не найдена
                return;
            }

            // Отправляем задачу в формате JSON
            sendText(exchange, gson.toJson(task), 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            // Получаем список всех задач из менеджера
            List<Task> tasks = taskManager.getAllTasks();

            // Преобразуем список задач в JSON
            String responseBody = gson.toJson(tasks);

            // Отправляем успешный ответ с кодом 200 и JSON-телом
            sendText(exchange, responseBody, 200);
        } catch (Exception e) {
            // Логируем ошибку и возвращаем статус 500 (Internal Server Error)
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            // Чтение тела запроса
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            // Парсинг JSON в объект Task
            Task task = gson.fromJson(requestBody, Task.class);

            // Проверка на корректность JSON и наличие обязательных полей
            if (task == null || task.getName() == null || task.getDescription() == null) {
                sendText(exchange, "Некорректный формат задачи: отсутствуют обязательные поля", 400);
                return;
            }

            // Проверка на пересечение задач по времени
            if (taskManager.isTaskOverlapping(task)) {
                sendText(exchange, "Задача пересекается по времени с другой задачей", 406);
                return;
            }

            // Создание задачи
            taskManager.createTask(task);

            // Успешный ответ
            sendText(exchange, "Задача успешно создана", 201);

        } catch (JsonSyntaxException e) {
            // Обработка ошибки некорректного JSON
            sendText(exchange, "Некорректный JSON", 400);
        } catch (Exception e) {
            // Обработка других ошибок
            e.printStackTrace(); // Логируем ошибку для отладки
            sendInternalError(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length < 3) {
            sendText(exchange, "Некорректный запрос", 400);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID задачи", 400);
            return;
        }

        Task task = taskManager.getTask(id);
        if (task == null) {
            sendNotFound(exchange); // Возвращаем 404, если задача не найдена
            return;
        }

        taskManager.deleteTask(id);
        sendText(exchange, "Задача удалена", 200);
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        try {
            // Получаем ID задачи из пути запроса
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 3) {
                sendText(exchange, "Некорректный запрос", 400);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                sendText(exchange, "Некорректный ID задачи", 400);
                return;
            }

            // Проверяем, существует ли задача
            Task existingTask = taskManager.getTask(id);
            if (existingTask == null) {
                sendNotFound(exchange); // Возвращаем 404, если задача не найдена
                return;
            }

            // Читаем тело запроса
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task updatedTask = gson.fromJson(requestBody, Task.class);

            // Проверяем, что JSON корректен
            if (updatedTask == null || updatedTask.getName() == null || updatedTask.getDescription() == null) {
                sendText(exchange, "Некорректный формат задачи", 400);
                return;
            }

            // Обновляем задачу
            updatedTask.setId(id); // Убедимся, что ID задачи не изменен
            taskManager.updateTask(updatedTask);

            // Возвращаем успешный ответ
            sendText(exchange, "Задача успешно обновлена", 200);

        } catch (JsonSyntaxException e) {
            sendText(exchange, "Некорректный JSON", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}