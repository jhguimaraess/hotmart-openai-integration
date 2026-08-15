package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartPurchaseData(

        String transaction,
        String status,

        @JsonProperty("approved_date")
        Long approvedDate

) {
}