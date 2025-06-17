package dev.denaro.yaml.parser;

import lombok.Getter;

import java.util.ArrayList;

public class TokenReader {
    private final ArrayList<YamlToken> tokens;
    @Getter
    private int index;

    public TokenReader(ArrayList<YamlToken> tokens) {
        this.tokens = tokens;
        index = -1;
    }

    public YamlToken next() {
        return this.tokens.get(++this.index);
    }

    public YamlLine readLine() {
        ArrayList<YamlToken> line = new ArrayList<>();
        while (this.hasNext()) {
            YamlToken t = this.next();
            line.add(t);

            if (t.type == YamlTokenType.new_line) {
                return new YamlLine(line);
            }
        }

        return new YamlLine(line);
    }

    public YamlLine getNextRealLine() {
        YamlLine line = readLine();

        while (true) {
            if (line.isEmpty()) {
                return line;
            }

            if (line.firstRealToken() != null) {
                return line;
            }

            line = readLine();
        }
    }

    public YamlLine getNextNonEmptyLine() {
        YamlLine line = readLine();

        while (true) {
            if (line.isEmpty()) {
                return line;
            }

            if (line.firstNonWhitespaceToken() != null) {
                return line;
            }

            line = readLine();
        }
    }

    public YamlToken peek() {
        return this.tokens.get(this.index + 1);
    }

    public boolean hasNext() {
        return this.index + 1 < this.tokens.size();
    }

    public YamlToken get(int i) {
        return this.tokens.get(i);
    }

    public void seek(int i) {
        this.index = i;
    }

    public void rewindLine() {
        for (int i = this.index - 1; i >= 0; i--) {
            if (this.tokens.get(i).type == YamlTokenType.new_line) {
                this.index = i + 1;
                return;
            }
        }

        this.index = 0;
    }
}
