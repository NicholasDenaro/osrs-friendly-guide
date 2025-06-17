package dev.denaro.yaml;

import java.util.ArrayList;
import java.util.Iterator;

public class YamlArray extends YamlValue implements Iterable<YamlValue>
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

    public ArrayList<YamlValue> getValues()
    {
        return new ArrayList<>(this.values);
    }

    @Override
    public Iterator<YamlValue> iterator() {
        return this.values.iterator();
    }
}
