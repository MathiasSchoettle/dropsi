package de.mschoettle.utils.configuration;

import de.mschoettle.control.service.impl.PaymentService;
import de.othr.sw.hamilton.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class PaymentConfiguration {

    private PaymentService paymentService;

    @Autowired
    public void setInjectedBean(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Create a Payment for every time a session is created. This payment contains a URL to which the user will be redirected
     * if he clicks on the coffee button.
     * <p>
     * If the Hamilton Bank service is not available an empty payment is returned.
     *
     * @return the payment for the coffee
     */
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Payment getPayment() {
        return paymentService.getNewPaymentForSession();
    }
}