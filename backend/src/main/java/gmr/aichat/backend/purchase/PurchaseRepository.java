package gmr.aichat.backend.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByHotmartTransactionId(String hotmartTransactionId);

    boolean existsByHotmartTransactionId(String hotmartTransactionId);

    List<Purchase> findAllByUserId(Long userId);
}
