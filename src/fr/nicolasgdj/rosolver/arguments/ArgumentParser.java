package fr.nicolasgdj.rosolver.arguments;

public class ArgumentParser {

    private String[] arguments;

    public ArgumentParser(String[] arguments) {
        this.arguments = arguments;
    }

    public void parse() throws IllegalArgumentException {
        EOption currentOption = null;
        for (String argument : arguments) {
            boolean foundNewOption = false;
            for (EOption option : EOption.values()) {
                if (option.match(argument)) {
                    currentOption = option;
                    foundNewOption = true;
                    break;
                }
            }
            if (foundNewOption && currentOption.getType() != EOptionType.Boolean) {
                continue;
            }
            EOption option = currentOption == null ? EOption.getDefault() : currentOption;
            if (option == null) {
                throw new IllegalArgumentException("Argument '" + argument + "' is unexcepted.");
            }
            switch (option.getType()) {
                case Boolean:
                    option.set(true);
                    currentOption = null;
                    break;
                case String:
                    option.set(argument);
                    currentOption = null;
                    break;
                case ListOfString:
                    option.add(argument);
                    break;
                default:
                    break;
            }
        }
        for (EOption option : EOption.values()) {
            if (option.isRequired() && !option.has()) {
                throw new IllegalArgumentException("Argument " + option.name() + " is required. Please check --help.");
            }
        }
    }

}
