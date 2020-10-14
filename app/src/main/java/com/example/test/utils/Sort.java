package com.example.test.utils;

import com.example.test.models.Task;

import java.util.Collections;
import java.util.List;

public class Sort {
    public static void sortByTitle(List<Task> tasks) {
        Collections.sort(tasks, (lhs, rhs) -> lhs.getTitle().compareTo(rhs.getTitle()));
    }

    public static void sortByStatus(List<Task> tasks) {
        Collections.sort(tasks, (lhs, rhs) -> (lhs.isStatus() && !rhs.isStatus()) ? -1 : (!lhs.isStatus() && rhs.isStatus()) ? 1 : 0);
    }

    public static void sortByPosition(List<Task> tasks) {
        Collections.sort(tasks, (lhs, rhs) -> Integer.compare(rhs.getPosition(), lhs.getPosition()));
    }

    public static void sortByPriority(List<Task> tasks) {
        Collections.sort(tasks, (lhs, rhs) -> Integer.compare(rhs.getPriority(), lhs.getPriority()));
    }

    public static void sortByDate(List<Task> tasks) {
        Collections.sort(tasks, (lhs, rhs) -> Long.compare(rhs.getDate(), lhs.getDate()));
    }
}
