package com.example.testtask_backend.controller.load;

import com.example.testtask_backend.model.Wallet;
import com.example.testtask_backend.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConcurrentLoadHttpTest {
    @Autowired
    private WalletRepository walletRepository;
    private final int THREADS = 1000;
    private final UUID walletId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final RestTemplate restTemplate = new RestTemplate();

    @BeforeAll
    public void initWallet() {
        walletRepository.save(Wallet.builder()
                .id(walletId)
                .balance(0L)
                .build());
    }

    @Test
    public void concurrentDepositRequestsShouldNotFail() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    String body = """
                            {
                              "valletId": "%s",
                              "operationType": "DEPOSIT",
                              "amount": 1
                            }
                            """.formatted(walletId.toString());

                    HttpEntity<String> request = new HttpEntity<>(body, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/api/v1/wallet", request, String.class);

                    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(), "Expected 2xx but got " + response.getStatusCode());

                } catch (Exception e) {
                    Assertions.fail("Request failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Проверка финального баланса
        Long balance = restTemplate.getForObject("http://localhost:8080/api/v1/wallets/" + walletId, Long.class);
        Assertions.assertEquals(THREADS, balance);
    }
}
