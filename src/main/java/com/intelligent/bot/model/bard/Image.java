package com.intelligent.bot.model.bard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Image {
    private final String url;
    private final String label;
    private final String article;

    public String markdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("[!");
        sb.append(label);
        sb.append("(");
        sb.append(url);
        sb.append(")](");
        sb.append(article);
        sb.append(")");

        return sb.toString();
    }

    public String labelRegex() {
        String temp = label.substring(1, label.length() -1);

        return "\\[" + temp + "\\]";
    }


}
