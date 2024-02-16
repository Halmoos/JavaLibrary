package com.example.JavaLibrary.listener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.JavaLibrary.book.Book;
import com.example.JavaLibrary.book.BookRepository;
import com.github.javafaker.Faker;

@Component
public class DatabaseSeeder {
    
    private final BookRepository bookRepository;

    public DatabaseSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDatabase() {
        if (bookRepository.count() == 0) {
            seedBooks();
        }
    }

    public void seedBooks() {
        Faker faker = new Faker();
        for (int i = 0; i < 4; i++) {
            Book book = new Book();
            book.setName(faker.book().title());
            book.setAuthor(faker.book().author());
            LocalDate randomDate = faker.date().birthday().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate publishDate = randomDate.minus(10, ChronoUnit.YEARS);
            book.setPublishDate(publishDate);
            book.setGenre(faker.book().genre());
            bookRepository.save(book);
        }
    }
}