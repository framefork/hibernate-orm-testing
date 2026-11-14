package org.framefork.testing.hibernate.v64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = Book.TABLE_NAME)
public class Book
{

    public static final String TABLE_NAME = "test_book";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    protected Book()
    {
    }

    public Book(final String title)
    {
        this.title = title;
    }

    public Long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

}
