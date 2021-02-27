package com.ryccoatika.Music.Lyrics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.ryccoatika.Music.Lyrics.model.LineEventsModel;
import com.ryccoatika.Music.Lyrics.model.LyricsResponse;
import com.ryccoatika.Music.Lyrics.model.LyricsResponse.MessageResponse.BodyResponse.LyricsDetailResponse;
import com.ryccoatika.Music.Lyrics.model.TrackResponse.MessageResponse.BodyResponse.TrackObjectResponse;
import com.ryccoatika.Music.Lyrics.service.BotService;
import com.ryccoatika.Music.Lyrics.service.BotTemplate;
import com.ryccoatika.Music.Lyrics.service.MusixMatchService;
import com.ryccoatika.Music.Lyrics.utils.CountryCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class LineBotController {

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private BotService botService;

    @Autowired
    private BotTemplate botTemplate;

    @Autowired
    private MusixMatchService musixMatchService;

    private CountryCodes countryCodes = new CountryCodes();

    private UserProfileResponse sender = null;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> callback(
            @RequestHeader("X-Line-Signature") String xLineSignature,
            @RequestBody String eventsPayload
    ) {
        try {
            if (!lineSignatureValidator.validateSignature(eventsPayload.getBytes(), xLineSignature)) {
                throw new RuntimeException("Invalid Signature Validation");
            }

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            LineEventsModel eventsModel = objectMapper.readValue(eventsPayload, LineEventsModel.class);

            eventsModel.getEvents().forEach((event) -> {
                if (event instanceof JoinEvent || event instanceof FollowEvent) {
                    String replyToken = ((ReplyEvent) event).getReplyToken();
                    handleJoinOrFollowEvents(replyToken, event.getSource());
                } else {
                    handleMessageEvent((MessageEvent) event);
                }
            } );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void greetingMessage(String replyToken, Source source) {
        if (sender == null) {
            sender = botService.getProfile(source.getSenderId());
        }

        TemplateMessage greetingMessage = botTemplate.greetingMessage(source, sender);

        botService.reply(replyToken, greetingMessage);
    }

    private void handleJoinOrFollowEvents(String replyToken, Source source) {
        greetingMessage(replyToken, source);
    }

    private void helpMessage(String replyToken, Source source, String additionalMessage) {
        if (sender == null) {
            sender = botService.getProfile(source.getSenderId());
        }

        String helpMessage = botTemplate.helpMessage();

        if (additionalMessage != null) {
            String[] messages = {
                    additionalMessage,
                    helpMessage
            };
            botService.replyText(replyToken, messages);
        } else {
            botService.replyText(replyToken, helpMessage);
        }
    }

    private void handleMessageEvent(MessageEvent messageEvent) {
        String replyToken = messageEvent.getReplyToken();
        MessageContent content = messageEvent.getMessage();
        Source source = messageEvent.getSource();
        String senderId = source.getSenderId();
        sender = botService.getProfile(senderId);

        if (content instanceof TextMessageContent) {
            if (source instanceof GroupSource || source instanceof RoomSource || source instanceof UserSource) {
                handleTextMessage(replyToken, (TextMessageContent) content, source);
            } else {
                botService.replyText(replyToken, "Unknown Message Source!");
            }
        } else {
            greetingMessage(replyToken, source);
        }
    }

    private void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
        String msgText = content.getText().toLowerCase();
        if (msgText.contains("lihat caranya")) {
            helpMessage(replyToken, source, null);
        } else if (msgText.contains("lihat lirik")) {
            handleLihatLirik(replyToken, msgText);
        }else if (msgText.contains("gideon")) {
            processText(replyToken, source, msgText);
        } else {
            handleFallbackMessage(replyToken, source);
        }
    }

    private void processText(String replyToken, Source source, String messageText) {
        if (messageText.contains("lagu populer di")) {
            int idx = messageText.indexOf("populer di") + "populer di".length();
            String msgText = messageText.substring(idx);
            handleLaguPopuler(replyToken, msgText);
        } else if (messageText.contains("cari lagu")) {
            int idx = messageText.indexOf("lagu") + "lagu".length();
            String msgText = messageText.substring(idx);
            handleCariLagu(replyToken, msgText);
        } else {
            handleFallbackMessage(replyToken, source);
        }
    }

    private void handleLihatLirik(String replyToken, String messageText) {
        int idx = messageText.indexOf("#") + "#".length();
        String msgText = messageText.substring(idx).trim();
        if (msgText.isEmpty()) {
            botService.replyText(replyToken, "Ada yang salah nih kak, tolong dicek lagi yaa!\n"+msgText);
        } else {
            LyricsDetailResponse lyricsDetailResponse = musixMatchService.getLyrics(msgText);
            if (lyricsDetailResponse == null) {
                botService.replyText(replyToken, "Yaah, gak ketemu nih kak. Maaf yaa :(");
            } else {
                botService.replyText(replyToken, lyricsDetailResponse.getLyricsBody());
            }
        }
    }

    private void handleLaguPopuler(String replyToken, String messageText) {
        String msgText = messageText.trim();
        String countryCode = countryCodes.getCode(msgText);
        if (countryCode == null) {
            botService.replyText(replyToken, "Negaranya gak ketemu nih kak, tolong dicek lagi yaa!\n"+msgText);
        } else {
            List<TrackObjectResponse> trackObjectResponses = musixMatchService.getPopularSong(countryCode);
            if (trackObjectResponses == null || trackObjectResponses.isEmpty()) {
                botService.replyText(replyToken, "Yaah, gak ketemu nih kak. Maaf yaa :(");
            } else {
                TemplateMessage carouselTrack = botTemplate.carouselTrack(trackObjectResponses);
                botService.reply(replyToken, carouselTrack);
            }
        }
    }

    private void handleCariLagu(String replyToken, String messageText) {
        String msgText = messageText.trim();
        List<TrackObjectResponse> trackObjectResponses;
        if (msgText.contains("judulnya") && msgText.contains("dari artis")) {
            // Gideon, cari lagu yang judulnya My Love dari artis Westlife
            int trackIdx = msgText.indexOf("judulnya") + "judulnya".length();
            int artistIdx = msgText.indexOf("dari artis");
            String qTrack = msgText.substring(trackIdx, artistIdx).trim();
            artistIdx += "dari artis".length();
            String qArtist = msgText.substring(artistIdx).trim();
            System.out.println("qTrack: " + qTrack);
            System.out.println("qArtist: " + qArtist);
            trackObjectResponses = musixMatchService.searchSong(qTrack, qArtist, null);
        } else if (msgText.contains("judulnya")) {
            int idx = msgText.indexOf("judulnya") + "judulnya".length();
            msgText = msgText.substring(idx).trim();
            trackObjectResponses = musixMatchService.searchSong(msgText, null, null);
        } else if (msgText.contains("artis")) {
            int idx = msgText.indexOf("artis") + "artis".length();
            msgText = msgText.substring(idx).trim();
            trackObjectResponses = musixMatchService.searchSong(null, msgText, null);
        } else if (msgText.contains("liriknya")) {
            int idx = msgText.indexOf("liriknya") + "liriknya".length();
            msgText = msgText.substring(idx).trim();
            trackObjectResponses = musixMatchService.searchSong(null, null, msgText);
        } else {
            botService.replyText(replyToken, "Ada yang salah nih kak, cek lagi yaa!");
            return;
        }

        if (trackObjectResponses == null || trackObjectResponses.isEmpty()) {
            botService.replyText(replyToken, "Yaah, gak ketemu nih kak. Maaf yaa :(");
        } else {
            TemplateMessage carouselTrack = botTemplate.carouselTrack(trackObjectResponses);
            botService.reply(replyToken, carouselTrack);
        }
    }

    private void handleFallbackMessage(String replyToken, Source source) {
        helpMessage(replyToken, source, "Hi Kak " + sender.getDisplayName() + ", Aku bingung nih sama yang kakak maksud.");
    }
}
