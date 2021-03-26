package edu.wctc.refactored;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wctc.data.Book;
import edu.wctc.data.Borrower;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    public static List<Borrower> borrowerList;
    public static List<Book> availableBookList;


    // Read the JSON files into the data lists and display them
    public static void readDataFiles() {
        // Create empty lists
        availableBookList = new ArrayList<>();
        borrowerList = new ArrayList<>();

        // Create a JSON object mapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Create Java objects from the JSON data and stream into the lists
            Stream.of(mapper.readValue(new File("books.json"), Book[].class)).forEach(availableBookList::add);
            Stream.of(mapper.readValue(new File("borrowers.json"), Borrower[].class)).forEach(borrowerList::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Put the lists into the window components for display
        LibraryWindow.displayAvailableBooks(availableBookList);
        LibraryWindow.displayBorrowers(borrowerList);
    }

    public static void searchBooks() {
        // Get search string from the text box
        String searchTerm = LibraryWindow.getBookSearchTerm();

        // Filter available books to those matching the search string
        List<Book> matches = availableBookList.stream()
                .filter(b ->
                        b.getAuthor().toString().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                b.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        // Display matches
        LibraryWindow.displayAvailableBooks(matches);
    }

    public static void searchBorrowers() {
        // Get search string from the text box
        String searchTerm = LibraryWindow.getBorrowerSearchTerm();

        // Filter borrowers to those matching the search string
        List<Borrower> matches = borrowerList.stream()
                .filter(b -> b.toString().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        // Display matches
        LibraryWindow.displayBorrowers(matches);
    }
    public static void writeDataFiles() {
        // Create a JSON mapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Write the data lists as JSON to the files
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("books.json"), availableBookList);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("borrowers.json"), borrowerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
