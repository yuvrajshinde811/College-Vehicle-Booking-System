package com.yuvraj.test;

import com.yuvraj.hibernate.dao.HibernateVehicleDAO;
import com.yuvraj.hibernate.entity.VehicleEntity;
import com.yuvraj.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

/**
 * Task 5 Test Suite: Verifies Hibernate 6 Architecture, CRUD, HQL,
 * Criteria Queries, and First-Level Cache.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestHibernateOperations {

    private static HibernateVehicleDAO hibernateDAO;
    private static int createdVehicleId;
    private static final String HIB_VEHICLE_NUM = "MH-12-HIB-" + (System.currentTimeMillis() % 10000);

    @BeforeAll
    public static void init() {
        hibernateDAO = new HibernateVehicleDAO();
    }

    @AfterAll
    public static void tearDown() {
        if (createdVehicleId > 0) {
            try {
                hibernateDAO.deleteVehicle(createdVehicleId);
            } catch (Exception ignored) {}
        }
        HibernateUtil.shutdown();
    }

    @Test
    @Order(1)
    public void testSessionFactoryCreation() {
        System.out.println("HIBERNATE TEST 1: Building SessionFactory from hibernate.cfg.xml...");
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Assertions.assertNotNull(sf, "SessionFactory should be successfully initialized");
        Assertions.assertTrue(sf.isOpen(), "SessionFactory should be open");
        System.out.println("  -> SUCCESS: SessionFactory built successfully.");
    }

    @Test
    @Order(2)
    public void testHibernatePersistAndFind() {
        System.out.println("HIBERNATE TEST 2: Testing persist() and get() Operations...");
        VehicleEntity v = new VehicleEntity(HIB_VEHICLE_NUM, "Hibernate Electric Bus", 35, "ACTIVE");
        createdVehicleId = hibernateDAO.saveVehicle(v);
        Assertions.assertTrue(createdVehicleId > 0, "Persisted vehicle ID must be generated");

        VehicleEntity found = hibernateDAO.getVehicleById(createdVehicleId);
        Assertions.assertNotNull(found);
        Assertions.assertEquals(HIB_VEHICLE_NUM, found.getVehicleNumber());
        Assertions.assertEquals("Hibernate Electric Bus", found.getVehicleType());
        Assertions.assertEquals(35, found.getCapacity());
        System.out.println("  -> SUCCESS: Persisted and retrieved entity ID = " + createdVehicleId);
    }

    @Test
    @Order(3)
    public void testHibernateUpdate() {
        System.out.println("HIBERNATE TEST 3: Testing merge() (Update) Operation...");
        VehicleEntity v = hibernateDAO.getVehicleById(createdVehicleId);
        Assertions.assertNotNull(v);

        v.setVehicleType("Hibernate Solar Hybrid Bus");
        v.setCapacity(40);
        hibernateDAO.updateVehicle(v);

        VehicleEntity updated = hibernateDAO.getVehicleById(createdVehicleId);
        Assertions.assertEquals("Hibernate Solar Hybrid Bus", updated.getVehicleType());
        Assertions.assertEquals(40, updated.getCapacity());
        System.out.println("  -> SUCCESS: Updated vehicle entity in database.");
    }

    @Test
    @Order(4)
    public void testHqlQuery() {
        System.out.println("HIBERNATE TEST 4: Testing HQL (Hibernate Query Language)...");
        List<VehicleEntity> vehicles = hibernateDAO.findActiveVehiclesByMinCapacity(10);
        Assertions.assertFalse(vehicles.isEmpty(), "HQL should return matching active vehicles");
        System.out.println("  -> SUCCESS: HQL returned " + vehicles.size() + " active vehicles matching capacity.");
    }

    @Test
    @Order(5)
    public void testCriteriaApiQuery() {
        System.out.println("HIBERNATE TEST 5: Testing Criteria API Query...");
        List<VehicleEntity> vehicles = hibernateDAO.findVehiclesUsingCriteria("Bus");
        Assertions.assertFalse(vehicles.isEmpty(), "Criteria API should return matching vehicles");
        System.out.println("  -> SUCCESS: Criteria API returned " + vehicles.size() + " vehicles matching type 'Bus'.");
    }

    @Test
    @Order(6)
    public void testFirstLevelCache() {
        System.out.println("HIBERNATE TEST 6: Testing First-Level (Session) Cache...");
        boolean sameReference = hibernateDAO.demonstrateFirstLevelCache(createdVehicleId);
        Assertions.assertTrue(sameReference, "In first-level cache, two get() calls in same session must return identical object reference");
        System.out.println("  -> SUCCESS: Verified First-Level Session Cache without duplicate SQL query.");
    }
}
