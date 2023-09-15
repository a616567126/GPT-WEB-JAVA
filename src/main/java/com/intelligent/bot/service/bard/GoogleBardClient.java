package com.intelligent.bot.service.bard;


import com.intelligent.bot.model.bard.Answer;
import com.intelligent.bot.model.bard.AnswerStatus;
import com.intelligent.bot.model.bard.BardRequest;
import com.intelligent.bot.model.bard.BardResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static com.intelligent.bot.constant.CommonConst.EMPTY_STRING;
import static com.intelligent.bot.utils.bard.BardUtils.*;
import static com.intelligent.bot.utils.bard.WebUtils.okHttpClientWithTimeout;


@Slf4j
public class GoogleBardClient implements AIClient {
    private final String token;
    private final OkHttpClient httpClient;
    private final BardRequest bardRequest = BardRequest.newEmptyBardRequest();

    public GoogleBardClient(String token) {
        this(token, Duration.ofMinutes(5));
    }

    public GoogleBardClient(String token, Duration timeout) {
        this.token = token;
        this.httpClient = okHttpClientWithTimeout(timeout);
    }

    public GoogleBardClient(String token, OkHttpClient httpClient) {
        this.token = token;
        this.httpClient = httpClient;
    }

    @Override
    public Answer ask(String question) {
        try {
            if (isEmpty(bardRequest.getStrSNlM0e())) {
                String strSNlM0e = callBardToGetSNlM0e();
                if (isEmpty(strSNlM0e)) {
                    throw new RuntimeException("Failed to get SNlM0e, it may be token issue");
                }
                bardRequest.setStrSNlM0e(strSNlM0e);
            }

            bardRequest.setQuestion(question);
            return processAskResult(callBardToAsk(bardRequest));
        } catch (Throwable e) {
            log.error("Failed to get answer: ", e);
            return Answer.builder().status(AnswerStatus.ERROR).build();
        }
    }

    @Override
    public void reset() {
        bardRequest.setStrSNlM0e(EMPTY_STRING);
        bardRequest.setConversationId(EMPTY_STRING);
        bardRequest.setResponseId(EMPTY_STRING);
        bardRequest.setChoiceId(EMPTY_STRING);
    }

    private String callBardToGetSNlM0e() {
        Call call = this.httpClient.newCall(createRequestForSNlM0e(token));
        try {
            try (Response response = call.execute()) {
                log.info("getSNlM0e Response code: " + response.code());
                return fetchSNlM0eFromBody(Objects.requireNonNull(response.body()).string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String callBardToAsk(BardRequest bardRequest) {
        log.info("calling Bard with request {}", bardRequest);
        Call call = this.httpClient.newCall(createPostRequestForAsk(token, bardRequest));

        try {
            try (Response response = call.execute()) {
                int statusCode = response.code();
                log.info("Ask Response code: " + statusCode);

                if (statusCode != 200) {
                    throw new IllegalStateException("Can't get the answer");
                }

                String result = Objects.requireNonNull(response.body()).string().split("\\n")[3];

                log.debug("Result for ask: {}", result);
                log.debug("Raw answers length: {}", result.length());

                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Answer processAskResult(String content) {
        BardResponse bardResponse = renderBardResponseFromResponse(content);

        bardRequest.setConversationId(bardResponse.getConversationId());
        bardRequest.setResponseId(bardResponse.getResponseId());
        bardRequest.setChoiceId(bardResponse.getChoiceId());

        return bardResponse.getAnswer();
    }
}
