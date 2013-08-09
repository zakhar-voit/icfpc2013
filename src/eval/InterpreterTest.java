package eval;

import network.Network;
import network.ResponceUtils;

import java.util.Arrays;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class InterpreterTest {
    static final String[] PROGRAMS =
            {
                    "(lambda (x_61405) (fold x_61405 x_61405 (lambda (x_61406 x_61407) (xor (shl1 (shr4 (shl1 (or (shr1 (shr16 (plus (if0 (shl1 (shl1 (shr16 x_61407))) (shr16 x_61407) x_61407) x_61406))) x_61407)))) x_61407))))"
            };

    static final long[][] ARGS =
            {
                    {777, 666, 123, 546}
            };

    public static void main(String[] args) {
        for (int i = 0; i < PROGRAMS.length; i++) {
            long[] serverAnswer = ResponceUtils.parseResponse(Network.eval(PROGRAMS[i], ARGS[i]));
            long[] localAnswer = Interpreter.eval(PROGRAMS[i], ARGS[i]);
            assert Arrays.equals(serverAnswer, localAnswer);
        }
    }
}
