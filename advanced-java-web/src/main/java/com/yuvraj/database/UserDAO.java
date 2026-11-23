
package com.yuvraj.database;

/**
 * Backward compatibility subclass preserving the original package location com.yuvraj.database.UserDAO.
 * Inherits all complete CRUD and authentication methods from com.yuvraj.dao.UserDAO.
 */
public class UserDAO extends com.yuvraj.dao.UserDAO {

    public UserDAO() {
        super();
    }
}

