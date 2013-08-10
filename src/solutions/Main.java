package solutions;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
public class Main {

    public void run() {
        new Solver().getProblems();
        /*for (int i = 0; i < 5; i++) {
            new Solver().run();
            try {
                Thread.sleep(25000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } */
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
