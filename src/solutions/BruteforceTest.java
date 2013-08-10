package solutions;

import network.ServerSubmitter;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class BruteforceTest {
    static final String PROGRAM = "(lambda (x_2624) (shr4 (shr4 x_2624)))";

    public static void main(String[] args) {
        System.out.println(new BruteforceSolution(new ServerSubmitter(PROGRAM), true).solve());
    }
}
