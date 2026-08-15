package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartWebhookRequest(

        String id,

        @JsonProperty("creation_date")
        Long creationDate,

        String event,

        String version,

        HotmartWebhookData data

) {
}