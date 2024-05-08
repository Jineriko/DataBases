package ru.itmo.databases;

public class Databases {
    public static void main(String[] args) {
        System.out.println("Databases Lessons");

        Author author = new Author();
        author.setUniqueName("aut001");

        AuthorDao authorDao = new AuthorDao();
        authorDao.createTable();
    }
}
