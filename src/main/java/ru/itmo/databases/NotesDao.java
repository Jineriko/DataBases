package ru.itmo.databases;

import java.sql.*;

public class NotesDao {
    public boolean create() {
        String createSql = "CREATE TABLE IF NOT EXISTS tb_notes(" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(120) NOT NULL, " +
                "text TEXT NOT NULL, " +
                "created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL CHECK (created_at <= CURRENT_TIMESTAMP), " +
                "author_id INTEGER NOT NULL, " +
                "CONSTRAINT fk_author_notes " +
                "FOREIGN KEY (author_id) " +
                "REFERENCES tb_authors (id))";

        try (Connection connection = C3P0Pool.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createSql);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(Note note, Author author) {
        String insertSql = "INSERT INTO tb_notes (title, text, author_id) " +
                "VALUES (?, ?, ?)";

        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, note.getTitle());
                ps.setString(2, note.getText());
                ps.setInt(3, author.getId());
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Note getById(int id) {
        String selectSql = "SELECT id, title, text, author_id " +
                "FROM tb_notes " +
                "WHERE id = ?";
        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();

                if (resultSet.next()) {
                    Note note = new Note();
                    note.setId(resultSet.getInt("id"));
                    note.setTitle(resultSet.getString("title"));
                    note.setText(resultSet.getString("text"));
                    note.setAuthor((Author) resultSet.getObject("author_id"));
                    return note;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Note getByAuthorId(int author_id) {
        String selectSql = "SELECT id, title, text, author_id " +
                "FROM tb_notes " +
                "WHERE author_id = ?";
        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setInt(1, author_id);
                ResultSet resultSet = ps.executeQuery();

                if (resultSet.next()) {
                    Note note = new Note();
                    note.setId(resultSet.getInt("id"));
                    note.setTitle(resultSet.getString("title"));
                    note.setText(resultSet.getString("text"));
                    note.setAuthor((Author) resultSet.getObject("author_id"));
                    return note;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Note getFirstId() {
        String selectSql = "SELECT id, title, text, author_id " +
                "FROM tb_notes " +
                "WHERE id > 5 " +
                "LIMIT 10";
        try (Connection connection = C3P0Pool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ResultSet resultSet = ps.executeQuery();

                if (resultSet.next()) {
                    Note note = new Note();
                    note.setId(resultSet.getInt("id"));
                    note.setTitle(resultSet.getString("title"));
                    note.setText(resultSet.getString("text"));
                    note.setAuthor((Author) resultSet.getObject("author_id"));
                    return note;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

/*
Домашнее задание

1. CHECK на created_at - дата в прошлом
2. INSERT tb_notes
3. SELECT tb_notes по идентификатору
4. SELECT tb_notes по идентификатору автора
5. SELECT tb_notes c LIMIT и OFFSET

(вместо чисел ? знак ставить)
*/
