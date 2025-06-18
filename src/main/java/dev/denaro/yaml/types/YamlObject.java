package dev.denaro.yaml.types;

import java.util.HashMap;

public class YamlObject extends YamlValue
{
    HashMap<String, YamlValue> map = new HashMap<>();

    public YamlValue get(String key)
    {
        return map.get(key);
    }

    public boolean hasKey(String key)
    {
        return map.containsKey(key);
    }

    public YamlObject getObject(String key) {
        if (map.get(key) instanceof YamlObject)
        {
            return (YamlObject) map.get(key);
        }

        return null;
    }

    public YamlObject getObjectOrDefault(String key, YamlObject object) {
        if (map.get(key) instanceof YamlObject)
        {
            return (YamlObject) map.get(key);
        }

        return object;
    }

    public YamlArray getArray(String key) {
        if (map.get(key) instanceof YamlArray)
        {
            return (YamlArray) map.get(key);
        }

        return null;
    }

    public YamlArray getArrayOrDefault(String key, YamlArray array) {
        if (map.get(key) instanceof YamlArray)
        {
            return (YamlArray) map.get(key);
        }

        return array;
    }

    public YamlSimpleValue getSimpleValue(String key) {
        if (map.get(key) instanceof YamlSimpleValue)
        {
            return (YamlSimpleValue) map.get(key);
        }

        return null;
    }

    public YamlSimpleValue getSimpleValueOrDefault(String key, YamlSimpleValue simple) {
        if (map.get(key) instanceof YamlSimpleValue)
        {
            return (YamlSimpleValue) map.get(key);
        }

        return simple;
    }

    public void set(String key, YamlValue value)
    {
        map.put(key, value);
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (String key : map.keySet())
        {
            sb.append(key).append(":").append(map.get(key)).append(",");
        }

        sb.append("}");
        return sb.toString();
    }
}
