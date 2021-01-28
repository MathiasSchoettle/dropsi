package de.mschoettle.control.service.impl;

import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.PaymentRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * PaymentService belongs in boundary as it communicates with the interface of the hamilton bank
 */
@Service
@Scope("singleton")
public class PaymentService {

    private RestTemplate restTemplate;

    @Value("${appconfig.hamilton.username}")
    private String username;

    @Value("${appconfig.hamilton.description}")
    private String description;

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

    public Payment getNewPaymentForSession() {

        PaymentRequest requestPayment = new PaymentRequest(username, BigDecimal.valueOf(COST_OF_COFFEE), description);

        RequestEntity<PaymentRequest> requestEntity = RequestEntity.post(url + "/api/payment/create")
                .header("api-key", apiKey)
                .body(requestPayment);

        try {
            ResponseEntity<Payment> responseEntity = restTemplate.exchange(requestEntity, Payment.class);

            // in some weird edge case while testing both the bank and dropsi a 302 status code was returned
            // this deals with that edge case
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                logger.info("requested money over hamilton bank");
                return responseEntity.getBody();
            } else {
                return new Payment(requestPayment);
            }

        } catch (HttpClientErrorException httpe) {
            logger.error("Error with Hamilton REST Api Bank");
            return new Payment(requestPayment);
        } catch (Exception c) {
            logger.error("Exception in Hamilton Bank");
            return new Payment(requestPayment);
        }
    }
}
