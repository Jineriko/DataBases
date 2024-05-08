package ru.itmo.databases;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
    public boolean createTable() {
        // Верхний регистр для sql, нижний для нас. Рекомендация, но не обязательно
        String createSql = "CREATE TABLE IF NOT EXISTS tb_authors (" +
                "id SERIAL PRIMARY KEY," + // уникальный индекс, автоматом ставится
                "unique_name VARCHAR(50) NOT NULL, " + // ограничение в 50 символов
                "registered_at DATE DEFAULT CURRENT_DATE NOT NULL," + // дата, по дефолту текущее время, если передаем то не null
                "is_active BOOLEAN DEFAULT TRUE NOT NULL)";
        try {
            Class.forName("org.postgresql.Driver"); // загрузка класса в память во время выполнения
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // import java.sql.*
        // JDBC стандарт интерфейса для БД
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/db_name",
                "jjd-user",
                "123456789"
        )) {
            try (Statement statement = connection.createStatement()) { // создаем соединение, объект через которое будет соединение. Для выполнение запроса
                statement.executeUpdate(createSql); // метод позволяет выполнить запрос,
                // которые не начинает со слов select (создание, обновление, удаление таблиц или записей)
                // метод возвращает количество модифицированных строк или исключение, если ошибка
                return true;
            }

        } catch (SQLException e) {
            // сервер недоступен, неверно указан адрес сервера, логин и пароль, либо проблема с БД
            throw new RuntimeException(e);
        }
    }

    public int[] insert(List<Author> authors) {
//        String insertSql = "INSERT INTO tb_authors (unique_name, is_active) " +
//                "VALUES (" + author.getUniqueName() + ", " + author.isActive() + ")";
        String insertSql = "INSERT INTO tb_authors (unique_name, is_active) " + // prepareStatement, данные подготавливаются перед отправкой, во избежании ошибок
                "VALUES (?, ?)";

        try (Connection connection = C3P0Pool.getConnection()) { // аналог, как на 26 строке. Только для подготовленных данных
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                for (Author author : authors) {
                    ps.setString(1, author.getUniqueName());
                    ps.setBoolean(2, author.isActive());
                    ps.addBatch(); // добавление в лист (накопление запросов)
                }
                return ps.executeBatch(); // в файле c3p0.properties добавляем после названия базы ?reWriteBatchedInserts=true
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Author author) { // обновление в БД значения автора
        String insertSql = "UPDATE tb_authors SET is_active = ? " +
                "WHERE unique_name = ?"; // также можно использовать и другие логические операторы

        try (Connection connection = C3P0Pool.getConnection()) { // аналог, как на 26 строке. Только для подготовленных данных
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setBoolean(1, author.isActive());
                ps.setString(2, author.getUniqueName());
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Author getByUniqueName(String uniqueName) {
        String selectSql = "SELECT id, unique_name, registered_at AS registered, is_active " + // AS - добавляет псевдоним названию. выборка в рамках одного запроса.
                "FROM tb_authors " +
                "WHERE unique_name = ?";
        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setString(1, uniqueName);
                ResultSet resultSet = ps.executeQuery(); // считывание запроса из таблицы БД

                if (resultSet.next()) { // одна запись, если записей больше нашлось то нужен while()
                    Author author = new Author();
                    author.setId(resultSet.getInt("id"));
                    author.setUniqueName(resultSet.getString("unique_name"));
                    author.setRegisteredAt(resultSet.getObject("registered", LocalDate.class)); // getDate не подойдет, т.к. возвращает не нужный тип данных, поэтому сначала object
                    author.setActive(resultSet.getBoolean("is_active")); // нужна проверка на null
                    return author;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Author> allAuthors() {
        String selectSql = "SELECT id, unique_name, registered_at, is_active " +
                "FROM tb_authors WHERE is_active = true";

        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ResultSet resultSet = ps.executeQuery();
                List<Author> authors = new ArrayList<>();
                while (resultSet.next()) {
                    Author author = new Author();
                    author.setId(resultSet.getInt("id"));
                    author.setUniqueName(resultSet.getString("unique_name"));
                    author.setRegisteredAt(resultSet.getObject("registered", LocalDate.class));
                    author.setActive(resultSet.getBoolean("is_active"));
                    authors.add(author);
                }
                return authors;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
