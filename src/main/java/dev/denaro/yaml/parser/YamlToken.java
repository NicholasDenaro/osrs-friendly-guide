package dev.denaro.yaml.parser;

public class YamlToken {
    public String value;
    public YamlTokenType type;
    private YamlToken nextToken;

    public YamlToken(YamlTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        String val = this.value;
        if (val.equals("\n")) {
            val = "<nl>";
        } else if (val.equals("\r")) {
            val = "<rc>";
        }

        return "<" + this.type + "=" + val + ">";
    }

    public void setNext(YamlToken next) {
        this.nextToken = next;
    }

    public YamlToken next() {
        return this.nextToken;
    }
}
