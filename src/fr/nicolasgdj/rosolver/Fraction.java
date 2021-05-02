package fr.nicolasgdj.rosolver;

public class Fraction {
    private int num, denum;

    public Fraction(int num) {
        this(num, 1);
    }

    public Fraction(int num, int denum) {
        this.num = num;
        this.denum = denum;
        simplify();

    }

    public int getDenum() {
        return denum;
    }

    public int getNum() {
        return num;
    }

    public void simplify() {
        int pgcd = pgcd(num, denum);
        num /= pgcd;
        denum /= pgcd;

        if (denum < 0) {
            num *= -1;
            denum *= -1;
        }
    }

    @Override
    public String toString() {
        if (denum == 1) {
            return "" + num;
        }
        return num + "/" + denum;
    }

    public Fraction add(int i) {
        return add(new Fraction(i));
    }

    public Fraction add(Fraction f) {
        return new Fraction(num * f.denum + f.num * denum, f.denum * denum);
    }

    public Fraction sub(int i) {
        return sub(new Fraction(i));
    }

    public Fraction sub(Fraction f) {
        return new Fraction(num * f.denum - f.num * denum, f.denum * denum);
    }

    public Fraction mult(int i) {
        return mult(new Fraction(i));
    }

    public Fraction mult(Fraction f) {
        return new Fraction(num * f.num, f.denum * denum);
    }

    public Fraction inverse() {
        return new Fraction(denum, num);
    }

    public Fraction divide(int i) {
        return divide(new Fraction(i));
    }

    public Fraction divide(Fraction f) {
        return mult(f.inverse());
    }

    public double val() {
        return (num * 1.0) / (denum * 1.0);
    }

    public Fraction abs() {
        if(val() < 0)
            return mult(-1);
        return this;
    }
    public static int pgcd(int a, int b) {
        if (b == 0) return a;
        return pgcd(b, a % b);
    }

    @Override
    public Object clone() {
        return new Fraction(this.num, this.denum);
    }


}
