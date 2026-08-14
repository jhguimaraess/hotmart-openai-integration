package gmr.aichat.backend.user;

import gmr.aichat.backend.purchase.Purchase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gmr.users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases = new ArrayList<>();

    protected User() {
    }

    public User(String name, String email, UserStatus status) {
        this.name = name;
        this.email = email;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

}
