package csd.week4.book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcBookRepository implements BookRepository {

    private JdbcTemplate jdbcTemplate;

    // Autowired the JdbcTemplate with constructor injection
    public JdbcBookRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    /**
     * We need to return the auto-generated id of the insert operation
     * 
     */
    @Override
    public Long save(Book book) {
        // Use KeyHolder to obtain the auto-generated key from the "insert" statement
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement statement = conn.prepareStatement("insert into books (title) values (?) ",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, book.getTitle());
                return statement;
            }
        }, holder);

        Long primaryKey = holder.getKey().longValue();
        return primaryKey;
    }

    /**
     * TODO: Activity 1 - Implement the update method
     * 
     * This method nees to return the number of rows affected by the update
     */
    @Override
    public int update(Book book) {
        // your code here
        return jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title=? WHERE id=?");
                stmt.setString(1, book.getTitle());
                stmt.setLong(2, book.getId());
                return stmt;
            }
        });
    }

    /**
     * Return the number of rows affected by the delete
     */
    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "delete books where id = ?", id);
    }

    /**
     * TODO: Activity 1 - Add code to return all books
     * Hint: use the "query" method of JdbcTemplate
     * Refer to the below code of "findByID" method on how to implement a RowMapper
     * using a lambda expression
     * 
     */
    @Override
    public List<Book> findAll() {
        // your code here
        String stmt = "SELECT * FROM books";
        List<Book> books = jdbcTemplate.query(
                stmt,
                (rs, rowNum) -> new Book(rs.getLong("id"), rs.getString("title")));
        // return value should be changed
        return books;
    }

    /**
     * QueryForObject method: to query a single row in the database
     * 
     * The "select *" returns a ResultSet (rs)
     * The Lambda expression (an instance of RowMapper) returns an object instance
     * using "rs"
     * 
     * Optional: a container which may contain null objects
     * -> To handle the case in which the given id is not found
     */
    @Override
    public Optional<Book> findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from books where id = ?",
                    // implement RowMapper interface to return the book found
                    // using a lambda expression
                    (rs, rowNum) -> Optional.of(new Book(rs.getLong("id"), rs.getString("title"))),
                    new Object[] { id });

        } catch (EmptyResultDataAccessException e) {
            // book not found - return an empty object
            return Optional.empty();
        }
    }
}