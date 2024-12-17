package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int HISTORY_LIMIT = 10;
    private final Map<Integer, Task> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        // Удаляем задачу из истории, если она уже есть
        remove(task.getId());

        history.addLast(task);
        historyMap.put(task.getId(), task);

        if (history.size() > HISTORY_LIMIT) {
            Task removed = history.removeFirst();
            historyMap.remove(removed.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void remove(int id) {
        history.removeIf(task -> task.getId() == id);
        historyMap.remove(id);
    }
}