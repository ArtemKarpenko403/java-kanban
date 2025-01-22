// Исправленный класс InMemoryHistoryManager
package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> nodeMap = new HashMap<>(); // id -> узел
    private Node<Task> head;  // Голова списка
    private Node<Task> tail;  // Хвост списка

    @Override
    public void add(Task task) {
        // Если задача уже есть, удаляем старую запись
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }

        // Добавляем задачу в конец списка
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        // Удаляем задачу из списка, если она есть
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        // Перекладываем задачи из списка в ArrayList
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    // Вспомогательный метод: добавить задачу в конец списка
    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode; // Если список был пуст
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode); // Обновляем хэш-таблицу
    }

    // Вспомогательный метод: удалить узел из списка
    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // Если это был первый узел
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // Если это был последний узел
        }

        nodeMap.remove(node.data.getId()); // Удаляем из хэш-таблицы
    }
}