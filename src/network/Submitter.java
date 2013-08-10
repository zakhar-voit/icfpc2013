package network;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public interface Submitter {
    public long[] eval(long[] args);

    public boolean guess(String program);

    public boolean isAllowed(String operand);
}
