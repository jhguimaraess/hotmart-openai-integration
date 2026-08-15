package gmr.aichat.backend.purchase;

import gmr.aichat.backend.user.User;
import gmr.aichat.backend.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserService userService;

    public PurchaseService(PurchaseRepository purchaseRepository, UserService userService){
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
    }

    @Transactional
    public Purchase register(Long userId,
                             String hotmartTransactionId,
                             String productId,
                             PurchaseStatus status,
                             Instant purchaseDate){

        if(purchaseRepository.existsByHotmartTransactionId(hotmartTransactionId)) {
            throw new IllegalArgumentException("Purchase already exists with transaction id: " + hotmartTransactionId);
        }

        User user = userService.findById(userId);

        Purchase purchase = new Purchase(
                user,
                hotmartTransactionId,
                productId,
                status,
                purchaseDate
        );

        return purchaseRepository.save(purchase);
    }

    public Purchase findByTransactionId(String hotmartTransactionId){
        return purchaseRepository
                .findByHotmartTransactionId(hotmartTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found with transaction id: " + hotmartTransactionId));
    }

    public List<Purchase> findByUserId(Long userId){
        return purchaseRepository.findAllByUserId(userId);
    }

    @Transactional
    public Purchase updateStatus(String hotmartTransactionId,
                                 PurchaseStatus status){
        Purchase purchase = findByTransactionId(hotmartTransactionId);

        purchase.setStatus(status);

        return purchase;
    }

    public boolean existsByTransactionId(String hotmartTransactionId){
        return purchaseRepository
                .existsByHotmartTransactionId(hotmartTransactionId);
    }
}
