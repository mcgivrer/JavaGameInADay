package com.snapgames.framework.utils;

import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class I18n {

    private static final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");

    public static String getI18n(String name) {
        return replaceTemplate(messages.getString(name), messages);
    }

    public static String getI18n(String keyMsg, Object... args) {
        return String.format(getI18n(keyMsg), args);
    }

    public static String replaceTemplate(String template, ResourceBundle values) {
        StringTokenizer tokenizer = new StringTokenizer(template, "${}", true);
        StringJoiner joiner = new StringJoiner("");

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (token.equals("$")) {
                if (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();

                    if (token.equals("{") && tokenizer.hasMoreTokens()) {
                        String key = tokenizer.nextToken();

                        if (tokenizer.hasMoreTokens() && tokenizer.nextToken().equals("}")) {
                            String value = values.containsKey(key) ? values.getString(key) : "${" + key + "}";
                            joiner.add(value);
                        }
                    }
                }
            } else {
                joiner.add(token);
            }
        }

        return joiner.toString();
    }

}
