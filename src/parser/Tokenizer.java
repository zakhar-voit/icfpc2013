package parser;

import java.util.ArrayList;

/**
 * Lexic analyzer of \BV source code
 *
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class Tokenizer {
    final String source;
    int pos = 0;

    /**
     * Use this function to get list of tokens from some source code.
     *
     * @param sourceCode \BV source code
     * @return list of tokens
     */
    public static ArrayList<Token> getTokens(String sourceCode) {
        Tokenizer tok = new Tokenizer(sourceCode);
        tok.parseTokens();
        return tok.tokens;
    }

    public static final class Token {
        static enum TokenType {
            OPEN_PAR,
            CLOSE_PAR,
            CONST,
            ID
        }

        public final TokenType type;
        public long constVal;
        public String idName;

        public Token(TokenType type) {
            this.type = type;
        }

        public Token(String s) {
            this.type = TokenType.ID;
            this.idName = s;
        }

        public Token(long val) {
            this.type = TokenType.CONST;
            this.constVal = val;
        }
    }

    ArrayList<Token> tokens;

    Tokenizer(String source) {
        this.source = source;
    }

    void parseTokens() {
        while (pos < source.length()) {
            parseNextToken();
        }
    }

    boolean isLetterOrDigit(char c) {
        return ('a' <= c && c <= 'z') || ('0' <= c && c <= '9');
    }

    void parseNextId() {
        char c = source.charAt(pos);
        if ('a' <= c && c <= 'z') {
            StringBuilder res = new StringBuilder();
            res.append(c);
            ++pos;
            while (pos < source.length() && isLetterOrDigit(source.charAt(pos))) {
                res.append(source.charAt(pos));
            }
            tokens.add(new Token(res.toString()));
        } else {
            throw new RuntimeException("Bad character at pos: " + pos);
        }
    }

    void parseNextToken() {
        /*
        Skip whitespace
         */
        while (pos < source.length()
                && " \n\t".contains("" + source.charAt(pos))) // check if current character is whitespace
            ++pos;
        if (pos == source.length())
            return;

        /*
        Try to parse parenthesises
         */
        if (source.charAt(pos) == '(') {
            tokens.add(new Token(Token.TokenType.OPEN_PAR));
            ++pos;
        } else if (source.charAt(pos) == ')') {
            tokens.add(new Token(Token.TokenType.CLOSE_PAR));
            ++pos;
        }
        /*
        Try to parse numeric constant(0 or 1)
         */
        else if (source.charAt(pos) == '0' || source.charAt(pos) == '1') {
            tokens.add(new Token(source.charAt(pos) - '0'));
            ++pos;
        }
        /*
        Try to parse identifier
         */
        else {
            parseNextId();
        }
    }
}
