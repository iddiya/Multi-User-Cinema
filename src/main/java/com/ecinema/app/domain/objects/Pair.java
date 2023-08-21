package com.ecinema.app.domain.objects;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> {
    private K first;
    private V second;
}
