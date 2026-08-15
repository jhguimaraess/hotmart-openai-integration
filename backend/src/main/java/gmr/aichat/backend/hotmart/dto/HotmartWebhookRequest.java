package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartWebhookRequest(

        String id,

        @JsonProperty("creation_date")
        Long creationDate,

        @NotBlank
        String event,

        String version,

        @NotNull
        @Valid
        HotmartWebhookData data

) {
}