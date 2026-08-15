package gmr.aichat.backend.hotmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotmartBuyerData(

        String name,
        String email

) {
}