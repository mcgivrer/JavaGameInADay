package com.snapgames.framework.utils;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Node<T> extends Rectangle2D.Double {
    private static long index = 0;

    protected long id = index++;
    protected String name = "node_" + (id);

    private Node<?> parent = null;
    private List<Node<?>> children = new ArrayList<>();

    public Node() {
    }

    public Node(String name) {
        this.name = name;
    }

    private T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public T setParent(Node<?> p) {
        this.parent = p;
        return (T) this;
    }

    public void add(Node<?> c) {
        c.parent = this;
        this.children.add(c);
    }

    public List<Node<?>> getChildren() {
        return children;
    }
}
