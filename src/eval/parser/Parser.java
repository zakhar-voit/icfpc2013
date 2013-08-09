package eval.parser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that builds syntax tree of \BV language
 *
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class Parser {
    /**
     * Use this function to get root node of syntax tree.
     *
     * @param source source code
     * @return root node of syntax tree
     */
    public static Node parse(String source) {
        return parse(Tokenizer.getTokens(source));
    }

    static Node parse(ArrayList<Tokenizer.Token> tokens) {
        Parser parser = new Parser(tokens);
        parser.nextToken();
        return parser.parseFunction1();
    }

    ArrayList<Tokenizer.Token> tokens;
    int pos = 0;
    Tokenizer.Token currentToken = null;

    public static final class Node {
        public static enum NodeType {
            FUNCTION1,
            FUNCTION2,
            ID,
            CONST,
            UNARY_OP,
            BINARY_OP,
            TERNARY_OP
        }

        public NodeType type;
        public String name;
        public long val;
        public Node[] children;

        @Override
        public String toString() {
            return "Node{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", val=" + val +
                    ", children=" + Arrays.toString(children) +
                    '}';
        }

        public Node(NodeType type, String name, long val, Node... children) {
            this.type = type;
            this.name = name;
            this.val = val;
            this.children = children;
        }
    }

    public Parser(ArrayList<Tokenizer.Token> tokens) {
        this.tokens = tokens;
    }

    void nextToken() {
        if (pos > tokens.size()) {
            throw new RuntimeException("Unexpected end of tokens.");
        } else if (pos == tokens.size()) {
            currentToken = null;
        } else {
            currentToken = tokens.get(pos++);
        }
    }

    void skipOpenPar() {
        if (currentToken.type == Tokenizer.Token.TokenType.OPEN_PAR) {
            nextToken();
        } else {
            throw new RuntimeException("Expected '(', but found: " + currentToken);
        }
    }

    void skipClosePar() {
        if (currentToken.type == Tokenizer.Token.TokenType.CLOSE_PAR) {
            nextToken();
        } else {
            throw new RuntimeException("Expected ')', but found: " + currentToken);
        }
    }

    boolean isOp1(String s) {
        return s.equals("not") || s.equals("shl1")
                || s.equals("shr1") || s.equals("shr4") || s.equals("shr16");
    }

    boolean isOp2(String s) {
        return s.equals("and") || s.equals("or") || s.equals("xor") || s.equals("plus");
    }

    Node parseFunction1() {
        Node res;
        /*
        Skip parenthesis.
         */
        if (currentToken.type == Tokenizer.Token.TokenType.OPEN_PAR) {
            skipOpenPar();
            res = parseFunction1();
            skipClosePar();
        }

        /*
        If current token is "lambda", try to parse function definition.
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && currentToken.idName.equals("lambda")) {
            nextToken();
            Node id = parseId();
            Node e = parseExpression();
            res = new Node(Node.NodeType.FUNCTION1, "lambda", 0, id, e);
        } else {
            throw new RuntimeException("Expected 'lambda', but found: " + currentToken);
        }

        return res;
    }

    Node parseExpression() {
        Node res;

        /*
        Skip parenthesis.
         */
        if (currentToken.type == Tokenizer.Token.TokenType.OPEN_PAR) {
            skipOpenPar();
            res = parseExpression();
            skipClosePar();
        }

        /*
        Try to parse if0 expression
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && currentToken.idName.equals("if0")) {
            res = parseIf0();
        }

        /*
        Try to parse fold expression
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && currentToken.idName.equals("fold")) {
            res = parseFold();
        }

        /*
        Try to parse op1
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && isOp1(currentToken.idName)) {
            res = parseOp1();
        }

        /*
        Try to parse op2
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && isOp2(currentToken.idName)) {
            res = parseOp2();
        }
        /*
        Try to parse numeric constant
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.CONST) {
            res = new Node(Node.NodeType.CONST, "const", currentToken.constVal);
            nextToken();
        }

        /*
        Try to parse identifier
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID) {
            res = parseId();
        } else {
            throw new RuntimeException("Expected expression, but found: " + currentToken);
        }

        return res;
    }

    Node parseIf0() {
        nextToken();
        return new Node(Node.NodeType.TERNARY_OP, "if0", 0,
                parseExpression(), parseExpression(), parseExpression());
    }

    Node parseFold() {
        nextToken();
        return new Node(Node.NodeType.TERNARY_OP, "fold", 0,
                parseExpression(), parseExpression(), parseFunction2());
    }

    Node parseOp1() {
        String op = currentToken.idName;
        nextToken();
        return new Node(Node.NodeType.UNARY_OP, op, 0,
                parseExpression());
    }

    Node parseOp2() {
        String op = currentToken.idName;
        nextToken();
        return new Node(Node.NodeType.BINARY_OP, op, 0,
                parseExpression(), parseExpression());
    }

    Node parseFunction2() {
        Node res;

        /*
        Skip parenthesis.
         */
        if (currentToken.type == Tokenizer.Token.TokenType.OPEN_PAR) {
            skipOpenPar();
            res = parseFunction2();
            skipClosePar();
        }

        /*
        Try to parse lambda function
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID
                && currentToken.idName.equals("lambda")) {
            nextToken();
            skipOpenPar();
            Node id1 = parseId();
            Node id2 = parseId();
            skipClosePar();

            res = new Node(Node.NodeType.FUNCTION2, "lambda", 0,
                    id1, id2, parseExpression());
        } else {
            throw new RuntimeException("Expected lambda, but found: " + currentToken);
        }

        return res;
    }

    Node parseId() {
        Node res;
        /*
        Skip parenthesis.
         */
        if (currentToken.type == Tokenizer.Token.TokenType.OPEN_PAR) {
            skipOpenPar();
            res = parseId();
            skipClosePar();
        }

        /*
        If current token is identifier, return it.
         */
        else if (currentToken.type == Tokenizer.Token.TokenType.ID) {
            res = new Node(Node.NodeType.ID, currentToken.idName, 0);
            nextToken();
        } else {
            throw new RuntimeException("Expected identifier, but found: " + currentToken);
        }

        return res;
    }
}
