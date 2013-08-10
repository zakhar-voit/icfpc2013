package solutions;

import network.LocalSubmitter;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class BruteforceTest {
    static final String PROGRAM = "(lambda (x) (plus 1 1))";

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        System.out.println(new BruteforceSolution(new LocalSubmitter(PROGRAM), true).solve());
        System.out.println(System.currentTimeMillis() - time);
    }
}
