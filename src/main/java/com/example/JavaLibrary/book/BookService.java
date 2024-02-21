package com.example.JavaLibrary.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(@NonNull Long bookId) {
        return bookRepository.findById(bookId);
    }

    public void addBook (Book book){
        Optional<Book> bookOptional = bookRepository.findBookByName(book.getName());
        if (bookOptional.isPresent()) {
            throw new IllegalStateException("Book name is already taken");
        }
        bookRepository.save(book);
    }

    public void deleteBook(@NonNull Long bookId) {
       boolean exists = bookRepository.existsById(bookId);
       if (!exists) {
        throw new IllegalStateException("Book with id " + bookId + " does not exist");
       }
       bookRepository.deleteById(bookId);
    }

    @Transactional
    public void updateBook(@NonNull Long bookId, Book updatedBook) {
        Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new IllegalStateException("Book with id " + bookId + " does not exist"));

        if (updatedBook.getName() != null && updatedBook.getName().length() > 0 && !Objects.equals(book.getName(), updatedBook.getName())) {
            Optional<Book> bookOptional = bookRepository.findBookByName(updatedBook.getName());
            if (bookOptional.isPresent()) {
                throw new IllegalStateException("This book name is already taken");
            }
            book.setName(updatedBook.getName());
        }

        if (updatedBook.getAuthor() != null && updatedBook.getAuthor().length() > 0 && !Objects.equals(book.getAuthor(), updatedBook.getAuthor())) {
            book.setAuthor(updatedBook.getAuthor());
        }

        if (updatedBook.getPublishDate() != null && !Objects.equals(book.getPublishDate(), updatedBook.getPublishDate())) {
            book.setPublishDate(updatedBook.getPublishDate());
        }

        if (updatedBook.getGenre() != null && updatedBook.getGenre().length() > 0 && !Objects.equals(book.getGenre(), updatedBook.getGenre())) {
            book.setGenre(updatedBook.getGenre());
        }
    }
    
    
}
