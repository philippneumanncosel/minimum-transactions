package de.klosebrothers.minimumtransactions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentsTest {

    private Payments payments;

    @BeforeEach
    void setUp() {
        payments = new Payments();
    }

    @Test
    void itShouldReturnZeroForUnknownPersons() {
        double payment = payments.getTotalPaymentFromTo("unknown", "alsoUnkown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownGiver() {
        payments.registerPayment("sampleName", "known", 10.0);

        double payment = payments.getTotalPaymentFromTo("unknown", "known");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownRecipient() {
        payments.registerPayment("known", "sampleName", 10.0);

        double payment = payments.getTotalPaymentFromTo("known", "unknown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForKnownPersonsWithNoPaymentHistory() {
        payments.registerPayment("A", "B", 10.0);
        payments.registerPayment("B", "C", 10.0);

        double payment = payments.getTotalPaymentFromTo("A", "C");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnPayment() {
        payments.registerPayment("Alex", "Bob", 10.0);

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(10.0);
    }

    @Test
    void itShouldReturnSumOfMultiplePayments() {
        payments.registerPayment("Alex", "Bob", 10.0);
        payments.registerPayment("Alex", "Bob", 3.0);
        payments.registerPayment("Alex", "Bob", 2.0);

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(15.0);
    }

    @Test
    void itShouldReturnPaymentsForMultiplePersons() {
        registerExamplePayments();

        double paymentAlexBob = payments.getTotalPaymentFromTo("Alex", "Bob");
        double paymentAlexClara = payments.getTotalPaymentFromTo("Bob", "Clara");
        double paymentBobDennis = payments.getTotalPaymentFromTo("Bob", "Dennis");

        assertThat(paymentAlexBob).isEqualTo(10.0);
        assertThat(paymentAlexClara).isEqualTo(3.0);
        assertThat(paymentBobDennis).isEqualTo(2.0);
    }

    @Test
    void itShouldReturnZeroInfluxForUnkwownPerson() {
        double influx = payments.getInfluxForPerson("unknown");

        assertThat(influx).isZero();
    }

    @Test
    void itShouldReturnCorrectInfluxForPerson() {
        registerExamplePayments();

        double influx = payments.getInfluxForPerson("Bob");

        assertThat(influx).isEqualTo(5.0);
    }

    @Test
    void itShouldReturnCorrectInfluxesForAllPersons() {
        registerExamplePayments();

        Map<String, Double> allInfluxes = payments.getAllInfluxes();

        assertThat(allInfluxes)
                .containsEntry("Alex", -10.0)
                .containsEntry("Bob", 5.0)
                .containsEntry("Clara", 3.0)
                .containsEntry("Dennis", 2.0);
    }

    @Test
    void itShouldListAllResolvingPayments() {
        registerExamplePayments();
        payments.registerPayment("Alex", "Clara", 6.0);

        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                Bob owes Alex 10.0
                Clara owes Alex 6.0
                Clara owes Bob 3.0
                Dennis owes Bob 2.0""");
    }

    @Test
    void itShouldEliminateMinimalCyclicPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", "Bob", 6.0);
        payments.registerPayment("Bob", "Alex", 5.0);

        payments.eliminateAllCyclicPayments();
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("Bob owes Alex 1.0");
        assertThat(allInfluxes)
                .containsEntry("Alex", -1.0)
                .containsEntry("Bob", 1.0);
    }

    @Test
    void itShouldEliminateMultipleCyclicPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", "Bob", 16.0);
        payments.registerPayment("Bob", "Claire", 10.0);
        payments.registerPayment("Claire", "Alex", 10.0);
        payments.registerPayment("Bob", "Alex", 5.0);

        payments.eliminateAllCyclicPayments();
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("Bob owes Alex 1.0");
        assertThat(allInfluxes)
                .containsEntry("Alex", -1.0)
                .containsEntry("Bob", 1.0)
                .containsEntry("Claire", 0.0);
    }

    private void registerExamplePayments() {
        payments.registerPayment("Alex", "Bob", 10.0);
        payments.registerPayment("Bob", "Clara", 3.0);
        payments.registerPayment("Bob", "Dennis", 2.0);
    }
}