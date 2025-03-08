package ru.noxly.simulation.webClients.dyndns;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.noxly.simulation.exceptions.AppError;
import ru.noxly.simulation.exceptions.GeneralException;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.noxly.simulation.common.Constants.WEB_CLIENT_DYNDNS_BEAN;
import static ru.noxly.simulation.utils.JsonUtil.toJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class DyndnsWebClient {

    @Autowired
    @Qualifier(WEB_CLIENT_DYNDNS_BEAN)
    private WebClient webClient;

    @Value("${services.dyndns.url}")
    private String baseUrl;

    /**
     * Выполнение POST-запроса
     */
    public <T, R> R post(String url, T body, Class<R> responseType) {
        val fullPath = baseUrl + url;
        log.info(format("Запрос на dyndns сервер method = [%s], path = [%s], body = [%s]", POST, fullPath, toJson(body)));

        return webClient.post()
                .uri(url)
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        handleErrorResponse(response, url).flatMap(Mono::error) // Возвращаем ошибку асинхронно
                )
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Выполнение GET-запроса
     */
    public <R> R get(String url, Class<R> responseType) {
        val fullPath = baseUrl + url;
        log.info(format("Запрос на dyndns сервер method = [%s], path = [%s]", GET, fullPath));

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        handleErrorResponse(response, url).flatMap(Mono::error) // Возвращаем ошибку асинхронно
                )
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Выполнение PUT-запроса
     */
    public <T, R> R put(String url, T body, Class<R> responseType) {
        val fullPath = baseUrl + url;
        log.info(format("Запрос на dyndns сервер method = [%s], path = [%s], body = [%s]", PUT, fullPath, toJson(body)));

        return webClient.put()
                .uri(url)
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        handleErrorResponse(response, url).flatMap(Mono::error) // Возвращаем ошибку асинхронно
                )
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Обработка ошибок
     */
    private Mono<GeneralException> handleErrorResponse(org.springframework.web.reactive.function.client.ClientResponse response, String url) {
        return response.bodyToMono(AppError.class)
                .map(error -> {
                    log.error("Ошибка HTTP [{}]: {} - {}", url, error.getStatus(), error.getMessage());
                    return new GeneralException(error.getStatus(), error.getMessage());
                })
                .onErrorResume(ex -> {
                    log.error("Ошибка при разборе ответа от [{}]", url, ex);
                    return Mono.just(new GeneralException(response.statusCode().value(), "Не удалось обработать ошибку от сервера"));
                });
    }
}