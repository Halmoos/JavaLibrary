package com.example.JavaLibrary.book;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String author;
    private LocalDate publishDate;
    private String genre;

    public Book() {

    }

    public Book(Long id,
            String name,
            String author,
            LocalDate publishDate,
            String genre) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
        this.genre = genre;

    }

    public Book(String name,
            String author,
            LocalDate publishDate,
            String genre) {
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
        this.genre = genre;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publishDate=" + publishDate +
                ", genre=" + genre +
                '}';
    }

}
