package network;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class ResponseUtils {
    static public long[] parseResponse(String response) {
        response = response.replaceAll("[\\[\\]\"\"]", "");
        String[] tokens = response.split(",");
        long[] res = new long[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String hexRepr = tokens[i].toLowerCase().substring(2);
            long curPow = 1;
            for (int j = hexRepr.length() - 1; j >= 0; --j) {
                res[i] += hexDigitToInt(hexRepr.charAt(j)) * curPow;
                curPow *= 16l;
            }
        }
        return res;
    }

    static int hexDigitToInt(char c) {
        if ('a' <= c && c <= 'z')
            return c - 'a' + 10;
        return c - '0';
    }

    @SuppressWarnings("unused")
    static public String toString(long[] arr) {
        StringBuilder res = new StringBuilder();
        for (long item : arr) {
            res.append(Long.toHexString(item)).append(" ");
        }
        return res.toString().trim();
    }
}
