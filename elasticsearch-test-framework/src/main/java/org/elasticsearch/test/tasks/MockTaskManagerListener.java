package org.elasticsearch.test.tasks;

import org.elasticsearch.tasks.Task;

/**
 * Listener for task registration/unregistration.
 */
public interface MockTaskManagerListener {
    void onTaskRegistered(Task task);

    void onTaskUnregistered(Task task);

    void waitForTaskCompletion(Task task);
}
