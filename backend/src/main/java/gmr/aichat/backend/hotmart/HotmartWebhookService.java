package gmr.aichat.backend.hotmart;

import gmr.aichat.backend.hotmart.dto.HotmartWebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HotmartWebhookService {

    private static final Logger logger =
            LoggerFactory.getLogger(HotmartWebhookService.class);

    public void process(HotmartWebhookRequest request){
        switch(request.event()){
            case "PURCHASE_APPROVED" ->
                processPurchaseApproved(request);
            case "PURCHASE_REFUNDED" ->
                processPurchaseRefunded(request);
            case "PURCHASE_CHARGEBACK" ->
                    processPurchaseChargeback(request);
            default ->
                    logger.info(
                            "Ignoring unsupported Hotmart event: {}",
                            request.event()
                    );
        }
    }

    private void processPurchaseApproved(HotmartWebhookRequest request){
        logger.info(
                "Processing approved purchase: {}",
                request.data().purchase().transaction()
        );
    }

    private void processPurchaseRefunded(
            HotmartWebhookRequest request
    ) {
        logger.info(
                "Processing refunded purchase: {}",
                request.data().purchase().transaction()
        );
    }

    private void processPurchaseChargeback(
            HotmartWebhookRequest request
    ) {
        logger.info(
                "Processing chargeback purchase: {}",
                request.data().purchase().transaction()
        );
    }
}
