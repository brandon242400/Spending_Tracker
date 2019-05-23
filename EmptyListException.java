/**
 * Exception used for an attempt to remove an item from an empty list.
 */

 class EmptyListException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message
     * @param message
     */
    public EmptyListException(String message) {
        super(message);
    }

    /**
     * Constructor without message to display
     */
    public EmptyListException() {
        this(null);
    }
 }