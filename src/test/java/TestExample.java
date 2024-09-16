import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestExample {
    private static TestExample packageDao;
    private static EntityManagerFactory emfTest;


    @BeforeAll
    static void setUpAll() {
    }

    @AfterAll
    public static void tearDown() {
        // entityManager.close();
    }

    @Test
    public void testPersistPackage() {

            /*
            Package pkg = new Package();
            pkg.setTrackingNumber("ABC123");
            pkg.setSenderName("Sender");
            pkg.setReceiverName("Receiver");
            pkg.setDeliveryStatus(DeliveryStatus.PENDING);

            packageDAO.persistPackage(pkg);

            // Retrieve the package from the database and assert its existence
            Package retrievedPackage = entityManager.find(Package.class, pkg.getId());
            Assertions.assertNotNull(retrievedPackage);
            Assertions.assertEquals("ABC123", retrievedPackage.getTrackingNumber());

             */
    }

}
