package ru.itmo.databases;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class C3P0Pool {
    private static ComboPooledDataSource pool =
            new ComboPooledDataSource();

//    static  {
//        pool.setDriverClass("org.postgresql.Driver");
//    }

    // необходим вызов метода close у объекта Connection
    // для того, чтобы вернуть соединение в пул
    public static Connection getConnection() throws SQLException { // нужно закрывать методом close или try with resourses
        return pool.getConnection();
    }
}
