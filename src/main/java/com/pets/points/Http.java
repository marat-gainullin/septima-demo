package com.pets.points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.pets.points.demo.LoopbackDereferenceDemoPoint;
import com.septima.application.Config;
import com.septima.application.endpoint.Answer;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Http {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .executor(Config.lookupExecutor())
            .build();

    private static final ObjectReader JSON_ARRAY_READER = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .reader()
            .forType(List.class);

    private Http() {
        throw new AssertionError("No instances expected");
    }

    public static CompletableFuture<String> fetch(String anUrl) {
        Logger.getLogger(LoopbackDereferenceDemoPoint.class.getName()).log(Level.INFO, "About to fetch an url: " + anUrl);
        return HTTP
                .sendAsync(
                        HttpRequest.newBuilder(URI.create(anUrl)).GET().build(),
                        HttpResponse.BodyHandler.asString()
                )
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return response.body();
                    } else {
                        throw new IllegalStateException("Failed to fetch the url: " + anUrl + "\nstatus: " + response.statusCode() + "; body: \n" + response.body());
                    }
                });
    }

    public static List<Map<String, Object>> reviveDates(String aKey, List<Map<String, Object>> raw) {
        StdDateFormat format = new StdDateFormat();
        return raw.stream()
                .map(d -> {
                    try {
                        Map<String, Object> revived = new HashMap<>(d);
                        String dateText = (String) d.get(aKey);
                        revived.put(aKey, dateText != null ? format.parse(dateText) : null);
                        return revived;
                    } catch (ParseException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public static CompletableFuture<List<Map<String, Object>>> fetchJsonArray(String anUrl) {
        return fetch(anUrl)
                .thenApply(content -> {
                    try {
                        return JSON_ARRAY_READER.readValue(content);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });
    }

    public static void withTailKey(Answer answer, Consumer<String> action, Runnable withoutKey) {
        String pathInfo = answer.getRequest().getPathInfo();
        if (pathInfo != null && !pathInfo.isEmpty() && pathInfo.length() > 1) {
            action.accept(pathInfo.substring(1));
        } else {
            withoutKey.run();
        }
    }
}
