package com.ryccoatika.Music.Lyrics.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackResponse {

    @SerializedName("message")
    MessageResponse messageResponse;

    public MessageResponse getMessageResponse() {
        return messageResponse;
    }

    public TrackResponse(MessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }

    // message response
    public class MessageResponse {
        @SerializedName("header")
        HeaderResponse headerResponse;

        @SerializedName("body")
        @Expose
        BodyResponse bodyResponse;

        public HeaderResponse getHeaderResponse() {
            return headerResponse;
        }

        public BodyResponse getBodyResponse() {
            return bodyResponse;
        }

        public MessageResponse(HeaderResponse headerResponse, BodyResponse bodyResponse) {
            this.headerResponse = headerResponse;
            this.bodyResponse = bodyResponse;
        }

        // body response
        public class BodyResponse {
            @SerializedName("track_list")
            List<TrackObjectResponse> trackObjectResponses;

            public List<TrackObjectResponse> getTrackObjectResponses() { return trackObjectResponses; }

            public BodyResponse(List<TrackObjectResponse> trackObjectResponse) {
                this.trackObjectResponses = trackObjectResponse;
            }

            // track list response
            public class TrackObjectResponse {
                @SerializedName("track")
                TrackDetailResponse trackDetailResponse;

                public TrackDetailResponse getTrackDetailResponse() { return trackDetailResponse; }

                public TrackObjectResponse(TrackDetailResponse trackDetailResponse) { this.trackDetailResponse = trackDetailResponse; }

                // track detail response
                public class TrackDetailResponse {
                    @SerializedName("track_id")
                    int trackId;
                    @SerializedName("track_name")
                    String trackName;
                    @SerializedName("artist_name")
                    String artistName;

                    public int getTrackId() {
                        return trackId;
                    }

                    public String getTrackName() {
                        if (trackName.length() > 60) {
                            return trackName.substring(0, 57) + "...";
                        } else {
                            return trackName;
                        }
                    }

                    public String getArtistName() {
                        if (artistName.length() > 40) {
                            return artistName.substring(0, 37) + "...";
                        } else {
                            return artistName;
                        }
                    }

                    public TrackDetailResponse(int trackId, String trackName, String artistName) {
                        this.trackId = trackId;
                        this.trackName = trackName;
                        this.artistName = artistName;
                    }
                }

            }
        }

        // header response
        public class HeaderResponse {
            @SerializedName("status_code")
            int statusCode;

            public int getStatusCode() {
                return statusCode;
            }

            public HeaderResponse(int statusCode) {
                this.statusCode = statusCode;
            }
        }
    }
}
