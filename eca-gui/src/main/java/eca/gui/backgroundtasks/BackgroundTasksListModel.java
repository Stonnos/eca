package eca.gui.backgroundtasks;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Background tasks list model.
 *
 * @author Roman Batygin
 */
public class BackgroundTasksListModel extends DefaultListModel<String> {
    private final List<BackgroundTaskInfo> tasks = new ArrayList<>();

    public synchronized void add(BackgroundTaskInfo taskInfo) {
        tasks.add(taskInfo);
        addElement(taskInfo.getTitle());
    }

    public synchronized BackgroundTaskInfo getTask(int i) {
        return tasks.get(i);
    }

    public synchronized void removeItem(String id) {
        int taskIndex = IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't find task with id [%s]", id)));
        tasks.remove(taskIndex);
        remove(taskIndex);
    }
}
