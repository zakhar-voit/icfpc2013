package solutions;

import eval.Interpreter;
import eval.parser.Parser;
import network.Submitter;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class BruteforceSolution {
    final boolean DEBUG;

    final Submitter submitter;

    final int ARGS_CNT = 20;
    final long MAX_ARG = (long) 1e15;
    final int DEEP = 3;

    final Parser.Node MAIN_ARG = new Parser.Node(Parser.Node.NodeType.ID, "x0", 0);

    //final String[] UNARY_OPS = {"not", "shl1", "shr1", "shr4", "shr16"};
    final String[] UNARY_OPS = {"shl1", "shr4"};
    final String[] BINARY_OPS = {"and", "or", "xor", "plus"};

    long[] args, result;

    Parser.Node root = new Parser.Node(Parser.Node.NodeType.FUNCTION1, "lambda", 0, MAIN_ARG, null);

    Parser.Node[] nextChildren;
    int nextChild;
    int nextDeep;

    int currentVar = 1;

    public BruteforceSolution(Submitter submitter) {
        this.submitter = submitter;
        this.DEBUG = false;
    }

    public BruteforceSolution(Submitter submitter, boolean debug) {
        this.submitter = submitter;
        this.DEBUG = debug;
    }

    long[] generateArgs() {
        Random rnd = new Random();
        long[] res = new long[ARGS_CNT];
        for (int i = 0; i < ARGS_CNT; i++) {
            res[i] = Math.abs(rnd.nextLong()) % MAX_ARG;
        }

        /* Generate 0b011111... */
        res[0] = 1;
        for (int i = 0; i < 62; i++)
            res[0] = res[0] + res[0] + 1;

        return res;
    }

    boolean tryToSubmit() {
        if (Arrays.equals(Interpreter.eval(root, args), result)
                && submitter.guess(root.toString())) {
            if (DEBUG)
                System.out.println(root);
            return true;
        }
        return false;
    }

    boolean findNextChild(Parser.Node cur, int deep) {
        if (cur == null)
            throw new RuntimeException("something wrong...");

        for (int i = 0; i < cur.children.length; i++) {
            if (cur.children[i] == null) {
                nextChildren = cur.children;
                nextChild = i;
                nextDeep = deep;
                return true;
            }
            if (findNextChild(cur.children[i], deep + 1))
                return true;
        }

        return false;
    }

    boolean rec() {
        if (!findNextChild(root, 0)) {
            if (tryToSubmit())
                return true;
        } else {
            Parser.Node[] currentChildren = nextChildren;
            int currentChild = nextChild;
            int currentDeep = nextDeep;

            /* Make const node */
            for (int val = 0; val < 2; val++) {
                currentChildren[currentChild] = new Parser.Node(Parser.Node.NodeType.CONST, "const", val);
                if (rec())
                    return true;
            }

            /* Make ID node */
            for (int name = 0; name < currentVar; name++) {
                currentChildren[currentChild] = new Parser.Node(Parser.Node.NodeType.ID, "x" + name, 0);
                if (rec())
                    return true;
            }

            if (currentDeep < DEEP) {
                /* Make unary operations */
                for (String unaryOp : UNARY_OPS) {
                    currentChildren[currentChild] =
                            new Parser.Node(Parser.Node.NodeType.UNARY_OP, unaryOp, 0, new Parser.Node[]{null});
                    if (rec())
                        return true;
                }

                /* Make binary operations */
                for (String binaryOp : BINARY_OPS) {
                    currentChildren[currentChild] =
                            new Parser.Node(Parser.Node.NodeType.BINARY_OP, binaryOp, 0, null, null);
                    if (rec())
                        return true;
                }

            }

            currentChildren[currentChild] = null;
        }
        return false;
    }

    boolean solve() {
        args = generateArgs();
        result = submitter.eval(args);
        return rec();
    }
}

