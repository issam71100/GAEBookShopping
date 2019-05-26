package ws;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Book;
import org.apache.commons.lang3.math.NumberUtils;
import repository.BookService;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/stock")
public class StockService {

    private Gson gson = new Gson();

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response indexBook() {
        BookService bookService = new BookService();
        List<Book> books = bookService.getAllBooks();
        String bookJson = sendBooksJson(books);
        return Response.status(Response.Status.OK).entity(bookJson).build();
    }

    @Path("/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createBook(@FormParam("isbn") String isbn, @FormParam("name") String name,
                               @FormParam("price") String price) {
        if (!isValidIsbn(isbn))
            return Response.status(Response.Status.BAD_REQUEST).entity("entrez un isbn valide").build();
        if (!isValidPrice(price)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("entrez un prix").build();
        }
        BookService bookService = new BookService();
        price = price.replace(',', '.');
        Book book = bookService.createBook(isbn, name, Float.parseFloat(price));
        if (book == null)	return Response.status(Response.Status.BAD_REQUEST).entity("Ce livre existe déjà").build();
        String bookJson = sendBookJson(book);
        return Response.status(Response.Status.OK).entity(bookJson).build();
    }



    @Path("/{isbn}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("isbn") String isbn) {
        BookService bookService = new BookService();
        Book book = bookService.getBookByIsbn(isbn);
        if (book != null) {
            String bookJson = sendBookJson(book);
            return Response.status(Response.Status.OK).entity(bookJson).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("not found the book").build();
        }
    }

    @Path("/purchase/{isbn}/{quantity}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response substractBook(@PathParam("isbn") String isbn, @PathParam("quantity") String nb) {

        BookService bookService = new BookService();
        Book book = bookService.getBookByIsbn(isbn);
        try {
            Integer quantity = Integer.valueOf(nb);
            if (book != null && quantity >= 0 && isValidIsbn(isbn)) {
                if (book.getQuantity() - quantity < 0) {

                    /*
                     * Todo Service Wholesaler
                     */

                    System.setProperty("http.proxySet", "true");
                    System.setProperty("http.proxyHost", "xxxxxxxx");
                    System.setProperty("http.proxyPort", "8888");
                    System.setProperty("http.nonProxyHosts","xxxxxxx");
                    Client client = ClientBuilder.newClient();

                    WebTarget target = client.target("http://bookstore-240620.appspot.com/rest2/");

                    target = target.path("fill/" + isbn + "/"+ quantity);

                    Response fillStock = target.request().get();
			        /*
					if(fillStoc<200 || fillStock.getStatus() > 202) {
						jsonResponse.addProperty("erreur", "Le stock n'a pas pu être remplie");
						return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(jsonResponse.toString()).build();
					}*/

                    JsonObject jsonResponse = new JsonObject();



                    jsonResponse.addProperty("erreur", "Pas assez de livres en stock.");
					//jsonResponse.addProperty("erreur", "Pas assez de livres en stock. Un renouvellement de celui-ci est en cours. Merci de réessayez plus tard");
					jsonResponse.addProperty("état", "Renouvellement du stock cours. ");
					jsonResponse.addProperty("instructions", "Merci de réessayez plus tard");

                    return Response.status(Response.Status.CONFLICT).entity(jsonResponse.toString()).build();
                }
                book.subQuantity(quantity);
                bookService.updateBook(book);
                Book order = new Book(isbn,quantity,book.getPrice());
                String bookJson = sendBookJson(order);
                return Response.status(Response.Status.OK).entity(bookJson).build();
            } else {
                return notValidArgumentResponse(isbn, book, quantity);
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("not found the book").build();
        }
    }

    @Path("/add/{isbn}/{quantity}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStock(@PathParam("isbn") String isbn, @PathParam("quantity") String nb) {

        BookService bookService = new BookService();
        Book book = bookService.getBookByIsbn(isbn);
        try {
            Integer quantity = Integer.valueOf(nb);
            if (book != null && quantity >= 0 && isValidIsbn(isbn)) {
                book.addQuantity(quantity);
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

    private boolean isValidPrice(String price) {
        return NumberUtils.isNumber(price);
    }


    private String sendBookJson(Book book) {
        // TODO Auto-generated method stub

        return this.gson.toJson(book);
    }

    private String sendBooksJson(List<Book> books) {
        // TODO Auto-generated method stub

        return this.gson.toJson(books);
    }
}