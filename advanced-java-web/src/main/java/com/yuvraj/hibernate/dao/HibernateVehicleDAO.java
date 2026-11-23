package com.yuvraj.hibernate.dao;

import com.yuvraj.hibernate.entity.VehicleEntity;
import com.yuvraj.hibernate.util.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Demonstrates Task 5 Hibernate Operations:
 * 1. Session and Transaction Management
 * 2. Save, Find, Update, Delete (CRUD)
 * 3. HQL Queries (Hibernate Query Language)
 * 4. Criteria API
 * 5. First-Level (Session) Cache Demonstration
 */
public class HibernateVehicleDAO {

    // 1. SAVE (PERSIST) OPERATION
    public int saveVehicle(VehicleEntity vehicle) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(vehicle);
            tx.commit();
            return vehicle.getVehicleId();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // 2. RETRIEVE BY ID (FIND)
    public VehicleEntity getVehicleById(int vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(VehicleEntity.class, vehicleId);
        }
    }

    // 3. UPDATE OPERATION
    public void updateVehicle(VehicleEntity vehicle) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(vehicle);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // 4. DELETE OPERATION
    public void deleteVehicle(int vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            VehicleEntity v = session.get(VehicleEntity.class, vehicleId);
            if (v != null) {
                session.remove(v);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // 5. HQL QUERY DEMONSTRATION
    public List<VehicleEntity> findActiveVehiclesByMinCapacity(int minCapacity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM VehicleEntity v WHERE v.status = :status AND v.capacity >= :minCap ORDER BY v.capacity ASC";
            Query<VehicleEntity> query = session.createQuery(hql, VehicleEntity.class);
            query.setParameter("status", "ACTIVE");
            query.setParameter("minCap", minCapacity);
            return query.list();
        }
    }

    // 6. CRITERIA API DEMONSTRATION
    public List<VehicleEntity> findVehiclesUsingCriteria(String vehicleType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> cq = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = cq.from(VehicleEntity.class);

            cq.select(root).where(
                cb.and(
                    cb.equal(root.get("status"), "ACTIVE"),
                    cb.like(root.get("vehicleType"), "%" + vehicleType + "%")
                )
            );

            return session.createQuery(cq).getResultList();
        }
    }

    // 7. FIRST-LEVEL CACHE DEMONSTRATION
    public boolean demonstrateFirstLevelCache(int vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("  [Cache Demo] First call: session.get(VehicleEntity.class, " + vehicleId + ")");
            VehicleEntity v1 = session.get(VehicleEntity.class, vehicleId);

            System.out.println("  [Cache Demo] Second call (same session): session.get(VehicleEntity.class, " + vehicleId + ")");
            VehicleEntity v2 = session.get(VehicleEntity.class, vehicleId);

            // In first-level cache, v1 and v2 refer to the EXACT SAME in-memory reference!
            boolean isSameReference = (v1 == v2);
            System.out.println("  [Cache Demo] Are references identical (v1 == v2)? " + isSameReference);
            return isSameReference;
        }
    }
}
