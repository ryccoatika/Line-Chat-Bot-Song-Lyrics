package com.ryccoatika.Music.Lyrics.service;

import com.ryccoatika.Music.Lyrics.db.MusixMatchInterface;
import com.ryccoatika.Music.Lyrics.model.LyricsResponse;
import com.ryccoatika.Music.Lyrics.model.LyricsResponse.MessageResponse.BodyResponse.LyricsDetailResponse;
import com.ryccoatika.Music.Lyrics.model.TrackResponse;
import com.ryccoatika.Music.Lyrics.model.TrackResponse.MessageResponse.BodyResponse.TrackObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Service
public class MusixMatchService {

    @Autowired
    private MusixMatchInterface musixMatchInterface;

    @Autowired
    @Qualifier("musixmatch_api_key")
    private String musixMatchApiKey;

    public List<TrackObjectResponse> getPopularSong(String country) {
        Call<TrackResponse> call = musixMatchInterface.getPopularSong(country, "hot", 1, musixMatchApiKey);

        try {
            Response<TrackResponse> response = call.execute();
            if (!response.isSuccessful()) {
                throw new IOException(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
            }
            if (response.body().getMessageResponse().getHeaderResponse().getStatusCode() != 200) {
                return null;
            } else {
                return response.body().getMessageResponse().getBodyResponse().getTrackObjectResponses();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TrackObjectResponse> searchSong(String qTrack, String qArtist, String qLyrics) {
        Call<TrackResponse> call = musixMatchInterface.searchSong(qTrack, qArtist, qLyrics, 1, musixMatchApiKey);

        try {
            Response<TrackResponse> response = call.execute();
            if (!response.isSuccessful()) {
                throw new IOException(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
            }
            if (response.body().getMessageResponse().getHeaderResponse().getStatusCode() != 200) {
                return null;
            } else {
                return response.body().getMessageResponse().getBodyResponse().getTrackObjectResponses();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LyricsDetailResponse getLyrics(String trackId) {
        Call<LyricsResponse> call = musixMatchInterface.getLyrics(trackId, musixMatchApiKey);

        try {
            Response<LyricsResponse> response = call.execute();
            if (!response.isSuccessful()) {
                throw new IOException(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
            }
            if (response.body().getMessageResponse().getHeaderResponse().getStatusCode() != 200) {
                return null;
            } else {
                return response.body().getMessageResponse().getBodyResponse().getLyricsDetailResponse();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
