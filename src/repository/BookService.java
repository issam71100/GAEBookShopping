package repository;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import entity.Book;

import java.util.List;

public class BookService {

    private Objectify objectify;

    public BookService() {

        ObjectifyService.run(new VoidWork() {
            public void vrun() {
                objectify = OfyService.ofy();
            }
        });
    }

    public List<Book> getAllBooks() {
        return objectify.load().type(Book.class).order("-addedAt").list();
    }

    public Book createBook() {
        Book book = new Book();
        book.setName("Naruto");
        objectify.save().entity(book);// persists our entity and sets a generated id
        return book;
    }

    public Book createBook(String isbn, String name, Float price) {
        if(getBookByIsbn(isbn) != null) return null;
        Book book = new Book();
        book.setName(name);
        book.setIsbn(isbn);
        System.out.println(price);
        book.setPrice(price);
        objectify.save().entity(book);// persists our entity and sets a generated id
        return book;
    }

    public Book updateBook(Book book) {
        objectify.save().entity(book);// persists our entity and sets a generated id
        return book;
    }

    public Book getBookByIsbn(String isbn) {
        // TODO Auto-generated method stub
        Book book = objectify.load().type(Book.class).id(isbn).now();
        return book;
    }
}
