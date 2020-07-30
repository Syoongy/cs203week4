package csd.week4.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookClient {
    
    private RestTemplate template;
    
    @Autowired
    void setTemplate(final RestTemplate template) {
        this.template = template;
    }

    /**
     * @Bean: indicates that this method creates a bean, and Spring will add it to the app context
     * Spring Boot does not auto-configure a RestTemplate by default
     * @param builder
     * @return an instance of RestTemplate
     */
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Other configurations here if needed
        return builder.build();
    }

    /**
     * Get a book with given id
     * 
     * @param URI
     * @param id
     * @return
     */
    public Book getBook(final String URI, final Long id) {
        final Book book = template.getForObject(URI + "/" + id, Book.class);
        return book;
    }

    /**
     * Add a new book
     * 
     * @param URI
     * @param newBook
     * @return
     */
    public Book addBook(final String URI, final Book newBook) {
        final Book returned = template.postForObject(URI, newBook, Book.class);
        return returned;
    }
    
}