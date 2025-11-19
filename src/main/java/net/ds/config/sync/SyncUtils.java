package net.ds.config.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncUtils {
    public static Object fixNumber(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> fixed = new HashMap<>();
            for (var entry : map.entrySet()) {
                fixed.put((String) entry.getKey(), fixNumber(entry.getValue()));
            }
            return fixed;
        } else if (obj instanceof List<?> list) {
            return list.stream().map(SyncUtils::fixNumber).toList();
        } else if (obj instanceof Double d) {
            if (d == Math.floor(d)) return d.intValue();
            return d;
        }
        return obj;
    }
}
