package entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Index
public class Book {
    private @Id String isbn;
    private String name;
    private Float price;
    private Integer quantity;
    private Date addedAt;

    public Book(){
        addedAt = new Date();
        quantity =0;
    }

    public Book(String isbn, Integer quantity, Float price){
        this.isbn = isbn;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return isbn;
    }
    public void setId(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public void addQuantity(Integer quantity) {
        this.quantity += quantity;
    }
    public void subQuantity(Integer quantity) {
        this.quantity -= quantity;
    }
    public Date getDate() {
        return addedAt;
    }
    public String getDateFormat() {
        SimpleDateFormat formater = new SimpleDateFormat("EEEE, d MMM yyyy");
        return formater.format(addedAt);
    }
    public void setDate(Date date) {
        this.addedAt = date;
    }

}
