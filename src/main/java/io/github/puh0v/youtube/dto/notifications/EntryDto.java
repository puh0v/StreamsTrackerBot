package io.github.puh0v.youtube.dto.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.github.puh0v.youtube.dto.NameSpaces;


@JacksonXmlRootElement(localName = "entry", namespace = NameSpaces.ATOM)
@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryDto(
        @JacksonXmlProperty(localName = "videoId", namespace = NameSpaces.YT)
        String videoId
){}
