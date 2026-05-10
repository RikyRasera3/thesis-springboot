package it.thesis.springboot.util;

import java.util.Optional;
import java.util.function.Consumer;

public final class OptionalUtils {
    public static <T> void getOptionalValue(Optional<T> value, Consumer<T> setter) {
        if(value.isPresent()) {
            setter.accept(value.orElse(null));
        }
    }
}