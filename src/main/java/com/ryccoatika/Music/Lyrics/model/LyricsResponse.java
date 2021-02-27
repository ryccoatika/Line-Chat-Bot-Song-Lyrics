package com.ryccoatika.Music.Lyrics.model;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class LyricsResponse {
    @SerializedName("message")
    MessageResponse messageResponse;

    public MessageResponse getMessageResponse() {
        return messageResponse;
    }

    public LyricsResponse(MessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }

    // message response
    public class MessageResponse {
        @SerializedName("header")
        HeaderResponse headerResponse;

        public HeaderResponse getHeaderResponse() {
            return headerResponse;
        }

        // do deserilizer manual
        BodyResponse bodyResponse;

        public BodyResponse getBodyResponse() {
            return bodyResponse;
        }

        public void setBodyResponse(BodyResponse bodyResponse) {
            this.bodyResponse = bodyResponse;
        }

        public MessageResponse(HeaderResponse headerResponse, BodyResponse bodyResponse) {
            this.headerResponse = headerResponse;
            this.bodyResponse = bodyResponse;
        }

        // body response
        public class BodyResponse {
            @SerializedName("lyrics")
            LyricsDetailResponse lyricsDetailResponse;

            public LyricsDetailResponse getLyricsDetailResponse() {
                return lyricsDetailResponse;
            }

            public BodyResponse(LyricsDetailResponse lyricsDetailResponse) {
                this.lyricsDetailResponse = lyricsDetailResponse;
            }

            // track detail response
            public class LyricsDetailResponse {
                @SerializedName("lyrics_body")
                String lyricsBody;

                public String getLyricsBody() {
                    return lyricsBody;
                }

                public LyricsDetailResponse(String lyricsBody) {
                    this.lyricsBody = lyricsBody;
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


    public static class OptionsDeserilizer implements JsonDeserializer<LyricsResponse> {

        @Override
        public LyricsResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            LyricsResponse lyricsResponse = new Gson().fromJson(json, LyricsResponse.class);
            JsonObject jsonObject = json.getAsJsonObject();
            jsonObject = jsonObject.getAsJsonObject("message");
            if (jsonObject.has("body")) {
                JsonElement jsonElement = jsonObject.get("body");
                if (jsonElement.isJsonObject()) {
                    MessageResponse.BodyResponse bodyResponse = new Gson().fromJson(jsonElement, MessageResponse.BodyResponse.class);
                    lyricsResponse.getMessageResponse().setBodyResponse(bodyResponse);
                }
            }
            return lyricsResponse;
        }
    }
}
