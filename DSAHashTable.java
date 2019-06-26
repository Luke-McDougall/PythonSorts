/* *
 * Luke McDougall
 *
 * Generic hash table implementation
 *
 * Last updated 13/05/2019
 * */
import java.util.*;
import java.lang.reflect.Array;

public class DSAHashTable<T>
{
    
    /*INNER CLASS*/
    private class DSAHashEntry
    {
        public boolean empty;
        public boolean used;
        public String key;
        public T value;

        public DSAHashEntry() 
        {
            init();
        }

        public void init()
        {
            empty = true;
            used = false;
            key = "";
            value = null;
        }
    }
    /*END INNER CLASS*/
    
    private DSAHashEntry[] m_hashTable;
    private int capacity;
    private int size;
    private int maxStep;   
 
    @SuppressWarnings("unchecked")
    public DSAHashTable(int maxSize)
    {
        if(maxSize < 1)
        {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        //Returns the next lowest prime greater than max size unless maxSize is prime
        capacity = nextPrime(maxSize);
        size = 0;
        //Type erasure was a bad idea
        m_hashTable = (DSAHashEntry[])Array.newInstance(DSAHashEntry.class, capacity);
        //Initialize m_hashTable with default entries
        for(int i = 0; i < capacity; i++)
        {
            m_hashTable[i] = new DSAHashEntry();
        }

        setMaxStep();
    }
    
    //Calculate max step for double hashing
    private void setMaxStep()
    {
        maxStep = nextPrime(capacity / 2);
    }
    
    public int getSize()
    {
        return size;
    }

    public int getCapacity()
    {
        return capacity;
    }

    private double calcLoad()
    {
        return (double)size / (double)capacity;
    }

    /* Function resize
     * Import: None
     * Export: None
     * Called when load exceeds min or max threshhold and resizes accordingly
     */
    private void resize()
    { 
        //Increase/decrease capacity in order to make load = 0.5
        System.out.printf("Current load is %f. ", calcLoad());
        int oldCapacity = capacity;
        capacity = nextPrime((int)((double)size / 0.5));
        System.out.printf("New capacity is %d\n", capacity);
        setMaxStep();   //Calc new step from new capacity
        size = 0;
        DSAHashEntry[] oldTable = m_hashTable; 
        m_hashTable = (DSAHashEntry[])Array.newInstance(DSAHashEntry.class, capacity);
        for(int i = 0; i < capacity; i++)
        {
            m_hashTable[i] = new DSAHashEntry();    //Init new table
        }
        
        for(int j = 0; j < oldCapacity; j++)
        {
            if(!oldTable[j].empty)
            {
                put(oldTable[j].key, oldTable[j].value);    //Insert all entries to the new table
            }
        }
        System.out.printf("Resize called. new capacity: %d\n", capacity);
        
    }
    
    /* Function nextPrime
     * Import: int start
     * Export: int prime
     * Returns the next prime number >= to start value
     */
    private int nextPrime(int start)
    {
        int prime = (start % 2 == 0) ? start + 1 : start;   //Even numbers aren't prime
        boolean isPrime = checkPrime(prime);    //Check if already prime

        while(!isPrime)
        {
            prime += 2;
            isPrime = checkPrime(prime);
        }
        return prime;
    }

    /* Function: checkPrime
     * Import: int prime
     * Export boolean isPrime
     * Returns true if passed int is prime
     */
    private boolean checkPrime(int prime)
    {
        int i = 3;
        boolean isPrime = true;
        while(i * i <= prime && isPrime)
        {
            if(prime % i == 0)
            {
                isPrime = false;
            }
            i += 2;
        }
        return isPrime;
    }

    /* Function: hash
     * Import: String key.
     * Export int hashIdx.
     * Generates a unique integer index from passed key.
     */
    private int hash(String key)
    {
        int a = 63689;  //Weird numbers
        int b = 378551;
        int hashIdx = 0;

        for(int i = 0; i < key.length(); i++)
        {
            hashIdx = (hashIdx * a) + key.charAt(i);
            a *= b;
        }

        hashIdx = hashIdx < 0 ? Integer.MAX_VALUE + hashIdx : hashIdx; //Apparently java has no unsigned int
        return hashIdx % capacity;
    }

    /* Function: probeHash
     * Import: String key
     * Export: int hashIdx
     * Generates a second index for double hashing
     */
    private int probeHash(String key)
    {
        int hashIdx = key.charAt(0);
        for(int i = 1; i < key.length(); i++)
        {
            hashIdx = (31 * hashIdx) + key.charAt(i);
        }
        //I'm incredibly smart.
        return hashIdx = (hashIdx = (hashIdx < 0) ? (Integer.MAX_VALUE + hashIdx) % maxStep : hashIdx % maxStep) == 0 ? 1 : hashIdx;
    }

    /* Function: put
     * Import: String key, T value
     * Export: None
     * Inserts the value and key into the hashtable 
     */
    public void put(String key, T value)
    {
        if(calcLoad() >= 0.6)
        {
            resize();
        }
        int index = hash(key);
        //System.out.printf("%s has index %d\n", key, index);
        int step = probeHash(key);
        if(m_hashTable[index].empty)
        {
            m_hashTable[index].key = key;
            m_hashTable[index].value = value;
            m_hashTable[index].empty = false;
            m_hashTable[index].used = true;
            size++;
        }
        else
        {
            insertLinearProbing(index, key, value, step);
        }
    }

    //Probing for insert
    private void insertLinearProbing(int index, String key, T value, int step)
    {
        do 
        {
            index = (index + step) % capacity;
        }while(!m_hashTable[index].empty);

        m_hashTable[index].key = key;
        m_hashTable[index].value = value;
        m_hashTable[index].empty = false;
        m_hashTable[index].used = true;
        size++;
    }
    
    /* Function: get
     * Import: String key
     * Export: T value
     * Returns value at index of key if there is a match. Throws exception otherwise
     */
    public T get(String key)
    {
        T value = null;
        int index = hash(key);
        int step = probeHash(key);
        if(m_hashTable[index].key.equals(key))
        {
            value = m_hashTable[index].value;
        }
        else
        {
            value = findLinearProbing(index, key, step);        
        }
        
        return value;
    }
    
    //Linear probing for get
    private T findLinearProbing(int index, String key, int step)
    {
        do 
        {
            if(!m_hashTable[index].used)
            {
                throw new NoSuchElementException(String.format("No element with key %s exists in the map.\n", key));
            }            
            index = (index + step) % capacity;
        }while(!m_hashTable[index].key.equals(key));

        return m_hashTable[index].value;
    }
    
    /* Function: remove
     * Import: String key
     * Export: None
     * Removes value and key from corresponding index if a match occurs. Throws exception otherwise
     */
    public void remove(String key)
    {
        if(calcLoad() <= 0.4)
        {
            resize();
        }
        int index = hash(key);
        int step = probeHash(key);
        if(m_hashTable[index].key.equals(key))
        {
            m_hashTable[index].key = "";
            m_hashTable[index].value = null;
            m_hashTable[index].empty = true;
            size--;
        }
        else
        {
            removeLinearProbing(index, key, step);
        }
    }
    
    //Linear probing for remove
    private void removeLinearProbing(int index, String key, int step)
    {
        do
        {
            if(!m_hashTable[index].used)
            {
                throw new NoSuchElementException(String.format("No element with key %s exists in the map.\n", key));
            }
            index = (index + step) % capacity;
        }while(!m_hashTable[index].key.equals(key));

        m_hashTable[index].key = "";
        m_hashTable[index].value = null;
        m_hashTable[index].empty = true;
        size--;
    }
    
    /* Function: containsKey
     * Import: String key
     * Export: boolean present
     * Returns true if passed key exists in table.
     */
    public boolean containsKey(String key)
    {
        int index = hash(key);
        int step = probeHash(key);
        boolean present = false;
        if(m_hashTable[index].used)
        {
            if(m_hashTable[index].key.equals(key))
            {
                present = true;
            }   
            else
            {
                present = containsLinearProbing(index, key, step);
            }
        }
        return present;
    } 

    //Linear probing for containsKey
    private boolean containsLinearProbing(int index, String key, int step)
    {
        boolean present = false, loopVar = true;
        
        do
        {
            if(!m_hashTable[index].used)
            {
                loopVar = false;
            }
            else if(m_hashTable[index].key.equals(key))
            {
                loopVar = false;
                present = true;
            }
            index = (index + step) % capacity;
        }while(loopVar);
        return present;
    }
}
