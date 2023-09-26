package de.klosebrothers.minimumtransactions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentsTest {


    @Test
    void itShouldReturnZeroForUnknownPersons() {
        Payments payments = new Payments();

        double payment = payments.getTotalPaymentFromTo("unknown", "alsoUnkown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownGiver() {
        Payments payments = new Payments();

        payments.registerPayment("sampleName", "known", 10.0);

        double payment = payments.getTotalPaymentFromTo("unknown", "known");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownRecipient() {
        Payments payments = new Payments();

        payments.registerPayment("known", "sampleName", 10.0);

        double payment = payments.getTotalPaymentFromTo("known", "unknown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForKnownPersonsWithNoPaymentHistory() {
        Payments payments = new Payments();

        payments.registerPayment("A", "B", 10.0);
        payments.registerPayment("B", "C", 10.0);

        double payment = payments.getTotalPaymentFromTo("A", "C");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnPayment() {
        Payments payments = new Payments();

        payments.registerPayment("Alex", "Bob", 10.0);

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(10.0);
    }

    @Test
    void itShouldReturnSumOfMultiplePayments() {
        Payments payments = new Payments();

        payments.registerPayment("Alex", "Bob", 10.0);
        payments.registerPayment("Alex", "Bob", 3.0);
        payments.registerPayment("Alex", "Bob", 2.0);

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(15.0);
    }

    @Test
    void itShouldReturnPaymentsForMultiplePersons() {
        Payments payments = new Payments();

        payments.registerPayment("Alex", "Bob", 10.0);
        payments.registerPayment("Alex", "Clara", 3.0);
        payments.registerPayment("Bob", "Dennis", 2.0);

        double paymentAlexBob = payments.getTotalPaymentFromTo("Alex", "Bob");
        double paymentAlexClara = payments.getTotalPaymentFromTo("Alex", "Clara");
        double paymentBobDennis = payments.getTotalPaymentFromTo("Bob", "Dennis");

        assertThat(paymentAlexBob).isEqualTo(10.0);
        assertThat(paymentAlexClara).isEqualTo(3.0);
        assertThat(paymentBobDennis).isEqualTo(2.0);
    }
}