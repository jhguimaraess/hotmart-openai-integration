package gmr.aichat.backend.hotmart;

import gmr.aichat.backend.hotmart.dto.HotmartWebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/hotmart")
public class HotmartWebhookController {

    private final HotmartWebhookService hotmartWebhookService;
    private final String expectedHottok;

    public HotmartWebhookController(
            HotmartWebhookService hotmartWebhookService,
            @Value("${hotmart.webhook.hottok}") String expectedHottok
    ) {
        this.hotmartWebhookService = hotmartWebhookService;
        this.expectedHottok = expectedHottok;
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(
                    value = "X-HOTMART-HOTTOK",
                    required = false
            ) String hottok,
            @RequestBody HotmartWebhookRequest request){

        if(!expectedHottok.equals(hottok)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        hotmartWebhookService.process(request);

        return ResponseEntity.ok().build();

    }

}
