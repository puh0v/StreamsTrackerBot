package io.github.puh0v.youtube.dto.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.github.puh0v.youtube.dto.NameSpaces;

import java.util.List;

@JacksonXmlRootElement(localName = "feed", namespace = NameSpaces.ATOM)
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedDto(
        @JacksonXmlProperty(localName = "entry", namespace = NameSpaces.ATOM)
        @JacksonXmlElementWrapper(useWrapping = false)
        List<EntryDto> entry
) {}

