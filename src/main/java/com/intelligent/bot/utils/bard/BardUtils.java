package com.intelligent.bot.utils.bard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.intelligent.bot.model.bard.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intelligent.bot.constant.CommonConst.*;

@Slf4j
public class BardUtils {

    public static Request.Builder createBuilderWithBardHeader(String token) {
        String[] tokens = token.split(";");
        if (tokens.length < 2) {
            throw new RuntimeException("Please provide the correct token:" + TOKEN_COOKIE_1PSID + ";" + TOKEN_COOKIE_1PSIDTS);
        }
        String token1PSID = tokens[0];
        String token1PSIDTS = tokens[1];
        return new Request.Builder()
                .addHeader("Host", HOSTNAME)
                .addHeader("Content-Type", CONTENT_TYPE)
                .addHeader("X-Same-Domain", "1")
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Origin", BASE_URL)
                .addHeader("Referer", BASE_URL)
                .addHeader("Cookie", TOKEN_COOKIE_1PSID + "=" + token1PSID
                        + ";" + TOKEN_COOKIE_1PSIDTS +
                        "=" + token1PSIDTS);
    }

    public static Request createRequestForSNlM0e(String token) {
        Request.Builder headerBuilder = createBuilderWithBardHeader(token);
        return headerBuilder.url(BASE_URL)
                .build();
    }

    public static String fetchSNlM0eFromBody(String input) {
        Matcher matcher = Pattern.compile("SNlM0e\":\"(.*?)\"").matcher(input);
        if (matcher.find()) {
            String result = matcher.group();
            return result.substring(9, result.length() - 1);
        }
        return null;
    }

    @NotNull
    public static Map<String, String> genQueryStringParamsForAsk() {
        int randomNumber = ThreadLocalRandom.current().nextInt(0, 10000) + 100000;

        Map<String, String> params = new HashMap<>();
        params.put("bl", BARD_VERSION);
        params.put("_reqid", String.valueOf(randomNumber));
        params.put("rt", "c");
        return params;
    }

    @NotNull
    public static HttpUrl.Builder createHttpBuilderForAsk() {
        HttpUrl.Builder httpBuilder = Objects
                .requireNonNull(HttpUrl.parse(BASE_URL + ASK_QUESTION_PATH))
                .newBuilder();

        for (Map.Entry<String, String> param : genQueryStringParamsForAsk().entrySet()) {
            httpBuilder.addQueryParameter(param.getKey(), param.getValue());
        }

        return httpBuilder;
    }

    /**
     * remove backslash \ in answer string
     */
    public static String removeBackslash(String answerStr) {
        return answerStr
                .replace("\\\\n", "\n")
                .replace("\\", "\"");
    }

    @NotNull
    public static Request createPostRequestForAsk(String token, BardRequest bardRequest) {
        return createBuilderWithBardHeader(token)
                .url(createHttpBuilderForAsk().build())
                .method("POST", buildRequestBodyForAsk(bardRequest))
                .build();
    }

    @NotNull
    public static RequestBody buildRequestBodyForAsk(BardRequest bardRequest) {
        String question = bardRequest.getQuestion().replace("\"", "\\\\\\\"");
        return new FormBody.Builder()
                .add("f.req", String.format("[null,\"[[\\\"%s\\\"],null,[\\\"%s\\\",\\\"%s\\\",\\\"%s\\\"]]\"]",
                        question, bardRequest.getConversationId(), bardRequest.getResponseId(), bardRequest.getChoiceId()))
                .add("at", bardRequest.getStrSNlM0e())
                .build();
    }

    public static BardResponse renderBardResponseFromResponse(String content) {
        Answer.AnswerBuilder builder = Answer.builder();
        String conversationId = "";
        String responseId = "";
        String choiceId = "";

        try {
            JsonArray chatData = getChatData(content);

            builder.chosenAnswer(getChosenAnswer(chatData));

            conversationId = ((JsonArray) chatData.get(1)).get(0).getAsString();
            responseId = ((JsonArray) chatData.get(1)).get(1).getAsString();
            choiceId = ((JsonArray) ((JsonArray) chatData.get(4)).get(0)).get(0).getAsString();

            List<Image> images = new ArrayList<>();

            try {
                images = getImages(chatData);
            } catch (Exception e) {
                log.info("No image");
            }

            builder.images(images);

            return new BardResponse(
                    conversationId,
                    responseId,
                    choiceId,
                    builder.status(AnswerStatus.OK).build()
            );

        } catch (Exception e) {
            return new BardResponse(
                    conversationId,
                    responseId,
                    choiceId,
                    builder.status(AnswerStatus.NO_ANSWER).build()
            );
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static JsonArray getChatData(String content) {
        JsonArray contentJsonArray = new Gson().fromJson(content, JsonArray.class);
        String element3content = ((JsonArray) contentJsonArray.get(0)).get(2).getAsString();
        return new Gson().fromJson(element3content, JsonArray.class);
    }

    private static String getChosenAnswer(JsonArray chatData) {
        return removeBackslash(((JsonArray) ((JsonArray) chatData.get(4)).get(0)).get(1).getAsString());
    }

    private static List<Image> getImages(JsonArray chatData) {
        List<Image> images = new ArrayList<>();

        JsonArray imagesJson = (JsonArray) (((JsonArray) ((JsonArray) chatData.get(4)).get(0)).get(4));

        for (JsonElement jsonElement : imagesJson) {
            JsonArray imageJson = (JsonArray) jsonElement;

            String url = ((JsonArray) ((JsonArray) imageJson.get(0)).get(0)).get(0).getAsString();
            String markdownLabel = imageJson.get(2).getAsString();
            String articleURL = ((JsonArray) ((JsonArray) imageJson.get(1)).get(0)).get(0).getAsString();

            Image image = new Image(url, markdownLabel, articleURL);
            images.add(image);
        }

        return images;
    }
}
