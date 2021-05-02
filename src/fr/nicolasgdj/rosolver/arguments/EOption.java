package fr.nicolasgdj.rosolver.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum EOption {

    PL("", EOptionType.String, true),
    SIMPLEXE("(-s|--simplexe)", EOptionType.Boolean, false, false),
    SOLVE_PL("(-p|--pl)", EOptionType.Boolean, false, false),
    HELP("(-h|--help)", EOptionType.Boolean, false, false);

    private String regex;
    private boolean required;
    private EOptionType type;
    private Object value;
    private int minArgument;
    private int maxArgument;


    EOption(String regex, int minArgument, int maxArgument, boolean required) {
        this(regex, minArgument, maxArgument, required, new ArrayList<String>());
    }

    EOption(String regex, int minArgument, int maxArgument, boolean required, Object defaultValue) {
        this(regex, EOptionType.ListOfString, required, defaultValue);
        this.minArgument = minArgument;
        this.maxArgument = maxArgument;
    }

    EOption(String regex, EOptionType type, boolean required) {
        this(regex, type, required, null);
    }

    EOption(String regex, EOptionType type, boolean required, Object defaultValue) {
        this.regex = regex;
        this.type = type;
        this.required = required;
        this.value = defaultValue;
    }

    public boolean match(String name) {
        return Pattern.matches(this.regex, name);
    }

    public boolean has() {
        if (value == null)
            return false;
        if (type == EOptionType.ListOfString) {
            int size = getAsList().size();
            return minArgument <= size && size <= maxArgument;
        }
        return true;
    }

    public boolean isRequired() {
        return this.required;
    }

    public Object get() {
        return this.value;
    }

    public EOptionType getType() {
        return this.type;
    }

    public List<String> getAsList() {
        return (List<String>) ((ArrayList<String>) get()).clone();
    }

    public boolean getAsBoolean() {
        return (Boolean) get();
    }

    public String getAsString() {
        return (String) get();
    }

    public boolean isDefault() {
        return regex.isEmpty();
    }

    public void set(Object value) {
        this.value = value;
    }

    public void add(String value) {
        if (type == EOptionType.ListOfString) {
            ((ArrayList<String>) this.value).add(value);
        } else {
            set(value);
            //TODO: Maybe throw exception ??
        }
    }

    public static EOption getDefault() {
        for (EOption option : values()) {
            if (option.isDefault())
                return option;
        }
        return null;
    }

}
