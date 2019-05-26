package ws2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

import entity.Book;
import repository.BookService;

@Path("/fill")
public class WholeSalerService {

    private Gson gson = new Gson();

    @Path("/{isbn}/{quantity}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStock(@PathParam("isbn") String isbn, @PathParam("quantity") String nb) {

        BookService bookService = new BookService();
        Book book = bookService.getBookByIsbn(isbn);

        try {
            Integer quantity = Integer.valueOf(nb);
            if (book != null && quantity >= 0 && isValidIsbn(isbn)) {
                book.addQuantity(quantity);
                bookService.updateBook(book);
                String bookJson = sendBookJson(book);
                return Response.status(Response.Status.OK).entity(bookJson).build();
            } else {
                return notValidArgumentResponse(isbn, book, quantity);
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("not found the book").build();
        }

    }

    private Response notValidArgumentResponse(String isbn, Book book, Integer quantity) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("erreur", "Les information ci dessous sont invalid");
        if (!isValidIsbn(isbn))
            jsonResponse.addProperty("erreur", "L' isbn ne correspond à aucun livre.");
        else
            jsonResponse.addProperty("erreur", "Les information ci dessous sont invalid");

        if (book == null) {
            if (!isValidIsbn(isbn))
                jsonResponse.addProperty("isbn", isbn);
        }
        if (quantity < 0) {
            jsonResponse.addProperty("quantity", quantity);
        }

        String jsonString = jsonResponse.toString();

        return Response.status(Response.Status.NOT_FOUND).entity(jsonString).build();
    }


    private boolean isValidIsbn(String isbn) {
        return isbn.length() >= 10 && isbn.length() <= 13
                && NumberUtils.isNumber(isbn);
    }

    private String sendBookJson(Book book) {
        return this.gson.toJson(book);
    }
}