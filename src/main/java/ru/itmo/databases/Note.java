package ru.itmo.databases;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class Note {
    private long id;
    private String title;
    private String text;
    private OffsetDateTime createAt; // postgersql не умеет работать с localdatetime, но умеет с offsetdatetime
    private Author author; // author_id на уровне таблиц

}
