package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartWebhookData(

        HotmartBuyerData buyer,
        HotmartProductData product,
        HotmartPurchaseData purchase

) {
}