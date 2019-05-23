import java.io.Serializable;
import java.util.Iterator;


    /**
     * Queue used to store actions made in the application
     */
    class BudgetQueue implements Iterable<BudgetQueue.Node>, Serializable {

		private static final long serialVersionUID = 32L;
        Node first;
        Node last;
        int count;


        /**
         * Constructor. Sets all values to null or 0
         */
        public BudgetQueue() {
            first = null;
            last = null;
            count = 0;
        }


        /**
         * Adds an item to the end of the queue with the element value, message, and date.
         * 
         * @param element to be added
         * @param message to be attached with element
         * @param date that element is added to queue
         */
        public void queue(Double element, String message, String date) {

            if (count == 0) {
                Node temp = new Node(element, message, date);
                first = temp;
                last = new Node();
                last.previous = first;
                first.next = last;
            } 
            else {
                last.element = element;
                last.description = message;
                last.date = date;
                last.next = new Node();
                Node temp = last;
                last = last.next;
                last.previous = temp;
            }

            count++;
        }


        /**
         * queue method without a message.
         * @param element to be added
         * @param date
         */
        public void queue(Double element, String date) {
            queue(element, null, date);
        }


        /**
         * queue method without a message or date.
         * @param element to be added
         */
        public void queue(Double element) {
            queue(element, null, null);
        }


        /**
         * Returns the Node at the beginning of the queue. (Typically the first one added)
         * 
         * @return Node
         * @throws EmptyListException
         */
        public Node dequeue() {
            try {
            if (isEmpty())
                throw new EmptyListException("No items in Queue");
            }
            catch(EmptyListException e) {
                return null;
            }

            Node temp = first;
            first = first.next;
            count--;
            return temp;
        }


        /**
         * Dequeue method that allows you to search for the Node to be removed.
         * Returns null if correct node isn't found.
         * 
         * @param element
         * @param message
         * @param date
         * @return Node that matches search or "null" if element not found
         */
        public Node dequeue(Double element, String message, String date) {
            Node temp = first;
            Node result = null;
            boolean equivalent = false;

            if(isEmpty()) {
                equivalent = true;
                count++;
            }


            while(!equivalent && temp != null) {

                if(element != null) {
                    if(temp.element.compareTo(element) != 0) {
                        temp = temp.next;
                        continue;
                    }
                    else
                        result = temp;
                }

                if(message != null) {
                    if(temp.description.compareTo(message) != 0) {
                        temp = temp.next;
                        continue;
                    }
                }

                if(date != null) {
                    if(temp.date != date) {
                        temp = temp.next;
                        continue;
                    }
                }

                equivalent = true;
                result = temp;
            }

            count--;
            return result;
        }


        /**
         * Dequeue operation to remove and return the Node from the end of the queue
         * instead of the front.
         * 
         * @return Node
         * @throws EmptyListException
         */
        public double dequeueLast() throws EmptyListException {
            if (isEmpty())
                throw new EmptyListException("No items in Queue");
            double result;
            last = last.previous;
            result = last.element;
            last.clear();
            last.next = null;
            count--;
            return result;
        }


        /**
         * Clears all nodes to begin from a new, clean queue.
         */
        @SuppressWarnings("unused")
        private void clearAllNodes() {
            count = 0;
            first = null;
            last = null;
        }


        /**
         * Returns true if Queue is empty.
         * 
         * @return boolean true if empty
         */
        public boolean isEmpty() {
            return count == 0;
        }


        @Override
        public Iterator<Node> iterator() {
            return new QueueIterator(first);
        }

        

        /**
         * Iterator class for queue
         */
        class QueueIterator implements Iterator<Node> {

            Node current;

            public QueueIterator(Node node) {
                current = node;
            }

            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public Node next() {
                Node temp = current;
                current = current.next;
                return temp;
            }

        }




        //________________________________________Start of Node inner class_________________________________________________________________

        /**
         * Node class to holds Queued objects.
         */
        class Node implements Serializable {

            private static final long serialVersionUID = 32L;
            Double element;
            String date;
            String description;
            Node next;
            Node previous;

            /**
             * Overloaded constructor
             * @param element
             * @param message
             */
            public Node(Double element, String message) {
                this(element, message, null);
            }

            /**
             * Overloaded constructor
             * @param element
             * @param message
             * @param date
             */
            public Node(Double element, String message, String date) {
                this.element = element;
                next = null;
                this.date = date;
                previous = null;
                description = message;
            }

            /**
             * Overloaded constructor
             * @param element
             */
            public Node(Double element) {
                this(element, null, null);
            }

            /**
             * Default constructor
             */
            public Node() {
                this(null, null, null);
            }

            /**
             * Clears all node data except for previous and next references
             */
            public void clear() {
                element = null;
                date = null;
                description = null;
            }
        }
}
