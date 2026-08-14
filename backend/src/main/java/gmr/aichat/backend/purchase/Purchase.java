package gmr.aichat.backend.purchase;

import gmr.aichat.backend.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "gmr.purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String hotmartTransactionId;

    @Column(nullable = false)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status;

    @Column(nullable = false)
    private Instant purchaseDate;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Purchase() {
    }

    public Purchase(
            User user,
            String hotmartTransactionId,
            String productId,
            PurchaseStatus status,
            Instant purchaseDate
    ) {
        this.user = user;
        this.hotmartTransactionId = hotmartTransactionId;
        this.productId = productId;
        this.status = status;
        this.purchaseDate = purchaseDate;
    }

    @PrePersist
    private void prePersist() {
        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getHotmartTransactionId() {
        return hotmartTransactionId;
    }

    public String getProductId() {
        return productId;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }
}
