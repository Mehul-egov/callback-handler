package org.qwikpe.callback.handler.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.qwikpe.callback.handler.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@EnableScheduling
public class WebClientUtiImpl implements WebClientUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebClientUtiImpl.class);

    @Value("${secret.abdm.clientId}")
    private String clientId;

    @Value("${secret.abdm.clientSecret}")
    private String clientSecret;

    private final ExchangeStrategies exchangeStrategies =
            ExchangeStrategies.builder()
                    .codecs(
                            clientCodecConfigurer ->
                                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 16))
                    .build();

    @Getter
    public String accessToken = null;

    @PostConstruct
    public void init() {
        setAbdmAccessToken();
    }

    @Scheduled(cron = "0 */15 * * * *")
    private void refreshToken() {
        setAbdmAccessToken();
    }

    public void setAbdmAccessToken() {
        Map<String, String> secretMap =
                new HashMap<>() {
                    {
                        put("clientId", clientId);
                        put("clientSecret", clientSecret);
                        put("grantType", "client_credentials");
                    }
                };

        JsonNode jsonNode =
                getWebClient().mutate()
                        .baseUrl("https://dev.abdm.gov.in/")
                        .build()
                        .post()
                        .uri("gateway/v0.5/sessions")
                        .bodyValue(secretMap)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

        accessToken = jsonNode.get("accessToken").asText();
    }

    private WebClient getWebClient() {
        HttpClient httpClient =
                HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                        .responseTimeout(Duration.ofSeconds(60))
                        .doOnConnected(
                                conn ->
                                        conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                                                .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public <T> T postMethod(String baserUrl,
                            String uri, Map<String, String> headers, Object body, Class<T> type, int maxRetryCount, MultiValueMap<String, String> param) throws RuntimeException {

        String modifiedUri;
        if(param != null){
            modifiedUri = UriComponentsBuilder.fromPath(uri).queryParams(param).toUriString();
        }else {
            modifiedUri = UriComponentsBuilder.fromPath(uri).toUriString();
        }

        try {
            return getWebClient().mutate()
                    .baseUrl(baserUrl)
                    .defaultHeaders(
                            httpHeaders -> {
                                httpHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
                                httpHeaders.setAccept(List.of(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE)));
                                for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                                    httpHeaders.add(stringStringEntry.getKey(), stringStringEntry.getValue());
                                }
                            })
                    .build()
                    .post()
                    .uri(modifiedUri)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(type)
                    .block();
        } catch (WebClientRequestException webClientRequestException) {
            LOGGER.error(
                    "postMethod :: webclient request exception while calling on uri: {}, retying",
                    uri,
                    webClientRequestException);

            --maxRetryCount;
            if (maxRetryCount == 0) {
                LOGGER.info(
                        "postMethod :: webclient request exceptions occurred after trying : {}", Constants.MAX_RETRY);
                throw new CustomException("webclient request exceptions occurred after trying" + Constants.MAX_RETRY, 500);
            }
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount, null);
        } catch (WebClientResponseException.Unauthorized | WebClientResponseException.Forbidden wr) {
            LOGGER.error("postMethod :: Unauthorized or forbidden error, so retrying, uri: {}", uri, wr);

            --maxRetryCount;
            if (maxRetryCount == 0) {
                LOGGER.info(
                        "postMethod :: unauthorized or forbidden after trying : {}", Constants.MAX_RETRY);
                throw new CustomException("unauthorized or forbidden after trying " + Constants.MAX_RETRY, wr.getStatusCode().value());
            }
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount, null);
        } catch (Exception e) {
            LOGGER.error("postMethod :: Error occurred while consuming the Api: {}, so retrying", uri, e);
            --maxRetryCount;

            if (maxRetryCount == 0) {
                WebClientResponseException wr = ((WebClientResponseException) e);
                LOGGER.info("postMethod :: error occurred while calling on uri: {}, try: {}", uri, Constants.MAX_RETRY);
                throw new CustomException("error occurred after trying " + Constants.MAX_RETRY + "times \n" + wr.getResponseBodyAsString(), wr.getStatusCode().value());
            }
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount, null);
        }
    }

    @Override
    public <T> T postMethod(String baserUrl,
                            Map<String, String> headers, Object body, Class<T> type,
                            int maxRetryCount) throws RuntimeException {

        try {
            return getWebClient().mutate()
                    .baseUrl(baserUrl)
                    .defaultHeaders(
                            httpHeaders -> {
                                httpHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
                                httpHeaders.setAccept(List.of(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE)));
                                httpHeaders.setBearerAuth(accessToken);
                                for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                                    httpHeaders.add(stringStringEntry.getKey(), stringStringEntry.getValue());
                                }
                            })
                    .build()
                    .post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(type)
                    .block();
        } catch (WebClientRequestException webClientRequestException) {
            LOGGER.error(
                    "postMethod :: webclient request exception while calling on uri: {}, retying",baserUrl,
                    webClientRequestException);

            --maxRetryCount;
            if (maxRetryCount == 0) {
                LOGGER.info(
                        "postMethod :: webclient request exceptions occurred after trying : {}", Constants.MAX_RETRY);
                throw new CustomException("webclient request exceptions occurred after trying" + Constants.MAX_RETRY, 500);
            }
            return postMethod(baserUrl, headers, body, type, maxRetryCount);
        } catch (WebClientResponseException.Unauthorized | WebClientResponseException.Forbidden wr) {
            LOGGER.error("postMethod :: Unauthorized or forbidden error, so retrying, uri: {}", baserUrl, wr);

            --maxRetryCount;
            if (maxRetryCount == 0) {
                LOGGER.info(
                        "postMethod :: unauthorized or forbidden after trying : {}", Constants.MAX_RETRY);
                throw new CustomException("unauthorized or forbidden after trying " + Constants.MAX_RETRY, wr.getStatusCode().value());
            }
            return postMethod(baserUrl, headers, body, type, maxRetryCount);
        } catch (Exception e) {
            LOGGER.error("postMethod :: Error occurred while consuming the Api: {}, so retrying", baserUrl, e);
            --maxRetryCount;

            if (maxRetryCount == 0) {
                WebClientResponseException wr = ((WebClientResponseException) e);
                LOGGER.info("postMethod :: error occurred while calling on uri: {}, try: {}", baserUrl, Constants.MAX_RETRY);
                throw new CustomException("error occurred after trying " + Constants.MAX_RETRY + "times \n" + wr.getResponseBodyAsString(), wr.getStatusCode().value());
            }
            return postMethod(baserUrl, headers, body, type, maxRetryCount);
        }
    }
}
