package com.ryccoatika.Music.Lyrics.service;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.ryccoatika.Music.Lyrics.model.TrackResponse.MessageResponse.BodyResponse.TrackObjectResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BotTemplate {
    public TemplateMessage createButton(String message, String actionTitle, String actionText) {
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                null,
                null,
                message,
                Collections.singletonList(new MessageAction(actionTitle, actionText))
        );
        return new TemplateMessage(actionTitle, buttonsTemplate);
    }

    public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
        String message = "Hi %s! Namaku Gideon, aku ditugasin sama penciptaku buat bantu Kakak cari lirik musik niih.";
        String action = "Lihat caranya";

        if (source instanceof GroupSource) {
            message = String.format(message, "Group");
        } else if (source instanceof RoomSource) {
            message = String.format(message, "Room");
        } else if (source instanceof UserSource) {
            message = String.format(message, sender.getDisplayName());
        } else {
            message = "Unknown Message Source!";
        }

        return createButton(message, action, action);
    }

    public TemplateMessage carouselTrack(List<TrackObjectResponse> trackDetailResponses) {
        List<CarouselColumn> carouselColumn = new ArrayList<>();
        trackDetailResponses.forEach((trackDetail) -> {
            carouselColumn.add(new CarouselColumn(null, trackDetail.getTrackDetailResponse().getArtistName(), trackDetail.getTrackDetailResponse().getTrackName(),
                    Arrays.asList(
                            new MessageAction("Lihat lirik", "Lihat lirik #"+trackDetail.getTrackDetailResponse().getTrackId())
                    )
            ));
        });
        CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumn);
        return new TemplateMessage("Ini kak hasilnya", carouselTemplate);
    }

    public String helpMessage() {
        String message = "Aku jelasin yaa apa aja yang bisa aku lakuin.\n\n" +
                "Kakak bisa minta daftar lagu populer di negara tertentu dengan chat aku kaya gini.\n" +
                "Gideon, lagu populer di Indonesia\n\n" +
                "Kakak juga bisa cari lagu loh, caranya bilang ke aku gini.\n" +
                "Gideon, cari lagu yang judulnya My Love\n" +
                "Gideon, cari lagu dari artis Westlife\n" +
                "Gideon, cari lagu yang judulnya My Love dari artis Westlife\n" +
                "Gideon, cari lagu yang liriknya I wonder where they are\n\n" +
                "Gampang kan? Yuk langsung chat aku.";

        return message;
    }
}
