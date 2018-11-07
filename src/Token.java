/**
 * Type of a token message, to manage critical sections
 * System message
 */
public class Token {

    private int id;

    /**
     * Constructor for a Token
     * @param id ID of the owner of the Token
     */
    public Token(int id) {

        this.id = id;

    }

    /**
     * Returns the ID of the owner of the Token
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the owner
     * @param id Owner of the Token
     */
    public void setId(int id) {
        this.id = id;
    }
}
