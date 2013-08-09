package eval;

import eval.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Class that can interpretate \BV source code.
 *
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class Interpreter {
    /**
     * Calculate value F(arg), where F - function defined in source, arg - 64bit number.
     *
     * @param source source that defines one function with one argument.
     * @param arg    argument passed to F
     * @return value F(arg).
     */
    public static long eval(String source, long arg) {
        Parser.Node root = Parser.parse(source);
        return new Interpreter(root).eval(arg);
    }

    /**
     * Eval(source, arg), for all args.
     */
    public static long[] eval(String source, long[] args) {
        long[] res = new long[args.length];
        Parser.Node root = Parser.parse(source);
        Interpreter interpreter = new Interpreter(root);
        for (int i = 0; i < args.length; i++) {
            res[i] = interpreter.eval(args[i]);
        }

        return res;
    }

    Parser.Node root;
    Stack<Map<String, Long>> callStack = new Stack<>();
    Map<String, Long> context = new HashMap<>();

    Interpreter(Parser.Node root) {
        this.root = root;
    }

    long eval(long value) {
        return callFunction(root, value);
    }

    long callFunction(Parser.Node function, long value) {
        String arg = function.children[0].name;
        Parser.Node body = function.children[1];
        callStack.push(context);
        context = new HashMap<>(context);
        context.put(arg, value);
        long res = evalExpression(body);
        context = callStack.pop();
        return res;
    }

    long callFunction(Parser.Node function, long val1, long val2) {
        String arg1 = function.children[0].name;
        String arg2 = function.children[1].name;

        callStack.push(context);

        context.put(arg1, val1);
        context.put(arg2, val2);

        long res = evalExpression(function.children[2]);

        context = callStack.pop();

        return res;
    }

    long evalExpression(Parser.Node exp) {
        if (exp.name.equals("if0"))
            return evalIf0(exp);
        else if (exp.name.equals("fold"))
            return evalFold(exp);
        else if (exp.name.equals("not"))
            return evalNot(exp);
        else if (exp.name.equals("shl1"))
            return evalShl1(exp);
        else if (exp.name.equals("shr1"))
            return evalShr1(exp);
        else if (exp.name.equals("shr4"))
            return evalShr4(exp);
        else if (exp.name.equals("shr16"))
            return evalShr16(exp);
        else if (exp.name.equals("and"))
            return evalAnd(exp);
        else if (exp.name.equals("or"))
            return evalOr(exp);
        else if (exp.name.equals("xor"))
            return evalXor(exp);
        else if (exp.name.equals("plus"))
            return evalPlus(exp);
        else if (exp.type == Parser.Node.NodeType.ID) {
            if (!context.containsKey(exp.name))
                throw new RuntimeException("Undefined variable: " + exp.name);
            return context.get(exp.name);
        } else if (exp.type == Parser.Node.NodeType.CONST) {
            return exp.val;
        } else {
            throw new RuntimeException("WTF??");
        }
    }

    long evalIf0(Parser.Node exp) {
        long cond = evalExpression(exp.children[0]);

        if (cond == 0)
            return evalExpression(exp.children[1]);
        else
            return evalExpression(exp.children[2]);
    }

    long evalFold(Parser.Node exp) {
        long acc = evalExpression(exp.children[1]);
        long arr = evalExpression(exp.children[0]);

        for (int i = 0; i < 8; i++) {
            long cur = arr & 255;
            arr >>= 8;
            acc = callFunction(exp.children[2], cur, acc);
        }

        return acc;
    }

    long evalNot(Parser.Node exp) {
        long val = evalExpression(exp.children[0]);
        return ~val;
    }

    long evalShl1(Parser.Node exp) {
        long val = evalExpression(exp.children[0]);
        return val << 1;
    }

    long evalShr1(Parser.Node exp) {
        long val = evalExpression(exp.children[0]);
        return val >> 1;
    }

    long evalShr4(Parser.Node exp) {
        long val = evalExpression(exp.children[0]);
        return val >> 4;
    }

    long evalShr16(Parser.Node exp) {
        long val = evalExpression(exp.children[0]);
        return val >> 16;
    }

    long evalAnd(Parser.Node exp) {
        long val1 = evalExpression(exp.children[0]);
        long val2 = evalExpression(exp.children[1]);
        return val1 & val2;
    }

    long evalOr(Parser.Node exp) {
        long val1 = evalExpression(exp.children[0]);
        long val2 = evalExpression(exp.children[1]);
        return val1 | val2;
    }

    long evalXor(Parser.Node exp) {
        long val1 = evalExpression(exp.children[0]);
        long val2 = evalExpression(exp.children[1]);
        return val1 ^ val2;
    }

    long evalPlus(Parser.Node exp) {
        long val1 = evalExpression(exp.children[0]);
        long val2 = evalExpression(exp.children[1]);
        return val1 + val2;
    }
}
