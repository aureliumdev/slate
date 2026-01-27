package dev.aurelium.slate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapParser {

    public Object getElement(Map<?, ?> map, String key) {
        // Check if not null
        Object object = map.get(key);
        Validate.notNull(object, "Item requires entry with key " + key);
        return object;
    }

    public String getString(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof String)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type String");
        }
        return (String) object;
    }

    public int getInt(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof Integer)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type int");
        }
        return (int) object;
    }

    public String getString(Map<?, ?> map, String key, String def) {
        try {
            return getString(map, key);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public int getInt(Map<?, ?> map, String key, int def) {
        try {
            return getInt(map, key);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public List<Map<?, ?>> getMapList(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof List)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type section map list");
        }
        List<?> unknownList = (List<?>) object;
        List<Map<?, ?>> mapList = new ArrayList<>();
        for (Object element : unknownList) {
            if (element instanceof Map) {
                mapList.add((Map<?, ?>) element);
            }
        }
        return mapList;
    }

    public List<Map<?, ?>> getMapList(Map<?, ?> map, String key, List<Map<?, ?>> def) {
        try {
            return getMapList(map, key);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public Map<?, ?> getMap(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("Key " + key + " must be a section map");
        }
        return (Map<?, ?>) object;
    }

    public Map<?, ?> getMap(Map<?, ?> map, String key, Map<?, ?> def) {
        try {
            return getMap(map, key);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

}
