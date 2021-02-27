package com.ryccoatika.Music.Lyrics.db;

import com.ryccoatika.Music.Lyrics.model.LyricsResponse;
import com.ryccoatika.Music.Lyrics.model.TrackResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusixMatchInterface {
    @GET("chart.tracks.get")
    Call<TrackResponse> getPopularSong(
            @Query("country") String country,
            @Query("chart_name") String chartName,
            @Query("f_has_lyrics") int fHasLyrics,
            @Query("apikey") String apiKey
    );

    @GET("track.lyrics.get")
    Call<LyricsResponse> getLyrics(
            @Query("track_id") String trackId,
            @Query("apikey") String apikey
    );

    @GET("track.search")
    Call<TrackResponse> searchSong(
            @Query("q_track") String qTrack,
            @Query("q_artist") String qArtist,
            @Query("q_lyrics") String qLyrics,
            @Query("f_has_lyrics") int fHasLyrics,
            @Query("apikey") String apikey
    );
}
