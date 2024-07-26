package org.qwikpe.callback.handler.util.uhi;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.qwikpe.callback.handler.exception.CustomException;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.WebClientUtiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@EnableScheduling
public class UhiWebClientUtilImpl implements UhiWebClientUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(UhiWebClientUtilImpl.class);

    private final ExchangeStrategies exchangeStrategies =
            ExchangeStrategies.builder()
                    .codecs(
                            clientCodecConfigurer ->
                                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 16))
                    .build();


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
                            String uri, Map<String, String> headers, Object body, Class<T> type, int maxRetryCount) throws RuntimeException {
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
                    .uri(uri)
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
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount);
        } catch (WebClientResponseException.Unauthorized | WebClientResponseException.Forbidden wr) {
            LOGGER.error("postMethod :: Unauthorized or forbidden error, so retrying, uri: {}", uri, wr);

            --maxRetryCount;
            if (maxRetryCount == 0) {
                LOGGER.info(
                        "postMethod :: unauthorized or forbidden after trying : {}", Constants.MAX_RETRY);
                throw new CustomException("unauthorized or forbidden after trying " + Constants.MAX_RETRY, wr.getStatusCode().value());
            }
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount);
        } catch (Exception e) {
            LOGGER.error("postMethod :: Error occurred while consuming the Api: {}, so retrying", uri, e);
            --maxRetryCount;

            if (maxRetryCount == 0) {
                WebClientResponseException wr = ((WebClientResponseException) e);
                LOGGER.info("postMethod :: error occurred while calling on uri: {}, try: {}", uri, Constants.MAX_RETRY);
                throw new CustomException("error occurred after trying " + Constants.MAX_RETRY + "times \n" + wr.getResponseBodyAsString(), wr.getStatusCode().value());
            }
            return postMethod(baserUrl, uri, headers, body, type, maxRetryCount);
        }
    }
}
