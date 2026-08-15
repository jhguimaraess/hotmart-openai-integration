package gmr.aichat.backend.hotmart;

import gmr.aichat.backend.purchase.Purchase;
import gmr.aichat.backend.purchase.PurchaseRepository;
import gmr.aichat.backend.purchase.PurchaseStatus;
import gmr.aichat.backend.user.User;
import gmr.aichat.backend.user.UserRepository;
import gmr.aichat.backend.user.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(
        properties = "hotmart.webhook.hottok=test-hottok"
)
@AutoConfigureMockMvc
class HotmartWebhookIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis =
            new GenericContainer<>(
                    DockerImageName.parse("redis:8-alpine")
            )
                    .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @AfterEach
    void cleanDatabase() {
        purchaseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserAndPurchaseWhenPurchaseIsApproved()
            throws Exception {

        String payload = """
                {
                  "id": "event-approved-test",
                  "creation_date": 1786740000000,
                  "event": "PURCHASE_APPROVED",
                  "version": "2.0.0",
                  "data": {
                    "buyer": {
                      "name": "Joao Guimaraes",
                      "email": "joao@test.com"
                    },
                    "product": {
                      "id": 123456,
                      "name": "AI Chat"
                    },
                    "purchase": {
                      "transaction": "HP-TEST-001",
                      "status": "APPROVED",
                      "approved_date": 1786740000000
                    }
                  }
                }
                """;

        mockMvc.perform(
                        post("/webhook/hotmart")
                                .header(
                                        "X-HOTMART-HOTTOK",
                                        "test-hottok"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isOk());

        User user = userRepository
                .findByEmail("joao@test.com")
                .orElseThrow();

        assertThat(user.getName())
                .isEqualTo("Joao Guimaraes");

        assertThat(user.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        Purchase purchase = purchaseRepository
                .findByHotmartTransactionId("HP-TEST-001")
                .orElseThrow();

        assertThat(purchase.getStatus())
                .isEqualTo(PurchaseStatus.APPROVED);

        assertThat(purchase.getUser().getId())
                .isEqualTo(user.getId());

        assertThat(purchase.getProductId())
                .isEqualTo("123456");
    }
}