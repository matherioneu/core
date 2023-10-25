package eu.matherion.core.shared.commons;

import com.google.common.collect.Maps;
import cz.maku.mommons.Mommons;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class Rests {

    public static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .build();

    public static <T> Optional<T> get(String url, Class<T> clazz, Map<String, String> headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .build();

        Call call = CLIENT.newCall(request);
        Response response = call.execute();
        ResponseBody body = response.body();
        if (body == null) return Optional.empty();
        return Optional.of(Mommons.GSON.fromJson(body.string(), clazz));
    }

    public static <T> Optional<T> get(String url, Class<T> clazz) throws IOException {
        return get(url, clazz, Maps.newHashMap());
    }
}
