package dev.denaro.yaml;

import lombok.Getter;

public class YamlSimpleValue extends YamlValue
{
    StringBuilder sb;

    @Getter
    private boolean isNull = false;

    public YamlSimpleValue(String value)
    {
        this.sb = new StringBuilder();
        if (value != null)
        {
            sb.append(value);
        }
    }

    private YamlSimpleValue()
    {
        this.isNull = true;
    }

    public int getInt()
    {
        return this.isNull ? null : Integer.parseInt(sb.toString());
    }

    public boolean getBoolean()
    {
        return this.isNull ? null : Boolean.parseBoolean(sb.toString());
    }

    public double getDouble()
    {
        return this.isNull ? null : Double.parseDouble(sb.toString());
    }

    public String getString()
    {
        return this.isNull ? null : sb.toString();
    }

    public StringBuilder append(String value)
    {
        return sb.append(value);
    }

    public String toString()
    {
        return sb.toString();
    }

    public static final YamlSimpleValue Null = new YamlSimpleValue();

    public static YamlSimpleValue fromBoolean(boolean bool)
    {
        return new YamlSimpleValue("" + bool);
    }
}
