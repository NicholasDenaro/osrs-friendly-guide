package dev.denaro.yaml;

import java.util.ArrayList;

public class YamlArray extends YamlValue
{
    private final ArrayList<YamlValue> values = new ArrayList<>();

    public void add(YamlValue value)
    {
        values.add(value);
    }

    public YamlValue get(int index)
    {
        return values.get(index);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (YamlValue value : this.values)
        {
            sb.append(value.toString()).append(",");
        }

        sb.append("]");

        return sb.toString();
    }
}
