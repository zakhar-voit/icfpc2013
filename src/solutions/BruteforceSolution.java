package solutions;

import eval.Interpreter;
import eval.parser.Parser;
import network.Submitter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class BruteforceSolution {
    final boolean DEBUG;
    final boolean USE_TERNARY_OPERATIONS = false;

    final Submitter submitter;

    final int ARGS_CNT = 20;
    final long MAX_ARG = (long) 1e15;
    final int DEEP = 4;
    final int SIZE = 6;

    int size = 0;

    long time;

    final Parser.Node MAIN_ARG = new Parser.Node(Parser.Node.NodeType.ID, "x0", 0);

    final String[] UNARY_OPS = {"not", "shl1", "shr1", "shr4", "shr16"};
    final String[] BINARY_OPS = {"and", "or", "xor", "plus"};

    long[] args, result;

    Parser.Node root = new Parser.Node(Parser.Node.NodeType.FUNCTION1, "lambda", 0, MAIN_ARG, null);

    Parser.Node[] nextChildren;
    Parser.Node nextParent;
    int nextChild;
    int nextDeep;

    int currentVar = 1;

    @SuppressWarnings("unused")
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
            if (DEBUG) {
                System.out.println(root);
                System.out.println("Work time: " + (System.currentTimeMillis() - time) + " ms");
            }
            return true;
        }
        return false;
    }

    class BfsPosition {
        Parser.Node[] children;
        int pos;
        int deep;

        public BfsPosition(Parser.Node[] children, int pos, int deep) {
            this.children = children;
            this.pos = pos;
            this.deep = deep;
        }
    }

    @SuppressWarnings("unused")
    boolean findNextChildBfs(Parser.Node cur, int deep) {
        if (cur == null)
            throw new RuntimeException("something wrong");

        assert cur == root;

        Queue<BfsPosition> q = new ArrayDeque<>();
        q.add(new BfsPosition(cur.children, 1, 0));

        while (!q.isEmpty()) {
            BfsPosition top = q.poll();
            Parser.Node[] curChildren = top.children;
            int pos = top.pos;
            int curDeep = top.deep;

            if (curChildren[pos] == null) {
                nextChildren = curChildren;
                nextChild = pos;
                nextDeep = curDeep;
                return true;
            }
            for (int i = 0; i < curChildren[pos].children.length; i++) {
                q.add(new BfsPosition(curChildren[pos].children, i, curDeep + 1));
            }
        }

        return false;
    }

    boolean findNextChildDfs(Parser.Node cur, int deep) {
        if (cur == null)
            throw new RuntimeException("something wrong...");

        for (int i = 0; i < cur.children.length; i++) {
            if (cur.children[i] == null) {
                nextChildren = cur.children;
                nextChild = i;
                nextDeep = deep;
                nextParent = cur;
                return true;
            }
            if (findNextChildDfs(cur.children[i], deep + 1))
                return true;
        }

        return false;
    }

    boolean rec(boolean wasFold) {
        if (!findNextChildDfs(root, 0)) {
            if (tryToSubmit())
                return true;
        } else {
            @SuppressWarnings("all")
            Parser.Node[] currentChildren = nextChildren;
            Parser.Node currentParent = nextParent;
            int currentChild = nextChild;
            int currentDeep = nextDeep;

            /* Make const node */
            for (int val = 0; val < 2; val++) {
                currentChildren[currentChild] = new Parser.Node(Parser.Node.NodeType.CONST, "const", val);
                ++size;
                if (rec(wasFold))
                    return true;
                --size;
            }

            /* Make ID node */
            for (int name = 0; name < currentVar; name++) {
                currentChildren[currentChild] = new Parser.Node(Parser.Node.NodeType.ID, "x" + name, 0);
                ++size;
                if (rec(wasFold))
                    return true;
                --size;
            }

            if (currentDeep < DEEP && size < SIZE) {
                /* Make unary operations */
                for (String unaryOp : UNARY_OPS) {
                    if (!submitter.isAllowed(unaryOp))
                        continue;

                    /* (not not) -> () heuristic */
                    if (currentParent.name.equals("not") && unaryOp.equals("not"))
                        continue;

                    currentChildren[currentChild] =
                            new Parser.Node(Parser.Node.NodeType.UNARY_OP, unaryOp, 0, new Parser.Node[]{null});
                    ++size;
                    if (rec(wasFold))
                        return true;
                    --size;
                }

                /* Make binary operations */
                for (String binaryOp : BINARY_OPS) {
                    if (!submitter.isAllowed(binaryOp))
                        continue;
                    currentChildren[currentChild] =
                            new Parser.Node(Parser.Node.NodeType.BINARY_OP, binaryOp, 0, null, null);
                    ++size;
                    if (rec(wasFold))
                        return true;
                    --size;
                }

                if (USE_TERNARY_OPERATIONS) {

                /* Make fold(once)*/
                    if (!wasFold && submitter.isAllowed("fold")) {
                        wasFold = true;
                        Parser.Node firstArg = new Parser.Node(Parser.Node.NodeType.ID, "x" + currentVar++, 0);
                        Parser.Node secondArg = new Parser.Node(Parser.Node.NodeType.ID, "x" + currentVar++, 0);
                        currentChildren[currentChild] =
                                new Parser.Node(Parser.Node.NodeType.TERNARY_OP, "fold", 0, null, null,
                                        new Parser.Node(Parser.Node.NodeType.FUNCTION2, "lambda", 0,
                                                firstArg, secondArg, null));
                        ++size;
                        if (rec(wasFold))
                            return true;
                        --size;
                    }

                /* Make if0 */
                    if (submitter.isAllowed("if0")) {
                        currentChildren[currentChild] =
                                new Parser.Node(Parser.Node.NodeType.TERNARY_OP, "if0", 0, null, null, null);
                        ++size;
                        if (rec(wasFold))
                            return true;
                        --size;
                    }
                }
            }

            currentChildren[currentChild] = null;
        }
        return false;
    }

    boolean solve() {
        args = generateArgs();
        result = submitter.eval(args);
        time = System.currentTimeMillis();
        return rec(false);
    }
}

