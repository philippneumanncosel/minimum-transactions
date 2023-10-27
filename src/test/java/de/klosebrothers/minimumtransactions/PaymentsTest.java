package de.klosebrothers.minimumtransactions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentsTest {

    private static final String TEST_GENERATED_RESOURCES_PATH = "src/test/generated/resources/";
    private Payments payments;

    @BeforeAll
    static void beforeAll() {
        try {
            Files.createDirectories(Paths.get(TEST_GENERATED_RESOURCES_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        payments = new Payments();
    }

    @AfterEach
    void tearDown() {
        File testGeneratedResourcesDirectory = new File(TEST_GENERATED_RESOURCES_PATH);
        try {
            FileUtils.cleanDirectory(testGeneratedResourcesDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void itShouldReturnZeroForUnknownPersons() {
        double payment = payments.getTotalPaymentFromTo("unknown", "alsoUnkown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownGiver() {
        payments.registerPayment("sampleName", 10.0, "known");

        double payment = payments.getTotalPaymentFromTo("unknown", "known");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForUnknownRecipient() {
        payments.registerPayment("known", 10.0, "sampleName");

        double payment = payments.getTotalPaymentFromTo("known", "unknown");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnZeroForKnownPersonsWithNoPaymentHistory() {
        payments.registerPayment("A", 10.0, "B");
        payments.registerPayment("B", 10.0, "C");

        double payment = payments.getTotalPaymentFromTo("A", "C");

        assertThat(payment).isZero();
    }

    @Test
    void itShouldReturnPayment() {
        payments.registerPayment("Alex", 10.0, "Bob");

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(10.0);
    }

    @Test
    void itShouldReturnSumOfMultiplePayments() {
        payments.registerPayment("Alex", 10.0, "Bob");
        payments.registerPayment("Alex", 3.0, "Bob");
        payments.registerPayment("Alex", 2.0, "Bob");

        double payment = payments.getTotalPaymentFromTo("Alex", "Bob");

        assertThat(payment).isEqualTo(15.0);
    }

    @Test
    void itShouldReturnPaymentsForMultiplePersons() {
        registerExamplePayments();

        double paymentAlexBob = payments.getTotalPaymentFromTo("Alex", "Bob");
        double paymentBobClara = payments.getTotalPaymentFromTo("Bob", "Clara");
        double paymentBobDennis = payments.getTotalPaymentFromTo("Bob", "Dennis");

        assertThat(paymentAlexBob).isEqualTo(10.0);
        assertThat(paymentBobClara).isEqualTo(3.0);
        assertThat(paymentBobDennis).isEqualTo(2.0);
    }

    @Test
    void itShouldReturnPaymentsForRegisteredPaymentWithMultipleReceivers() {
        payments.registerPayment("Alex", 30.0, "Bob", "Clara", "Dennis");

        double paymentAlexBob = payments.getTotalPaymentFromTo("Alex", "Bob");
        double paymentAlexClara = payments.getTotalPaymentFromTo("Alex", "Clara");
        double paymentAlexDennis = payments.getTotalPaymentFromTo("Alex", "Dennis");

        assertThat(paymentAlexBob).isEqualTo(10.0);
        assertThat(paymentAlexClara).isEqualTo(10.0);
        assertThat(paymentAlexDennis).isEqualTo(10.0);
    }

    @Test
    void itShouldReturnPaymentsForRegisteredPaymentWithMultipleReceiversIncludingSelf() {
        payments.registerPayment("Alex", 40.0, "Bob", "Clara", "Dennis", "Alex");

        double paymentAlexBob = payments.getTotalPaymentFromTo("Alex", "Bob");
        double paymentAlexClara = payments.getTotalPaymentFromTo("Alex", "Clara");
        double paymentAlexDennis = payments.getTotalPaymentFromTo("Alex", "Dennis");

        assertThat(paymentAlexBob).isEqualTo(10.0);
        assertThat(paymentAlexClara).isEqualTo(10.0);
        assertThat(paymentAlexDennis).isEqualTo(10.0);
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
        payments.registerPayment("Alex", 6.0, "Clara");

        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                Bob owes Alex 10.0
                Clara owes Alex 6.0
                Clara owes Bob 3.0
                Dennis owes Bob 2.0""");
    }

    @Test
    void itShouldEliminateMinimalCyclicPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", 6.0, "Bob");
        payments.registerPayment("Bob", 5.0, "Alex");

        payments.eliminateAllCyclicPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("Bob owes Alex 1.0");
        assertThat(allInfluxes)
                .containsEntry("Alex", -1.0)
                .containsEntry("Bob", 1.0);
    }

    @Test
    void itShouldEliminateMultipleCyclicPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", 16.0, "Bob");
        payments.registerPayment("Bob", 10.0, "Claire");
        payments.registerPayment("Claire", 10.0, "Alex");
        payments.registerPayment("Bob", 5.0, "Alex");

        payments.eliminateAllCyclicPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("Bob owes Alex 1.0");
        assertThat(allInfluxes)
                .containsEntry("Alex", -1.0)
                .containsEntry("Bob", 1.0)
                .containsEntry("Claire", 0.0);
    }

    @Test
    void itShouldEliminateMinimalChainedPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", 5.0, "Bob");
        payments.registerPayment("Bob", 5.0, "Claire");

        payments.eliminateAllChainedPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("Claire owes Alex 5.0");
        assertThat(allInfluxes)
                .containsEntry("Alex", -5.0)
                .containsEntry("Bob", 0.0)
                .containsEntry("Claire", 5.0);
    }

    @Test
    void itShouldEliminateLargerChainedPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", 1.0, "Bob");
        payments.registerPayment("Bob", 2.0, "Claire");
        payments.registerPayment("Claire", 2.0, "Dennis");
        payments.registerPayment("Dennis", 1.0, "Eddy");
        payments.registerPayment("Eddy", 2.0, "Frank");

        payments.eliminateAllChainedPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                        Bob owes Alex 1.0
                        Dennis owes Eddy 1.0
                        Frank owes Bob 2.0""");
        assertThat(allInfluxes)
                .containsEntry("Alex", -1.0)
                .containsEntry("Bob", -1.0)
                .containsEntry("Claire", 0.0)
                .containsEntry("Dennis", 1.0)
                .containsEntry("Eddy", -1.0)
                .containsEntry("Frank", 2.0);
    }

    @Test
    void itShouldEliminateMultipleChainedPaymentsWhileRetainingEqualInfluxes() {
        payments.registerPayment("Alex", 2.0, "Bob");
        payments.registerPayment("Bob", 2.0, "Claire");
        payments.registerPayment("Claire", 1.0, "Dennis");
        payments.registerPayment("Dennis", 2.0, "Eddy");
        payments.registerPayment("Eddy", 2.0, "Frank");
        payments.registerPayment("Bob", 2.0, "Greg");
        payments.registerPayment("Greg", 2.0, "Claire");
        payments.registerPayment("Claire", 2.0, "Henry");
        payments.registerPayment("Henry", 3.0, "Frank");

        payments.eliminateAllChainedPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                        Claire owes Dennis 1.0
                        Frank owes Alex 2.0
                        Frank owes Henry 3.0
                        Henry owes Bob 2.0""");
        assertThat(allInfluxes)
                .containsEntry("Alex", -2.0)
                .containsEntry("Bob", -2.0)
                .containsEntry("Claire", 1.0)
                .containsEntry("Dennis", -1.0)
                .containsEntry("Eddy", 0.0)
                .containsEntry("Frank", 5.0)
                .containsEntry("Greg", 0.0)
                .containsEntry("Henry", -1.0);
    }

    @Test
    void itShouldEliminateIndirectPayments() {
        payments.registerPayment("Alex", 1.0, "Bob");
        payments.registerPayment("Alex", 1.0, "Claire");
        payments.registerPayment("Bob", 2.0, "Claire");

        payments.eliminateAllIndirectPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                        Claire owes Alex 2.0
                        Claire owes Bob 1.0""");
        assertThat(allInfluxes)
                .containsEntry("Alex", -2.0)
                .containsEntry("Bob", -1.0)
                .containsEntry("Claire", 3.0);
    }

    @Test
    void itShouldEliminateMultipleIndirectPayments() {
        payments.registerPayment("Alex", 1.0, "Bob");
        payments.registerPayment("Alex", 1.0, "Eddy");
        payments.registerPayment("Bob", 2.0, "Claire");
        payments.registerPayment("Claire", 3.0, "Dennis");
        payments.registerPayment("Claire", 3.0, "Eddy");
        payments.registerPayment("Dennis", 4.0, "Eddy");

        payments.eliminateAllIndirectPayments(false);
        Map<String, Double> allInfluxes = payments.getAllInfluxes();
        String resolvingPayments = payments.getResolvingPayments();

        assertThat(resolvingPayments).isEqualTo("""
                        Claire owes Bob 1.0
                        Eddy owes Alex 2.0
                        Eddy owes Claire 5.0
                        Eddy owes Dennis 1.0""");
        assertThat(allInfluxes)
                .containsEntry("Alex", -2.0)
                .containsEntry("Bob", -1.0)
                .containsEntry("Claire", -4.0)
                .containsEntry("Eddy", 8.0)
                .containsEntry("Dennis", -1.0);
    }

    @Test
    void itShouldSimplifyRandomPaymentsWhileRetainingEqualInfluxes() {
        payments = new Payments("testsimplification", 25);
        registerRandomPayments(5, 100, 1337);
        Map<String, Double> expectedInfluxes = payments.getAllInfluxes();

        payments.simplify(false);

        Map<String, Double> actualInfluxes = payments.getAllInfluxes();
        assertThat(actualInfluxes).containsExactlyInAnyOrderEntriesOf(expectedInfluxes);
    }

    @Test
    void itShouldCreateGifOfStepsWhileSimplifyingPayments() {
        payments = new Payments("testsimplification", 4);
        registerRandomPayments(5, 100, 1337);

        payments.simplify(true);

        File createdGif = new File(TEST_GENERATED_RESOURCES_PATH + "testsimplification.gif");

        assertThat(createdGif).isFile();
    }

    @Test
    void itShouldSimplifyPaymentsForRealWorldData() {
        payments = new Payments("Plose", 1);
        registerRealWorldPayments();
        Map<String, Double> expectedInfluxes = payments.getAllInfluxes();

        payments.simplify(true);

        Map<String, Double> actualInfluxes = payments.getAllInfluxes();
        assertThat(actualInfluxes).containsExactlyInAnyOrderEntriesOf(expectedInfluxes);
    }

    private void registerExamplePayments() {
        payments.registerPayment("Alex", 10.0, "Bob");
        payments.registerPayment("Bob", 3.0, "Clara");
        payments.registerPayment("Bob", 2.0, "Dennis");
    }

    private void registerRandomPayments(int numberPersons, int numberPayments, int seed) {
        Random random = new Random(seed);
        for (int payment = 0; payment < numberPayments; payment++) {
            int amount = random.nextInt(1, 3);
            int giver = random.nextInt(1, numberPersons);
            int recipient = random.nextInt(1, numberPersons);
            if (giver == recipient) {
                ++recipient;
            }
            payments.registerPayment("Person"+giver, amount, "Person"+recipient);
        }
    }

    private void registerRealWorldPayments() {
        payments.registerPayment("Philly", 80.0, "Fabi", "Paul", "Philly");
        payments.registerPayment("Fabi", 80.0 , "Fabi", "Paul", "Philly");
        payments.registerPayment("Paul", 20.0, "Fabi");
        payments.registerPayment("Philly", 11.0, "Fabi", "Paul", "Philly");
        payments.registerPayment("Paul", 31.86, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte");
        payments.registerPayment("Fabi", 140.0, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte");
        payments.registerPayment("Philly", 70.0, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte");
        payments.registerPayment("Malte", 116.0, "Dunch", "Wiebke", "Malte");
        payments.registerPayment("Philly", 215.0, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte", "Janne");
        payments.registerPayment("Philly", 100.0, "Fabi", "Paul", "Philly", "Dunch", "Malte");
        payments.registerPayment("Fabi", 15.0, "Fabi", "Paul", "Philly", "Dunch", "Malte");
        payments.registerPayment("Philly", 40.0, "Philly", "Malte");
        payments.registerPayment("Paul", 7.5, "Malte");
        payments.registerPayment("Fabi", 145.0 , "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte", "Janne");
        payments.registerPayment("Fabi", 26.0 , "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte", "Janne");
        payments.registerPayment("Fabi", 37.0 , "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte", "Janne");
        payments.registerPayment("Fabi", 17.5 , "Fabi", "Philly", "Dunch", "Wiebke", "Janne");
        payments.registerPayment("Fabi", 13.8, "Philly", "Janne");
        payments.registerPayment("Fabi", 13.5, "Philly", "Dunch", "Janne");
        payments.registerPayment("Fabi", 10.0, "Philly", "Dunch");
        payments.registerPayment("Fabi", 7.9, "Dunch");
        payments.registerPayment("Fabi", 4.8, "Wiebke");
        payments.registerPayment("Fabi", 5.8, "Janne");
        payments.registerPayment("Philly", 400.0, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte", "Janne");
        payments.registerPayment("Philly", 200.0, "Fabi", "Paul", "Philly", "Dunch", "Wiebke", "Malte");
        payments.registerPayment("Fabi", 22.0, "Philly", "Dunch", "Wiebke", "Janne");
        payments.registerPayment("Fabi", 15.0, "Malte");
        payments.registerPayment("Janne", 58.0, "Fabi", "Paul", "Philly", "Janne");
        payments.registerPayment("Philly", 11.0, "Fabi", "Paul", "Philly", "Janne");
        payments.registerPayment("Fabi", 50.0, "Fabi", "Paul", "Philly", "Janne");
    }
}