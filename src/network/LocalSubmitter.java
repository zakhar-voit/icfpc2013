package network;

import eval.Interpreter;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class LocalSubmitter implements Submitter {
    final Interpreter interpreter;

    public boolean isAllowed(String operand) {
        return false; // NO!
    }

    public LocalSubmitter(String sourceCode) {
        interpreter = new Interpreter(sourceCode);
    }

    @Override
    public long[] eval(long[] args) {
        return interpreter.eval(args);
    }

    @Override
    public boolean guess(String program) {
        System.out.println(program);
        return true;
    }
}
