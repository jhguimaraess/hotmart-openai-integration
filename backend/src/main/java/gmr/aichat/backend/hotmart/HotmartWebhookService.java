package gmr.aichat.backend.hotmart;

import gmr.aichat.backend.hotmart.dto.HotmartWebhookRequest;
import gmr.aichat.backend.purchase.Purchase;
import gmr.aichat.backend.purchase.PurchaseService;
import gmr.aichat.backend.purchase.PurchaseStatus;
import gmr.aichat.backend.user.User;
import gmr.aichat.backend.user.UserService;
import gmr.aichat.backend.user.UserStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HotmartWebhookService {

    private static final Logger logger =
            LoggerFactory.getLogger(HotmartWebhookService.class);

    private final UserService userService;
    private final PurchaseService purchaseService;

    public HotmartWebhookService(
            UserService userService,
            PurchaseService purchaseService
    ){
        this.userService = userService;
        this.purchaseService = purchaseService;
    }

    @Transactional
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

        var buyer = request.data().buyer();
        var product = request.data().product();
        var purchaseData = request.data().purchase();

        String transactionId = purchaseData.transaction();

        if(purchaseService.existsByTransactionId(transactionId)){
            logger.info(
                    "Purchase already processed: {}",
                    transactionId
            );

            return;
        }

        User user = userService
                .findByEmail(buyer.email())
                        .orElseGet(() ->
                                userService.create(
                                        buyer.name(),
                                        buyer.email(),
                                        UserStatus.ACTIVE
                                ));

        if(user.getStatus() != UserStatus.ACTIVE){
            user = userService.updateStatus(
                    user.getId(),
                    UserStatus.ACTIVE
            );
        }

        Instant purchaseDate = resolvePurchaseDate(request);

        Purchase purchase = purchaseService.register(
                user.getId(),
                transactionId,
                String.valueOf(product.id()),
                PurchaseStatus.APPROVED,
                purchaseDate
        );

        logger.info(
                "Purchase {} registered for user {}",
                purchase.getHotmartTransactionId(),
                user.getEmail()
        );
    }

    private void processPurchaseRefunded(
            HotmartWebhookRequest request
    ) {

        String transactionId =
                request.data().purchase().transaction();

        var purchaseOptional =
                purchaseService.findOptionalByTransactionId(transactionId);

        if (purchaseOptional.isEmpty()) {
            logger.warn(
                    "Ignoring refund for unknown transaction: {}",
                    transactionId
            );

            return;
        }

        Purchase purchase =
                purchaseService.updateStatus(
                        transactionId,
                        PurchaseStatus.REFUNDED
                );

        updateUserAccess(purchase);

        logger.info(
                "Purchase {} refunded",
                transactionId
        );
    }

    private void processPurchaseChargeback(
            HotmartWebhookRequest request
    ) {

        String transactionId =
                request.data().purchase().transaction();

        var purchaseOptional =
                purchaseService.findOptionalByTransactionId(transactionId);

        if (purchaseOptional.isEmpty()) {
            logger.warn(
                    "Ignoring chargeback for unknown transaction: {}",
                    transactionId
            );

            return;
        }

        Purchase purchase =
                purchaseService.updateStatus(
                        transactionId,
                        PurchaseStatus.CHARGEBACK
                );

        updateUserAccess(purchase);

        logger.info(
                "Purchase {} marked as chargeback",
                transactionId
        );
    }

    private void updateUserAccess(Purchase purchase) {

        Long userId = purchase.getUser().getId();

        boolean hasApprovedPurchase =
                purchaseService.hasApprovedPurchase(userId);

        if (!hasApprovedPurchase) {
            userService.updateStatus(
                    userId,
                    UserStatus.INACTIVE
            );
        }
    }

    private Instant resolvePurchaseDate(
            HotmartWebhookRequest request
    ) {

        Long approvedDate =
                request.data().purchase().approvedDate();

        if (approvedDate != null) {
            return Instant.ofEpochMilli(approvedDate);
        }

        if (request.creationDate() != null) {
            return Instant.ofEpochMilli(
                    request.creationDate()
            );
        }

        return Instant.now();
    }
}
