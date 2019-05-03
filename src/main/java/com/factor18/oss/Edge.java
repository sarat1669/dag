package com.factor18.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Edge<V, P> {
    private V source;
    private V target;
    private P sourcePort;
    private P targetPort;
}
