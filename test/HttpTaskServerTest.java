
import com.google.gson.Gson;
import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilities.GsonConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private final Gson gson = GsonConfig.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача должна быть успешно создана");

        Task createdTask = taskManager.getTask(1);
        assertNotNull(createdTask, "Задача должна быть добавлена в менеджер");
        assertEquals("Test Task", createdTask.getName(), "Имя задачи должно совпадать");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // Создаем задачу для теста
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);

        // Отправляем GET-запрос на получение списка задач
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode(), "Запрос должен быть успешным");

        // Проверяем, что ответ содержит созданную задачу
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, tasks.length, "Должна быть возвращена одна задача");
        assertEquals("Test Task", tasks[0].getName(), "Имя задачи должно совпадать");
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Подзадача должна быть успешно создана");

        Subtask createdSubtask = taskManager.getSubtask(2);
        assertNotNull(createdSubtask, "Подзадача должна быть добавлена в менеджер");
        assertEquals("Test Subtask", createdSubtask.getName(), "Имя подзадачи должно совпадать");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.getTask(1); // Добавляем задачу в историю

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Запрос должен быть успешным");

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length, "История должна содержать одну задачу");
        assertEquals("Test Task", history[0].getName(), "Имя задачи в истории должно совпадать");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Запрос должен быть успешным");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritizedTasks.length, "Должны быть возвращены две задачи");
        assertEquals("Task 1", prioritizedTasks[0].getName(), "Первая задача должна быть Task 1");
        assertEquals("Task 2", prioritizedTasks[1].getName(), "Вторая задача должна быть Task 2");
    }

    @Test
    public void testCreateTaskWithOverlappingTime() throws IOException, InterruptedException {
        // Создаем первую задачу
        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task1);

        // Создаем вторую задачу, которая пересекается по времени с первой
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15));
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Должен вернуться статус 406 (Not Acceptable)");
    }

    @Test
    public void testUpdateNonExistentTask() throws IOException, InterruptedException {
        // Создаем задачу с несуществующим ID
        Task task = new Task("Non-existent Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(999); // Несуществующий ID
        String taskJson = gson.toJson(task);

        // Отправляем PUT-запрос на обновление задачи
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(405, response.statusCode(), "Должен вернуться статус 405 (Method Not Allowed)");
    }

    @Test
    public void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999")) // Несуществующий ID
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Должен вернуться статус 404 (Not Found)");
    }

    @Test
    public void testCreateTaskWithMissingFields() throws IOException, InterruptedException {
        String invalidTaskJson = "{ \"name\": \"Task without description\" }"; // Отсутствует поле "description"

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Должен вернуться статус 400 (Bad Request)");
    }

    @Test
    public void testSendInvalidJson() throws IOException, InterruptedException {
        String invalidJson = "{ invalid json }"; // Некорректный JSON

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Должен вернуться статус 400 (Bad Request)");
    }

    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999")) // Несуществующий ID
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Должен вернуться статус 404 (Not Found)");
    }
}