package dev.denaro.yaml;

import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class Yaml
{
    enum YamlTokenType {whitespace, string, array_index, kvp_splitter, comment, new_line};
    private static class YamlToken
    {
        public String value;
        public YamlTokenType type;
        private YamlToken nextToken;

        public YamlToken(YamlTokenType type, String value)
        {
            this.type = type;
            this.value = value;
        }

        public String toString()
        {
            return "<" + this.type + "=" + (this.value.equals("\n") ? "<nl>" : this.value) + ">";
        }

        public void setNext(YamlToken next)
        {
            this.nextToken = next;
        }

        public YamlToken next()
        {
            return this.nextToken;
        }
    }

    private BufferedReader reader;
    public Yaml()
    {

    }

    public YamlValue load(InputStream yamlString) throws IOException, ParseException {
        reader = new BufferedReader(new InputStreamReader(yamlString));

        ArrayList<YamlToken> tokens = this.tokenize();

        return parse(tokens);
    }

    private ArrayList<YamlToken> tokenize() throws IOException {

        StringBuilder sb = new StringBuilder();

        ArrayList<YamlToken> tokens = new ArrayList<>();

        int val = reader.read();
        char next = (char) val;
        boolean consumedNext = false;
        System.out.println("Start");
        while (next != (char) 0)
        {
            char ch = next;

            val = reader.read();
            next = val == -1 ? (char) 0 : (char) val;

            System.out.print(ch);
            if (consumedNext)
            {
                consumedNext = false;
                continue;
            }

            char startingChar = sb.length() > 0 ? sb.charAt(0) : (char) 0;
            if (startingChar == '#')
            {
                if (ch != '\n')
                {
                    sb.append(ch);
                }
                else
                {
                    tokens.add(new YamlToken(YamlTokenType.comment, sb.toString()));
                    sb.setLength(0);
                    sb.append(ch);
                    tokens.add(new YamlToken(YamlTokenType.new_line, sb.toString()));
                    sb.setLength(0);
                }
                continue;
            }
            else if (startingChar == ' ')
            {
                if (ch == ' ')
                {
                    sb.append(ch);
                }
                else
                {
                    tokens.add(new YamlToken(YamlTokenType.whitespace, sb.toString()));
                    sb.setLength(0);
                }

                // no continue
            }
            else if (startingChar == '"')
            {
                sb.append(ch);
                if (ch == '"')
                {
                    tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
                    sb.setLength(0);
                }
                continue;
            }
            else if (startingChar == '\'')
            {
                sb.append(ch);
                if (ch == '\'')
                {
                    if (next == '\'')
                    {
                        consumedNext = true;
                    }
                    else
                    {
                        tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
                        sb.setLength(0);
                    }
                }
                continue;
            }

            if (ch == '\n')
            {
                if (sb.length() > 0)
                {
                    tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
                    sb.setLength(0);
                }

                sb.append(ch);
                tokens.add(new YamlToken(YamlTokenType.new_line, sb.toString()));
                sb.setLength(0);
            }
            else if (ch == ' ')
            {
                if (sb.length() == 1)
                {
                    if (sb.charAt(0) == '-')
                    {
                        sb.append(ch);

                        tokens.add(new YamlToken(YamlTokenType.array_index, sb.toString()));
                        sb.setLength(0);
                    }
                    else if (sb.charAt(0) == ':')
                    {
                        sb.append(ch);

                        tokens.add(new YamlToken(YamlTokenType.kvp_splitter, sb.toString()));
                        sb.setLength(0);
                    }
                }
                else
                {
                    sb.append(ch);
                }
            }
            else
            {
                if (ch == ':' && (next == ' ' || next == '\n'  || next == (char)0 ))
                {
                    if (sb.length() > 0)
                    {
                        tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
                        sb.setLength(0);
                    }

                    sb.append(ch);
                    if (next == ' ')
                    {
                        sb.append(next);
                        consumedNext = true;
                    }
                    tokens.add(new YamlToken(YamlTokenType.kvp_splitter, sb.toString()));
                    sb.setLength(0);
                }
                else if (ch == '#' && sb.charAt(sb.length() - 1) == ' ')
                {
                    if (sb.length() > 0)
                    {
                        tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
                        sb.setLength(0);
                    }

                    sb.append(ch);
                }
                else
                {
                    sb.append(ch);
                }
            }
        }

        if (sb.length() > 0)
        {
            tokens.add(new YamlToken(YamlTokenType.string, sb.toString()));
        }

        for (int i = 1; i < tokens.size(); i++)
        {
            tokens.get(i - 1).setNext(tokens.get(i));
        }

        System.out.println("\nend. Tokens:");
        System.out.println(tokens);

        return tokens;
    }

    private static class YamlLine
    {
        private final ArrayList<YamlToken> line;

        public YamlLine(ArrayList<YamlToken> line)
        {
            this.line = line;
        }

        public int getDepth()
        {
            if (line.isEmpty())
            {
                return 0;
            }

            if (line.get(0).type == YamlTokenType.whitespace)
            {
                return line.get(0).value.length();
            }

            return 0;
        }

        public YamlToken get(YamlTokenType type)
        {
            return line.stream().filter(token -> token.type == type).findFirst().orElse(null);
        }

        public YamlToken firstRealToken()
        {
            return this.line.stream()
                    .filter(token -> token.type != YamlTokenType.comment && token.type != YamlTokenType.whitespace && token.type != YamlTokenType.new_line)
                    .findFirst().orElse(null);
        }


        public YamlToken firstNonWhitespaceToken()
        {
            return this.line.stream()
                    .filter(token -> token.type != YamlTokenType.whitespace && token.type != YamlTokenType.new_line)
                    .findFirst().orElse(null);
        }

        public boolean hasComment()
        {
            return this.line.stream().anyMatch(token -> token.type == YamlTokenType.comment);
        }

        public boolean isEmpty()
        {
            return this.line.isEmpty() || this.line.get(0).type == YamlTokenType.new_line;
        }
    }

    private static class TokenReader
    {
        private final ArrayList<YamlToken> tokens;
        @Getter
        private int index;

        public TokenReader(ArrayList<YamlToken> tokens)
        {
            this.tokens = tokens;
            index = -1;
        }

        public YamlToken next()
        {
            return this.tokens.get(++this.index);
        }

        public YamlLine readLine()
        {
            ArrayList<YamlToken> line = new ArrayList<>();
            while (this.hasNext())
            {
                YamlToken t = this.next();
                line.add(t);

                if (t.type == YamlTokenType.new_line)
                {
                    return new YamlLine(line);
                }
            }

            return new YamlLine(line);
        }

        public YamlLine getNextRealLine()
        {
            YamlLine line = readLine();

            while (true)
            {
                if (line.isEmpty())
                {
                    return line;
                }

                if (line.firstRealToken() != null)
                {
                    return line;
                }

                line = readLine();
            }
        }

        public YamlLine getNextNonEmptyLine()
        {
            YamlLine line = readLine();

            while (true)
            {
                if (line.isEmpty())
                {
                    return line;
                }

                if (line.firstNonWhitespaceToken() != null)
                {
                    return line;
                }

                line = readLine();
            }
        }

        public YamlToken peek()
        {
            return this.tokens.get(this.index + 1);
        }

        public boolean hasNext()
        {
            return this.index + 1 < this.tokens.size();
        }

        public YamlToken get(int i)
        {
            return this.tokens.get(i);
        }

        public void seek(int i)
        {
            this.index = i;
        }

        public void rewindLine()
        {
            for (int i = this.index - 1; i >= 0; i--)
            {
                if (this.tokens.get(i).type == YamlTokenType.new_line)
                {
                    this.index = i + 1;
                    return;
                }
            }

            this.index = 0;
        }
    }

    private YamlValue parse(ArrayList<YamlToken> tokens) throws ParseException {
        if (tokens.isEmpty())
        {
            return null;
        }

        YamlToken token = tokens.stream().filter((t) -> t.type == YamlTokenType.string || t.type == YamlTokenType.array_index).findFirst().orElse(null);

        if (token == null)
        {
            throw new ParseException("", 0);
        }

        int index = tokens.indexOf(token);
        if (token.type == YamlTokenType.array_index)
        {
            int depth = 0;
            if (index > 0)
            {
                YamlToken prev = tokens.get(index - 1);
                if (prev.type == YamlTokenType.whitespace)
                {
                    depth = prev.value.length();
                }
                else if (prev.type != YamlTokenType.new_line)
                {
                    throw new ParseException("", 0);
                }
            }
            return parseArray(new TokenReader(tokens), new YamlArray(), depth);
        }
        else if (token.type == YamlTokenType.string)
        {
            YamlToken next = null;

            if (index + 1 < tokens.size())
            {
                next = tokens.get(index + 1);
            }

            if (next == null || next.type == YamlTokenType.new_line)
            {
                // depth doesn't matter for simple values as the root
                return parseSimpleValue(new TokenReader(tokens), new YamlSimpleValue(""), 0);
            }

            int depth = 0;
            if (index > 0)
            {
                YamlToken prev = tokens.get(index - 1);
                if (prev.type == YamlTokenType.whitespace)
                {
                    depth = prev.value.length();
                }
                else if (prev.type != YamlTokenType.new_line)
                {
                    throw new ParseException("", 0);
                }
            }

            return parseObject(new TokenReader(tokens), new YamlObject(), depth);
        }

        throw new ParseException("", 0);
    }

    private YamlObject parseObject(TokenReader reader, YamlObject object, int depth) throws ParseException {
        System.out.println("Reading object");

        while (true)
        {
            int lineStart = reader.getIndex();
            YamlLine line = reader.readLine();

            if (line.isEmpty())
            {
                return object;
            }

            int d = line.getDepth();

            if (d < depth)
            {
                reader.seek(lineStart);
                return object;
            }
            else if (d == depth)
            {
                YamlToken key = line.firstRealToken();
                if (key == null || key.type != YamlTokenType.string)
                {
                    throw new ParseException("Must be a string", -1);
                }

                System.out.println("key: " + key);

                YamlToken splitter = key.next();

                if (splitter == null || splitter.type != YamlTokenType.kvp_splitter)
                {
                    throw new ParseException("Must be a ':'", -1);
                }

                System.out.println("splitter: " + splitter);

                YamlToken value = splitter.next();

                System.out.println("value: " + value);

                if (value == null)
                {
                    object.set(key.value, null);
                    return object;
                }

                if (value.type == YamlTokenType.new_line || value.type == YamlTokenType.comment)
                {
                    // check next lines if creating new array, new object, or is string

                    int lastIndex = reader.getIndex();

                    YamlLine nextLine = reader.getNextRealLine();

                    int d2 = nextLine.getDepth();

                    if (d2 < depth)
                    {
                        object.set(key.value, null);
                        return object;
                    }
                    else if (d2 == depth)
                    {
                        object.set(key.value, null);
                    }
                    else // d2 > depth
                    {
                        // check if next is string or next is array indexer

                        YamlToken realToken = nextLine.firstRealToken();

                        reader.seek(lastIndex);
                        if (realToken.type == YamlTokenType.string)
                        {
                            object.set(key.value, parseSimpleValue(reader, new YamlSimpleValue(""), depth + 1));
                        }
                        else if (realToken.type == YamlTokenType.array_index)
                        {
                            object.set(key.value, parseArray(reader, new YamlArray(), d2));
                        }
                        else
                        {
                            throw new ParseException("Expected string or array", -1);
                        }
                    }
                }
                else if (value.type == YamlTokenType.string)
                {
                    object.set(key.value, parseSimpleValue(reader, new YamlSimpleValue(value.value.trim()), depth + 1));
                }
            }
            else // d > depth
            {
                throw new ParseException("", 0);
            }
        }
    }

    private YamlArray parseArray(TokenReader reader, YamlArray array, int depth) throws ParseException {
        System.out.println("Reading array");

        while (true) {
            int startLine = reader.getIndex();
            YamlLine line = reader.readLine();

            int d = line.getDepth();

            if (d < depth)
            {
                reader.seek(startLine);
                return array;
            }
            else if (d == depth)
            {
                YamlToken arrayIndex = line.firstRealToken();
                if (arrayIndex.type != YamlTokenType.array_index)
                {
                    throw new ParseException("expected array index", -1);
                }
                else
                {
                    System.out.println("new index");
                    YamlToken value = arrayIndex.next();
                    if (value.type == YamlTokenType.new_line || value.type == YamlTokenType.comment)
                    {
                        // check next lines if creating new array, new object, or is string

                        int lastIndex = reader.getIndex();

                        YamlLine nextLine = reader.getNextRealLine();

                        int d2 = nextLine.getDepth();

                        if (d2 < depth)
                        {
                            array.add(null);
                            return array;
                        }
                        else if (d2 == depth)
                        {
                            array.add(null);
                        }
                        else // d2 > depth
                        {
                            // check if next is string or next is array indexer

                            YamlToken realToken = nextLine.firstRealToken();

                            reader.seek(lastIndex);
                            if (realToken.type == YamlTokenType.string)
                            {
                                array.add(parseSimpleValue(reader, new YamlSimpleValue(""), depth + 1));
                            }
                            else if (realToken.type == YamlTokenType.array_index)
                            {
                                array.add(parseArray(reader, new YamlArray(), d2));
                            }
                            else
                            {
                                throw new ParseException("Expected string or array", -1);
                            }
                        }
                    }
                    else if (value.type == YamlTokenType.string)
                    {
                        // check if string or object

                        YamlToken splitter = value.next();

                        if (splitter != null && splitter.type == YamlTokenType.kvp_splitter)
                        {
                            YamlObject object = new YamlObject();

                            int objectDepth = depth + value.value.length();

                            YamlToken objectValue = splitter.next();

                            // dupe code
                            if (objectValue == null)
                            {
                                System.out.println("null array value");
                                object.set(value.value, null);
                                return array;
                            }

                            if (objectValue.type == YamlTokenType.new_line || objectValue.type == YamlTokenType.comment)
                            {
                                // check next lines if creating new array, new object, or is string

                                int lastIndex = reader.getIndex();

                                YamlLine nextLine = reader.getNextRealLine();

                                int d2 = nextLine.getDepth();

                                if (d2 < objectDepth)
                                {
                                    System.out.println("null array value");
                                    object.set(value.value, null);
                                }
                                else if (d2 == objectDepth)
                                {
                                    System.out.println("null array value");
                                    object.set(value.value, null);
                                }
                                else // d2 > objectDepth
                                {
                                    // check if next is string or next is array indexer

                                    YamlToken realToken = nextLine.firstRealToken();

                                    reader.seek(lastIndex);
                                    if (realToken.type == YamlTokenType.string)
                                    {
                                        object.set(value.value, parseSimpleValue(reader, new YamlSimpleValue(""), objectDepth + 1));
                                    }
                                    else if (realToken.type == YamlTokenType.array_index)
                                    {
                                        object.set(value.value, parseArray(reader, new YamlArray(), d2));
                                    }
                                    else
                                    {
                                        throw new ParseException("Expected string or array", -1);
                                    }
                                }
                            }
                            else if (value.type == YamlTokenType.string)
                            {
                                System.out.println("set object value to string");
                                object.set(value.value, parseSimpleValue(reader, new YamlSimpleValue(objectValue.value.trim()), objectDepth + 1));
                            }
                            // end dupe code

                            array.add(parseObject(reader, object, depth + value.value.length()));
                        }
                        else
                        {
                            array.add(parseSimpleValue(reader, new YamlSimpleValue(value.value.trim()), depth + 1));
                        }
                    }
                }
            }
            else // d > depth
            {
                throw new ParseException("expected to be handled by simple value or object", -1);
            }
        }
    }

    private YamlSimpleValue parseSimpleValue(TokenReader reader, YamlSimpleValue value, int depth)
    {
        int startIndex = reader.getIndex();

        while(true)
        {
            YamlLine line = reader.readLine();

            if (line.isEmpty())
            {
                if (line.get(YamlTokenType.new_line) != null)
                {
                    value.append("\n");
                }
            }

            if (line.hasComment())
            {
                YamlToken str = line.get(YamlTokenType.string);
                if (str != null)
                {
                    value.append(" ").append(str.value);
                }
                return value;
            }

            if (line.getDepth() >= depth)
            {
                for (YamlToken token : line.line)
                {
                    if (token.type != YamlTokenType.whitespace && token.type != YamlTokenType.new_line)
                    {
                        value.append(token.value);
                    }
                }
            }
            else
            {
                reader.seek(startIndex);
                return value;
            }
        }
    }

    public List<YamlValue> loadAll(String yamlString) throws IOException, ParseException {
        ArrayList<YamlValue> data = new ArrayList<>();
        for(String yamlStr : yamlString.split("---"))
        {
            data.add(this.load(new ByteArrayInputStream(yamlStr.getBytes(StandardCharsets.UTF_8))));
        }

        return data;
    }
}
