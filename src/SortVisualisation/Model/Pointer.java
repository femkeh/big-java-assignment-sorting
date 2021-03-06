package SortVisualisation.Model;

import java.util.LinkedList;

/**
 * Created by peterzen on 2017-03-06.
 * Part of the big-java-assignment-sorting project.
 */
public class Pointer {
    private LinkedList<Integer> indices;

    public Pointer() {
        indices = new LinkedList<>();
    }

    public Pointer(int[] inputIndices) {
        this();

        for (int index : inputIndices) {
            indices.add(index);
        }
    }

    public Pointer(int inputIndex) {
        this();

        indices.add(inputIndex);
    }

    public LinkedList<Integer> getIndices() {
        return indices;
    }

    public void setCurrent(int index) {
        indices.clear();
        indices.add(index);
    }

    public void setCurrent(int[] newIndices) {
        indices.clear();
        for (int i : newIndices) {
            indices.add(i);
        }
    }

    public void updateIndex(int i, int value) {
        indices.set(i, value);
    }
}
