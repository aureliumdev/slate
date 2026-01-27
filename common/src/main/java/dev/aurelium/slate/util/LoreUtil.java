package dev.aurelium.slate.util;

import dev.aurelium.slate.lore.type.TextLore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LoreUtil {

    public static String getStyle(String input) {
        int firstColorCode = input.indexOf("§");
        if (firstColorCode != -1) {
            int currentPos = firstColorCode;
            while (input.charAt(currentPos) == '§') {
                currentPos += 2;
            }
            return input.substring(firstColorCode, currentPos);
        } else {
            return "";
        }
    }

    public static String wrapLore(String input, int maxLength) {
        return wrapLore(input, maxLength, "\n");
    }

    public static String wrapLore(String input, int maxLength, String insertion) {
        return wrapLore(input, maxLength, null, (line, lore) -> insertion);
    }

    public static String wrapLore(String input, int maxLength, TextLore textLore, BiFunction<String, TextLore, String> insertionFunction) {
        StringBuilder sb = new StringBuilder(input);

        int i = 0;
        List<String> lines = new ArrayList<>();
        while (i < input.length()) {
            String sub = substringIgnoreFormatting(sb.toString(), i, Math.min(i + maxLength, input.length()));
            int addedLength = 0;
            if (!sub.equals(" ")) {
                String added = substringIgnoreFormatting(sub, 0, Math.min(maxLength, sub.length()));
                int lastSpace = added.lastIndexOf(" ");
                if (lastSpace != -1) { // Check if section contains spaces
                    if (sb.charAt(Math.min(i + added.length(), sb.length() - 1)) == ' ' || i + added.length() == sb.length()) { // Complete word or last word
                        lines.add(added);
                        addedLength = added.length();
                    } else { // Cut off word
                        String addedCutOff = added.substring(0, lastSpace);
                        addedLength = addedCutOff.length();
                        lines.add(addedCutOff);
                    }
                } else { // Add the max number of characters and cut off word
                    lines.add(added);
                    addedLength = added.length();
                }
            }
            i = i + addedLength;
            if (i < sb.length()) {
                if (sb.charAt(i) == ' ') {
                    i++;
                }
            }
        }
        StringBuilder output = new StringBuilder();
        String lastInsertion = "";
        for (String line : lines) {
            String insertion = insertionFunction.apply(line, textLore);
            lastInsertion = insertion;
            output.append(line).append(insertion);
        }
        output.replace(output.length() - lastInsertion.length(), output.length(), "");
        return output.toString();
    }

    private static String substringIgnoreFormatting(String input, int start, int end) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        if (start < 0 || end > input.length() || start > end) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean insideBrackets = false;
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '<') {
                insideBrackets = true;
            }
            if (c == '>') {
                insideBrackets = false;
            }
            if (!insideBrackets || count <= start) {
                count++;
            }
            if (count > start && count <= end) {
                result.append(c);
            }
            if (count > end) {
                break;
            }
        }

        return result.toString();
    }

    public static String toNewlineString(List<String> lore) {
        StringBuilder sb = new StringBuilder();
        for (String line : lore) {
            sb.append(line);
            sb.append("\n");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

}
