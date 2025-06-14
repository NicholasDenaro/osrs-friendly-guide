package dev.denaro.yaml;

import java.util.HashMap;

public class YamlObject extends YamlValue
{
    HashMap<String, YamlValue> map = new HashMap<>();

    public YamlValue get(String key)
    {
        return map.get(key);
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
