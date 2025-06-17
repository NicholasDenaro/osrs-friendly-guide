package dev.denaro.yaml.parser;

import lombok.Getter;

import java.util.ArrayList;

public class YamlLine {
    @Getter
    private final ArrayList<YamlToken> line;

    public YamlLine(ArrayList<YamlToken> line) {
        this.line = line;
    }

    public int getDepth() {
        if (line.isEmpty()) {
            return 0;
        }

        if (line.get(0).type == YamlTokenType.whitespace) {
            return line.get(0).value.length();
        }

        return 0;
    }

    public YamlToken get(YamlTokenType type) {
        return line.stream().filter(token -> token.type == type).findFirst().orElse(null);
    }

    public YamlToken firstRealToken() {
        return this.line.stream()
                .filter(token -> token.type != YamlTokenType.comment && token.type != YamlTokenType.whitespace && token.type != YamlTokenType.new_line)
                .findFirst().orElse(null);
    }


    public YamlToken firstNonWhitespaceToken() {
        return this.line.stream()
                .filter(token -> token.type != YamlTokenType.whitespace && token.type != YamlTokenType.new_line)
                .findFirst().orElse(null);
    }

    public boolean hasComment() {
        return this.line.stream().anyMatch(token -> token.type == YamlTokenType.comment);
    }

    public boolean isEmpty() {
        return this.line.isEmpty() || this.line.get(0).type == YamlTokenType.new_line;
    }
}
