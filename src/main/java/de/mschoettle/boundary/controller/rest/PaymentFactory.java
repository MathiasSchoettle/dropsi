package de.mschoettle.boundary.controller.rest;

import de.othr.sw.hamilton.entity.Payment;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Random;

@Configuration
public class PaymentFactory {

    private RestTemplate restTemplate;

    @Value("${appconfig.hamilton.username}")
    private String username;

    @Value("${appconfig.hamilton.apiKey}")
    private String apiKey;

    @Value("${appconfig.hamilton.url}")
    private String url;

    private Logger logger;

    private static final float COST_OF_COFFEE = 1.5f;

    @Autowired
    public void setInjectedBean(RestTemplate restTemplate, Logger logger) {
        this.restTemplate = restTemplate;
        this.logger = logger;
    }

    /**
     * Create a Payment for every time a session is created. This payment contains a URL to which the user will be rediredcted
     * if he clicks on the coffee button.
     *
     * If the Hamilton Bank service is not available an empty payment is returned.
     *
     * @return the payment for the coffee
     */
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Payment getPayment() {

        Payment payment = new Payment(username, BigDecimal.valueOf(COST_OF_COFFEE), "Pay a coffee for the free dropsi service");

        RequestEntity<Payment> requestEntity = RequestEntity.post(url + "/api/payment/create")
                .header("api-key", apiKey)
                .body(payment);

        try {
            ResponseEntity<Payment> responseEntity = restTemplate.exchange(requestEntity, Payment.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            logger.error("Connection timeout to Hamilton Bank");
            return payment;
        }
    }
}