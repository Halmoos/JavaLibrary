package com.example.JavaLibrary.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    private BookService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BookService(bookRepository);
    }

    @Test
    void canGetAllBooks() {
        // when
        underTest.getBooks();
        // then
        verify(bookRepository).findAll();
    }

    @Test
    void canGetBookById() {
        // given
        Long bookId = 1L;

        // when
        underTest.getBookById(bookId);

        // then
        verify(bookRepository, times(1)).findById(bookId);
    }

    @SuppressWarnings("null")
    @Test
    void canAddBook() {
        // given
        Book book = new Book(
                "Test book",
                "test author",
                LocalDate.of(2020, 3, 31),
                "Test genre");

        // when
        underTest.addBook(book);

        // then
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(bookArgumentCaptor.capture());

        Book capturedBook = bookArgumentCaptor.getValue();

        assertEquals(capturedBook, book);
    }

    @Test
    void willThrowBookNameTakenOnAdd() {
        // given
        Book existingBook = new Book("Test book",
                                     "existing author", 
                                     LocalDate.now(), 
                                     "Existing genre");
        Book bookToAdd = new Book("Test book",
                                 "test author", 
                                 LocalDate.of(2020, 3, 31), 
                                 "Test genre");

        // when
        when(bookRepository.findBookByName(bookToAdd.getName())).thenReturn(Optional.of(existingBook));

        // then
        Exception thrown = assertThrows(IllegalStateException.class,
                () -> underTest.addBook(bookToAdd),
                "Excepted willThrowBookNameTakenOnAdd() to throw, but didnt");
        assertTrue(thrown.getMessage().equals("Book name is already taken"));

        verify(bookRepository, never()).save(bookToAdd);
    }

    @Test
    void willDeleteBook() {
        // given 
        Long bookId = 1L;

        // when
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // then
        underTest.deleteBook(bookId);
        verify(bookRepository).deleteById(bookId);
        
    }

    @Test
    void willThrowBookIdNotFoundOnDelete() {
        // given
        Long bookId = 1L;

        // when
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // then
        Exception thrown = assertThrows(IllegalStateException.class,
                () -> underTest.deleteBook(bookId),
                "Excepted willThrowBookIdNotFoundOnDelete() to throw, but didnt");
        assertTrue(thrown.getMessage().equals("Book with id " + bookId + " does not exist"));
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void canUpdateBook() {
        // given
        Long bookId =  1L;
        Book existingBook = new Book(bookId, "Old Name", "Old Author", LocalDate.of(2020, 3, 31), "Old Genre");
        Book updatedBook = new Book(bookId, "New Name", "Old Author", LocalDate.now(), "New Genre");
        Book bookSpy = spy(existingBook);

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookSpy));
        when(bookRepository.findBookByName(updatedBook.getName())).thenReturn(Optional.empty());

        // then
        underTest.updateBook(bookId, updatedBook);

        verify(bookRepository).findById(bookId);
        verify(bookRepository).findBookByName(updatedBook.getName());

        verify(bookSpy).setName(updatedBook.getName());
        verify(bookSpy, atMostOnce()).setAuthor(updatedBook.getAuthor());
        verify(bookSpy, atMostOnce()).setPublishDate(updatedBook.getPublishDate());
        verify(bookSpy, atMostOnce()).setGenre(updatedBook.getGenre());

        assertThat(bookSpy).usingRecursiveComparison().isEqualTo(updatedBook);

    }

    @Test
    void willThrowBookNotFoundOnUpdate() {
        // given
        Long bookId =  1L;
        Book updatedBook = new Book(bookId, "New Name", "New Author", LocalDate.now(), "New Genre");

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // then
        Exception thrown = assertThrows(IllegalStateException.class,
                () -> underTest.updateBook(bookId, updatedBook),
                "Excepted willThrowBookNotFoundOnUpdate() to throw, but didnt");
        assertTrue(thrown.getMessage().equals("Book with id " + bookId + " does not exist"));
    }

    @Test
    void willThrowBookNameTakenOnUpdate() {
        // given
        Long bookId =  1L;
        Book existingBook = new Book(bookId, "Old Name", "Old Author", LocalDate.now(), "Old Genre");
        Book updatedBook = new Book(bookId, "Existing Name", "New Author", LocalDate.now(), "New Genre");
        Book bookSpy = spy(existingBook);

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findBookByName(updatedBook.getName())).thenReturn(Optional.of(existingBook));

        // then
        Exception thrown = assertThrows(IllegalStateException.class,
                () -> underTest.updateBook(bookId, updatedBook),
                "Excepted willThrowBookNotFoundOnUpdate() to throw, but didnt");
        assertTrue(thrown.getMessage().equals("This book name is already taken"));

        verify(bookSpy, never()).setName(updatedBook.getName());
        verify(bookSpy, never()).setAuthor(updatedBook.getAuthor());
        verify(bookSpy, never()).setPublishDate(updatedBook.getPublishDate());
        verify(bookSpy, never()).setGenre(updatedBook.getGenre());
    }

}
