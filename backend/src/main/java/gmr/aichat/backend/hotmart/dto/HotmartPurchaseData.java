package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartPurchaseData(

        @NotBlank
        String transaction,

        String status,

        @JsonProperty("approved_date")
        Long approvedDate

) {
}