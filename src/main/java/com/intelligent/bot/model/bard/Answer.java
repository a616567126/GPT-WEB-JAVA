package com.intelligent.bot.model.bard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class Answer {
    private final AnswerStatus status;

    private final String chosenAnswer;

    private final List<Image> images;

    public String markdown() {

        String markdown = this.chosenAnswer;

        if (images != null && images.size() > 0) {
            for (Image image : images) {
                markdown = markdown.replaceFirst(image.labelRegex(), image.markdown());
            }

        }

        return markdown;
    }

}
