package dev.denaro.yaml;

public class YamlSimpleValue extends YamlValue
{
    StringBuilder sb;

    public YamlSimpleValue(String value)
    {
        this.sb = new StringBuilder();
        sb.append(value);
    }

    public StringBuilder append(String value)
    {
        return sb.append(value);
    }

    public String toString()
    {
        return sb.toString();
    }
}
