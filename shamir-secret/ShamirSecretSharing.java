import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretSharing {

    // Large prime modulus (commonly used in Shamir's Secret Sharing)
    private static final BigInteger PRIME = new BigInteger(
        "208351617316091241234326746312124448251235562226470491514186331217050270460481"
    );

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java ShamirSecretSharing <input.json>");
            return;
        }

        // Read JSON input
        String content = Files.readString(Paths.get(args[0]));
        JSONObject json = new JSONObject(content);

        int k = json.getJSONObject("keys").getInt("k");

        // Collect first k points (x, y)
        List<BigInteger> xVals = new ArrayList<>();
        List<BigInteger> yVals = new ArrayList<>();

        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject obj = json.getJSONObject(key);
            int base = Integer.parseInt(obj.getString("base"));
            String valueStr = obj.getString("value");

            // Convert value from given base to BigInteger
            BigInteger y = new BigInteger(valueStr, base);

            xVals.add(BigInteger.valueOf(x));
            yVals.add(y);

            if (xVals.size() == k) break; // we only need k points
        }

        // Perform Lagrange interpolation at x = 0
        BigInteger secret = lagrangeInterpolation(BigInteger.ZERO, xVals, yVals, PRIME);
        System.out.println("Secret (constant term): " + secret);
    }

    /**
     * Lagrange interpolation to find f(x) at a given point, modulo p.
     */
    private static BigInteger lagrangeInterpolation(BigInteger x,
            List<BigInteger> xVals,
            List<BigInteger> yVals,
            BigInteger mod) {

        BigInteger result = BigInteger.ZERO;
        int k = xVals.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = xVals.get(i);
            BigInteger yi = yVals.get(i);

            BigInteger term = yi;
            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xj = xVals.get(j);

                // (x - xj) * (xi - xj)^(-1) mod p
                BigInteger numerator = x.subtract(xj).mod(mod);
                BigInteger denominator = xi.subtract(xj).mod(mod);

                BigInteger inv = denominator.modInverse(mod); // modular inverse
                term = term.multiply(numerator).mod(mod);
                term = term.multiply(inv).mod(mod);
            }
            result = result.add(term).mod(mod);
        }
        return result;
    }
}
