package net.ds.newConfig;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public @interface IgnoreValue {
    class SerializationStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(IgnoreValue.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
