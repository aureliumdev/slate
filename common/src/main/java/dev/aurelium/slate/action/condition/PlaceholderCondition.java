package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.SlateLibrary;

public class PlaceholderCondition extends Condition {

    private final String placeholder;
    private final String value;
    private final Compare compare;

    public PlaceholderCondition(SlateLibrary slate, String placeholder, String value, Compare compare) {
        super(slate);
        this.placeholder = placeholder;
        this.value = value;
        this.compare = compare;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getValue() {
        return value;
    }

    public Compare getCompare() {
        return compare;
    }

    public enum Compare {

        EQUALS((left, right) -> {
            try {
                double leftDouble = Double.parseDouble(left);
                double rightDouble = Double.parseDouble(right);
                return Double.compare(leftDouble, rightDouble) == 0;
            } catch (NumberFormatException e) {
                return left.equals(right);
            }
        }),
        GREATER_THAN((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble > rightDouble;
        }),
        GREATER_THAN_OR_EQUALS((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble >= rightDouble;
        }),
        LESS_THAN((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble < rightDouble;
        }),
        LESS_THAN_OR_EQUALS((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble <= rightDouble;
        });

        private final ComparisonTest test;

        Compare(ComparisonTest test) {
            this.test = test;
        }

        public boolean test(String left, String right) throws NumberFormatException {
            return test.test(left, right);
        }

    }

    interface ComparisonTest {

        boolean test(String left, String right);

    }

}
