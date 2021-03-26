package edu.wctc.refactored;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wctc.data.Book;
import edu.wctc.data.Borrower;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LibraryWindow extends JFrame {

    // GUI widgets
    private static JList<Borrower> lstBorrowers = new JList<>();
    private static JList<Book> lstBooksOnLoan = new JList<>();
    private static JList<Book> lstBooksAvailable = new JList<>();
    private static JButton btnCheckoutBook = new JButton("Checkout Selected Book");
    private static JButton btnReturnBook = new JButton("Return Selected Book");
    private static JTextField txtBorrowerSearch = new JTextField();
    private static JButton btnBorrowerSearch = new JButton("Search Borrowers");
    private static JTextField txtBookSearch = new JTextField();
    private static JButton btnBookSearch = new JButton("Search Books");
    private static JButton btnSaveAndExit = new JButton("Save and Exit");

    public LibraryWindow() {
        super("Library Lending System");

        // Read JSON files into the data lists
        Controller.readDataFiles();

        // Set up the GUI and layout components
        init();
        // Set window size and fit components
        setPreferredSize(new Dimension(800, 600));
        pack();
        // When the GUI is closed, the program ends
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void returnBook() {
        // Get selections from the JLists (window widgets)
        Borrower borrower = LibraryWindow.getSelectedBorrower();
        Book book = LibraryWindow.getSelectedAvailableBook();

        // Ensure one of each is selected
        if (book == null || borrower == null) {
            JOptionPane.showMessageDialog(this, "You must select a borrower and a book to return.");
        } else {
            // Add the book to the data list and remove from the borrower
            Controller.availableBookList.add(book);
            borrower.returnBook(book);

            // Update the screen to show the book is checked out
            refreshBookDisplay(Controller.availableBookList);
        }
    }

    public void checkoutBook() {
        // Get selections from the JLists (window widgets)
        Borrower borrower = LibraryWindow.getSelectedBorrower();
        Book book = LibraryWindow.getSelectedAvailableBook();

        // Ensure one of each is selected
        if (book == null || borrower == null) {
            JOptionPane.showMessageDialog(this, "You must select a borrower and a book to checkout.");
        } else {
            // Remove book from data list and add to selected borrower
            Controller.availableBookList.remove(book);
            borrower.checkoutBook(book);

            // Update the screen to show the book is checked out
            refreshBookDisplay(Controller.availableBookList);
        }
    }

    private void init() {
        // Add actions to the buttons that will call a method when clicked
        btnCheckoutBook.addActionListener(e -> checkoutBook());
        btnReturnBook.addActionListener(e -> returnBook());

        btnBookSearch.addActionListener(e -> Controller.searchBooks());
        btnBorrowerSearch.addActionListener(e -> Controller.searchBorrowers());

        btnSaveAndExit.addActionListener(e -> saveAndExit());

        // When user selects borrower from the list, shows their borrowed books
        lstBorrowers.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displayBooksOnLoan();
            }
        });

        // Restrict JLists to a single selected item
        lstBorrowers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstBooksAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstBooksOnLoan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // The rest of this method lays out the GUI components
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnlLeft.add(txtBorrowerSearch, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        pnlLeft.add(btnBorrowerSearch, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        JLabel lbl = new JLabel("Borrowers", SwingConstants.LEFT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        pnlLeft.add(lbl, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        pnlLeft.add(new JScrollPane(lstBorrowers), new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
        splitPane.add(pnlLeft, JSplitPane.LEFT);

        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnlRight.add(txtBookSearch, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        pnlRight.add(btnBookSearch, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        lbl = new JLabel("Books in the Library", SwingConstants.LEFT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        pnlRight.add(lbl, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        pnlRight.add(new JScrollPane(lstBooksAvailable), new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
        pnlRight.add(btnCheckoutBook, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        lbl = new JLabel("Books on Loan", SwingConstants.LEFT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        pnlRight.add(lbl, new GridBagConstraints(0, 4, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        pnlRight.add(new JScrollPane(lstBooksOnLoan), new GridBagConstraints(0, 5, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
        pnlRight.add(btnReturnBook, new GridBagConstraints(0, 6, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        splitPane.add(pnlRight, JSplitPane.RIGHT);

        splitPane.setDividerLocation(350);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.add(btnSaveAndExit);

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.add(splitPane, BorderLayout.CENTER);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        getContentPane().add(pnlMain);
    }
    public static void displayBooksOnLoan() {
        Borrower borrower = lstBorrowers.getSelectedValue();
        lstBooksOnLoan.setListData(borrower.getBooksOnLoan());
    }
    public static void refreshBookDisplay(List<Book> availableBookList) {
        // Update both book lists (available and on loan) in the window
        displayAvailableBooks(availableBookList);
        displayBooksOnLoan();
    }

    public void saveAndExit() {
        // Write the borrower and book lists to JSON files
        Controller.writeDataFiles();

        // Close this window, which will exit the program
        this.dispose();
    }

    public static void displayAvailableBooks(List<Book> list) {
        // Set the data in the JList (window widget)
        lstBooksAvailable.setListData(list.toArray(new Book[0]));
    }

    public static void displayBorrowers(List<Borrower> list) {
        // Set the data in the JList (window widget)
        lstBorrowers.setListData(list.toArray(new Borrower[0]));
    }
    public static String getBookSearchTerm() {
        return txtBookSearch.getText();
    }

    // Get search term from the text box
    public static String getBorrowerSearchTerm() {
        return txtBorrowerSearch.getText();
    }

    // Get selection from the JList
    public static Book getSelectedAvailableBook() {
        return lstBooksAvailable.getSelectedValue();
    }

    // Get selection from the JList
    public static Book getSelectedBookOnLoan() {
        return lstBooksOnLoan.getSelectedValue();
    }

    // Get selection from the JList
    public static Borrower getSelectedBorrower() {
        return lstBorrowers.getSelectedValue();
    }
}
