package com.ryccoatika.Music.Lyrics.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.ryccoatika.Music.Lyrics.model.LyricsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {

    @Autowired
    private Environment mEnv;

    @Bean(name = "com.linecorp.channel_secret")
    public String getChannelSecret() { return mEnv.getProperty("com.linecorp.channel_secret"); }

    @Bean(name="com.linecorp.channel_access_token")
    public String getChannelAccessToken() { return mEnv.getProperty("com.linecorp.channel_access_token"); }

    @Bean(name="musixmatch_api_key")
    public String getMusixMatchApiKey() { return mEnv.getProperty("musixmatch_api_key"); }

    @Bean(name = "lineMessagingClient")
    public LineMessagingClient getLineMessagingClient() {
        return LineMessagingClient.builder(getChannelAccessToken())
                .apiEndPoint(LineMessagingClientBuilder.DEFAULT_API_END_POINT)
                .connectTimeout(LineMessagingClientBuilder.DEFAULT_CONNECT_TIMEOUT)
                .readTimeout(LineMessagingClientBuilder.DEFAULT_READ_TIMEOUT)
                .writeTimeout(LineMessagingClientBuilder.DEFAULT_WRITE_TIMEOUT)
                .build();
    }

    @Bean(name = "lineSignatureValidator")
    public LineSignatureValidator getLineSignatureValidator() {
        return new LineSignatureValidator(getChannelSecret().getBytes());
    }

    @Bean(name = "retrofit")
    public Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.musixmatch.com/ws/1.1/")
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder().registerTypeAdapter(
                                        LyricsResponse.class, new LyricsResponse.OptionsDeserilizer()
                                ).create()
                        )
                )
                .build();
    }

    @Bean(name = "musixMatchInterface")
    public MusixMatchInterface getMusixMatchService() {
        return getRetrofit().create(MusixMatchInterface.class);
    }
}
