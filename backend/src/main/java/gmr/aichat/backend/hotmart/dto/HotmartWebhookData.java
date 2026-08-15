package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartWebhookData(

        @NotNull
        @Valid
        HotmartBuyerData buyer,

        @NotNull
        @Valid
        HotmartProductData product,

        @NotNull
        @Valid
        HotmartPurchaseData purchase

) {
}