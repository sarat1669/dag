package com.factor18.oss;

import java.util.Set;

public interface Vertex<P> {
    String getName();
    Set<P> getInPorts();
    Set<P> getOutPorts();
}
