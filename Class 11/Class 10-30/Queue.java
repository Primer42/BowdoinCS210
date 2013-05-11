// An implementation of queues, based on vectors.
// (c) 1996 duane a. bailey

import java.util.*;

public class Queue {
    protected Vector data;
    
    // post: constructs an empty queue.
    public Queue() {
        data = new Vector();
    }

    public Queue(int size)
    // post: constructs an empty queue of appropriate size
    {
        data = new Vector(size);
    }

    public void add(Object value)
    // post: the value is added to the tail of the structure
    {
        data.addElement(value);
    }

    public void enqueue(Object value)
    // post: the value is added to the tail of the structure
    {
        add(value);
    }
    

    public Object remove()
    // pre: the queue is not empty
    // post: the element at the head of the queue is removed and returned
    {
        Object result = data.elementAt(0);
        data.removeElementAt(0);
        return result;
    }
    public Object dequeue()
    // pre: the queue is not empty
    // post: the element at the head of the queue is removed and returned
    {
        return remove();
    }

    public Object peek()
    // pre: the queue is not empty
    // post: the element at the head of the queue is returned
    {
        return data.elementAt(0);
    }

    public int size()
    // post: returns the number of elements in the queue.
    {
        return data.size();
    }

    public void clear()
    // post: removes all elements from the queue.
    {
        data.clear();
    }
    
    public boolean isEmpty()
    // post: returns true iff the queue is empty
    {
        return data.isEmpty();
    }

    public String toString()
    // post: returns string representation of queue
    {
        StringBuffer s = new StringBuffer();
        int i;

        s.append("<QueueArray:");
        for (i = 0; i < data.size(); i++)
        {
            s.append(" "+data.elementAt(i));
        }
        s.append(">");
        return s.toString();
    }
}
