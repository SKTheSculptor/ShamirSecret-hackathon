import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShamirSecret {
    static final class Frac {
        BigInteger num, den;

        Frac(BigInteger n, BigInteger d) {
            if (d.signum() == 0) throw new ArithmeticException("Denominator is zero");
            if (d.signum() < 0) { n = n.negate(); d = d.negate(); }
            BigInteger g = n.gcd(d);
            num = n.divide(g);
            den = d.divide(g);
        }

        static Frac of(long n) { return new Frac(BigInteger.valueOf(n), BigInteger.ONE); }
        static Frac of(BigInteger n) { return new Frac(n, BigInteger.ONE); }

        Frac add(Frac o) { return new Frac(num.multiply(o.den).add(o.num.multiply(den)), den.multiply(o.den)); }
        Frac mul(Frac o) { return new Frac(num.multiply(o.num), den.multiply(o.den)); }
        Frac div(Frac o) { return new Frac(num.multiply(o.den), den.multiply(o.num)); }
    }

    static class Point {
        long x;
        BigInteger y;
        Point(long x, BigInteger y) { this.x = x; this.y = y; }
    }

    static class ParsedInput {
        int n, k;
        List<Point> points = new ArrayList<>();
    }
    static ParsedInput parse(String json) {
        ParsedInput pi = new ParsedInput();
        Matcher mk = Pattern.compile("\"keys\"\\s*:\\s*\\{[^}]*\"n\"\\s*:\\s*(\\d+)\\s*,\\s*\"k\"\\s*:\\s*(\\d+)[^}]*\\}",
                Pattern.DOTALL).matcher(json);
        if (!mk.find()) throw new RuntimeException("Could not parse n and k");
        pi.n = Integer.parseInt(mk.group(1));
        pi.k = Integer.parseInt(mk.group(2));
        Matcher ms = Pattern.compile(
                "\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*\\}",
                Pattern.DOTALL).matcher(json);

        while (ms.find()) {
            long x = Long.parseLong(ms.group(1));
            int base = Integer.parseInt(ms.group(2).trim());
            String val = ms.group(3).trim();
            BigInteger y = new BigInteger(val, base);
            pi.points.add(new Point(x, y));
        }

        if (pi.points.size() < pi.k)
            throw new RuntimeException("Not enough shares for k");
        return pi;
    }
    static BigInteger lagrangeAtZero(List<Point> pts) {
        Frac sum = Frac.of(0);
        int m = pts.size();

        for (int i = 0; i < m; i++) {
            long xi = pts.get(i).x;
            BigInteger yi = pts.get(i).y;
            Frac term = Frac.of(yi);

            for (int j = 0; j < m; j++) {
                if (i == j) continue;
                long xj = pts.get(j).x;
                Frac num = new Frac(BigInteger.valueOf(-xj), BigInteger.ONE);
                Frac den = new Frac(BigInteger.valueOf(xi - xj), BigInteger.ONE);
                term = term.mul(num).div(den);
            }
            sum = sum.add(term);
        }

        if (!sum.den.equals(BigInteger.ONE)) {
            throw new RuntimeException("Non-integer secret: " + sum.num + "/" + sum.den);
        }
        return sum.num;
    }

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
        }

        String json = sb.toString().trim();
        if (json.isEmpty()) {
            System.err.println("Please provide the JSON input");
            return;
        }

        ParsedInput pi = parse(json);
        List<Point> subset = pi.points.subList(0, pi.k);

        BigInteger secret = lagrangeAtZero(subset);
        System.out.println(secret);
    }
}

