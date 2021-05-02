package fr.nicolasgdj.rosolver;

public enum PLType {
    INF("<="),
    SUP(">="),
    EQ("="),
    ;
    private String symbol;
    PLType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static PLType getBySymbol(String symbol){
        for(PLType type : values()) {
            if(type.getSymbol().equals(symbol))
                return type;
        }
        return null;
    }
}
