package fr.nicolasgdj.rosolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PL {

    List<Fraction> values;
    PLType operation;
    Fraction result;

    List<Integer> y = new ArrayList<>();

    public PL(List<Fraction> values, PLType operation, Fraction result) {
        this.values = values;
        this.operation = operation;
        this.result = result;
    }

    public Fraction getResult() {
        return result;
    }

    public List<Fraction> getValues() {
        return values;
    }

    public PLType getOperation() {
        return operation;
    }

    public void setY(int indice){
        if(!y.contains(indice))
            y.add(indice);
    }

    public void mult(int value) {
        mult(new Fraction(value));
    }

    public void mult(Fraction fraction) {
        for(int i = 0; i < getValues().size(); ++i) {
            getValues().set(i, getValues().get(i).mult(fraction));
        }
        if(fraction.val() < 0)
            switch (operation){
                case INF ->  operation = PLType.SUP;
                case SUP ->  operation = PLType.INF;
            }
        result = result.mult(fraction);
    }

    public boolean has(int indice) {
        if(getMaxIndice() < indice)
            return false;
        return values.get(indice-1).getNum() != 0;
    }

    public Fraction get(int indice) {
        if(!has(indice))
            return null;
        return values.get(indice-1);
    }

    public boolean isCanonique() {
        return getOperation() == PLType.INF || getOperation() == PLType.EQ;
    }

    public boolean isNormal(){
        return getOperation() == PLType.EQ;
    }

    public void set(int indice, Fraction value) {
        if(indice > getMaxIndice()) {
            while(indice != getMaxIndice()) {
                values.add(new Fraction(0));
            }
        }
        values.set(indice-1, value);
    }

    @Override
    public String toString() {
        String txt = "";
        boolean first = true;
        int lastX = 1;
        for(int i = 1; i <= getValues().size(); ++i) {
            Fraction val = getValues().get(i-1);
            if(val.val() < 0) {
                txt += " - " + (val.getNum() == -1 ? "" : val.mult(-1));
            } else if(val.getNum() == 0) {
                continue;
            } else {
                txt +=  (first ? "" : " + ") + (val.getNum() == 1 ? "" : val);
            }
            first = false;
            if(y.contains(i)) {
                txt += "y" + getIndice(i-lastX-1);
            } else {
                txt += "x" + getIndice(i);
                lastX = i;
            }
        }
        txt += " " + getOperation().getSymbol() + " " + getResult();
        return txt;
    }

    public int getMaxIndice(){
        return getValues().size();
    }

    public static final String INDICE = "₀₁₂₃₄₅₆₇₈₉";

    public static String getIndice(int i){
        String result = "";
        String txt = "" + i;
        for(String c : txt.split("")) {
            Integer j = Integer.parseInt(c);
            result+= INDICE.charAt(j);
        }
        return result;
    }

    // 1x1 + 2x2 + 3x3 + 4x <= 5
    public static PL parse(String txt) {
        String[] split = txt.split(" ");

        HashMap<Integer, Fraction> valuesMap = new HashMap<>();
        int maxI = 0;
        int toAddToResult = 0;
        boolean isMinus = false;
        boolean result = false;
        Fraction resultF = null;
        PLType op = null;
        for(String s : split) {
            if(!result) {
                PLType type = PLType.getBySymbol(s);
                if (type == null) {
                    String[] splt = s.split("x");
                    if(splt.length == 2) {
                        Fraction value = new Fraction(Integer.parseInt(splt[0]));
                        if(isMinus)
                            value = value.mult(-1);
                        int i = Integer.parseInt(splt[1]);
                        if(i > maxI) {
                            maxI = i;
                        }
                        valuesMap.put(i, value);
                    } else {
                        if(s.equals("-")) {
                            isMinus = true;
                            continue;
                        } else if(s.equals("+")) {
                            continue;
                        }
                        toAddToResult += Integer.parseInt(s);
                    }
                    isMinus = false;
                } else {
                    op = type;
                    result = true;
                }
            } else {
                if(s.equals("-")) {
                    isMinus = true;
                    continue;
                } else if(s.equals("+")) {
                    continue;
                }
                resultF = new Fraction( Integer.parseInt(s) - toAddToResult);
            }
        }
        if(!result || maxI == 0)
            return null;
        List<Fraction> values = new ArrayList<>();
        for(int i = 1; i <= maxI; ++i) {
            values.add(valuesMap.containsKey(i) ? valuesMap.get(i) : new Fraction(0));
        }
        return new PL(values, op, resultF);
    }



    public static void main(String[] args) {
        String test = "1x1 + 2x5 - 2x2 + 5 <= 5";
        PL pl = parse(test);
        System.out.println(pl);
    }

    public void setOperation(PLType op) {
        this.operation = op;
    }

    public void add(PL pl) {
        for(int i = 1; i <= pl.getMaxIndice(); ++i) {
            if(!has(i)) {
                values.set(i-1, (Fraction) pl.getValues().get(i-1).clone());
            } else {
                values.set(i-1, values.get(i-1).add(pl.getValues().get(i-1)));
            }
        }
        result = result.add(pl.getResult());

    }

    @Override
    public Object clone(){
        List<Fraction> values = new ArrayList<>();
        for(Fraction f : getValues())
            values.add((Fraction) f.clone());
        return new PL(values, operation, (Fraction) getResult().clone());
    }

    public void setResult(Fraction fraction) {
        this.result = fraction;
    }

    public void removeY() {
        for(int i : y) {
            values.remove(i-1);
        }
        y.clear();
    }
}
