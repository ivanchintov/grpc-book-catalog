package io.github.ivanchintov.bookcatalog.validation;

import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.grpc.Status;
import org.apache.commons.validator.routines.ISBNValidator;

import java.time.Year;

public class AddBookValidator {

    public void validateTitle(String title) {
        if (title.isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Title cannot be empty.")
                    .asRuntimeException();
        }

        if (title.length() > 500) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Title cannot exceed 500 characters.")
                    .asRuntimeException();
        }
    }

    public void validateAuthor(String author) {
        if (author.isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Author cannot be empty.")
                    .asRuntimeException();
        }

        if (author.length() > 300) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Author cannot exceed 300 characters.")
                    .asRuntimeException();
        }
    }

    public void validatePublicationYear(int year) {
        if (year == 0) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Publication year cannot be empty.")
                    .asRuntimeException();
        }

        if (year < 1 || year > Year.now().getValue()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Publication year must be a positive year and cannot be in the future.")
                    .asRuntimeException();
        }
    }

    public void validateGenre(Genre genre) {
        if (genre == Genre.GENRE_UNSPECIFIED) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Genre cannot be empty.")
                    .asRuntimeException();
        }
    }

    public String validateIsbn(String isbn) {
        if (isbn.isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("ISBN cannot be empty.")
                    .asRuntimeException();
        }

        ISBNValidator isbnValidator = ISBNValidator.getInstance();
        String validatedIsbn = isbnValidator.validate(isbn);

        if (validatedIsbn == null) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("ISBN is not valid.")
                    .asRuntimeException();
        }

        return validatedIsbn;
    }
}
