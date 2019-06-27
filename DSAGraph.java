/* *
 * Luke McDougall
 * 
 * General purpose graph class. Has a linked list of nodes that each have a 
 * linked list of connections
 *
 * Last updated 19/04/2019
 * */
import java.util.*;
import java.io.*;
public class DSAGraph
{
    //INNER CLASS
    private class DSAGraphVertex
    {
        //class fields
        public DSALinkedList<DSAGraphVertex> adjacency_list;
        public int label;
        public boolean visited;
        //constructor
        public DSAGraphVertex(int label)
        {
            adjacency_list = new DSALinkedList<DSAGraphVertex>();
            this.label = label;
            visited = false;
        }
        
        /* Function: addEdge
         * Import: DSAGraphVertex vertex.
         * Export: None.
         * Adds passed vertex to the current vertex adjacency list
         */
        public void addEdge(DSAGraphVertex vertex)
        {
            if(!present(vertex.label))
            {
                adjacency_list.insertLast(vertex);
            }
            else
            {
                throw new IllegalArgumentException("Error: Edge already present.");
            }
        }
    
        /* Function: toString
         * Import: None.
         * Export: String str.
         * Returns a formatted string containing the vertex label to be used in an adjacency list.
         */
        public String toString()
        {
            String str = String.format("%-7c|", label);
            for(DSAGraphVertex v : adjacency_list)
            {
                str += String.format("%c ", v.label);
            }
            return str;
        }

        /* Function: adjacencyMatrixRow
         * Import: boolean[] row.
         * Export: None.
         *
         * Fills in the passed row matrix with information about which vertices
         * are adjacent to it. Loops through the vertex_list and adds false to
         * the array if the vertex isn't adjacent otherwise adds true.
         */
        public void adjacencyMatrixRow(boolean[] row)
        {
            int index = 0;
            for(DSAGraphVertex v : vertex_list)
            {
                row[index] = present(v.label);
                index++;
            }
        }

        /* Function: present
         * Import: int label.
         * Export: boolean present
         *
         * Returns true if the passed vertex label is in this vertex adjacency
         * list. Otherwise returns false.
         */
        private boolean present(int label)
        {
            boolean present = false;
            Iterator<DSAGraphVertex> iter = adjacency_list.iterator();
            DSAGraphVertex v = null; 
            while(iter.hasNext() && !present)
            {
                v = iter.next();
                if(v.label == label)
                {
                    present = true;
                }
            }
            return present;
        }
    }
    //END INNER CLASS
    
    //class fields
    private DSALinkedList<DSAGraphVertex> vertex_list;
    private int vertex_count;
    private int edge_count;

    //Default constructor
    public DSAGraph()
    {
        vertex_list = new DSALinkedList<DSAGraphVertex>();
        vertex_count = 0;
        edge_count = 0;
    }

    //Alternate contructor. Constructs graph from a text file.
    public DSAGraph(String filename)
    {
        vertex_list = new DSALinkedList<DSAGraphVertex>();
        vertex_count = 0;
        edge_count = 0;
        FileReader rdr = null;
        BufferedReader bufrdr = null;
        try
        {
            rdr = new FileReader(filename);
            bufrdr = new BufferedReader(rdr);
            String line = bufrdr.readLine();
            while(line != null)
            {
                try
                {   
                    parseLine(line);
                }
                catch(IllegalArgumentException arg)
                {
                    System.out.println(arg.getMessage());
                }
                line = bufrdr.readLine();
            }
            bufrdr.close();
            rdr.close();
        }
        catch(IOException e)
        {
            if(rdr != null)
            {
                try
                {
                    rdr.close();
                }
                catch(IOException e2) {}
            }
            System.out.println("Error in file processing: " + e.getMessage());
        }
    }
    
    /* Function: addVertex
     * Import: int label.
     * Export: None.
     *
     * Creates a new vertex with the passed label if there are no current vertices
     * with the same label. Throws exception otherwise.
     */
    public void addVertex(int label)
    {
        if(vertexPresent(label))
        {
            throw new IllegalArgumentException(String.format("Error: Graph already contains vertex %d.", label));
        }
        else
        {
            DSAGraphVertex newVertex = new DSAGraphVertex(label);
            vertex_list.insertLast(newVertex);
            vertex_count++;
        }
    }

    /* Function addEdge 
     * Import: int vertex_label_1, int vertex_label_2.
     * Export: None.
     *
     * Adds the vertex with label vertex_label_2 to the adjacency list of
     * the vertex with label vertex_label_1 if both vertices exist. Throws
     * exception otherwise.
     */
    public void addEdge(int vertex_label_1, int vertex_label_2)
    {
        if(vertexPresent(vertex_label_1) && vertexPresent(vertex_label_2))
        {
            DSAGraphVertex v1, v2;
            v1 = findVertex(vertex_label_1);
            v2 = findVertex(vertex_label_2);
            v1.addEdge(v2);
            edge_count++;
        }
        else
        {
            throw new IllegalArgumentException("Error: One or more of the passed vertex labels are not present in graph.");
        }
    }

    /* Function: vertexPresent
     * Import: int label.
     * Export: boolean present.
     * Retruns true if a vertex with the passed label exists. Otherwise returns false.
     */
    private boolean vertexPresent(int label)
    {
        boolean present = false;
        Iterator<DSAGraphVertex> iter  = vertex_list.iterator();
        DSAGraphVertex v = null;
        while(iter.hasNext() && !present)
        {
            v = iter.next();
            if(v.label == label)
            {
                present = true;
            }
        }         
        return present;
    }

    /* Function: findVertex
     * Import: int label.
     * Export: DSAGraphVertex v.
     *
     * Returns the vertex with passed label. This should never be called if it's
     * unknown whether or not such a vertex exists.
     */
    private DSAGraphVertex findVertex(int label)
    {
        Iterator<DSAGraphVertex> iter = vertex_list.iterator();
        DSAGraphVertex v = null;
        boolean found = false;
        while(iter.hasNext() && !found)
        {
            v = iter.next();
            if(v.label == label)
            {
                found = true;
            }
        }
        return v;
    }

    /* Function: parseLine
     * Import: String line.
     * Export: None.
     *
     * Parses a line of text representing two vertices. Adds both vertices to
     * the graph and adds an edge between them. Throws exception if the line is
     * invalid.
     */
    private void parseLine(String line)
    {
        String[] vertexes = line.split(" ");
        if(vertexes.length == 2)
        {
            int vertex_1 = (int)(vertexes[0].charAt(0));
            int vertex_2 = (int)(vertexes[1].charAt(0));
            if(!vertexPresent(vertex_1))
            {
                addVertex(vertex_1);
            }
            if(!vertexPresent(vertex_2))
            {
                addVertex(vertex_2);
            }
            addEdge(vertex_1, vertex_2);
        }
        else
        {
            throw new IllegalArgumentException("Error: Invalid line");
        }
    }
    
    /* Function: displayList
     * Import: None.
     * Export: String table.
     * Returns a string representing the graph as an adjacency list.
     */
    public String displayList()
    {
        String table = "Vertex |Adjacent\n";
        for(DSAGraphVertex v : vertex_list)
        {
            table += v.toString() + "\n";
        }
        return table;
    }

    /* Function: depthFirstSearch
     * Import: None.
     * Export: DSAQueue T.
     *
     * Performs depth first search on the graph and returns a queue of vertex
     * labels.
     */
    public DSAQueue<Integer> depthFirstSearch()
    {
        DSAQueue<Integer> T = new DSAQueue<Integer>();
        DSAStack<DSAGraphVertex> S = new DSAStack<DSAGraphVertex>();
        for(DSAGraphVertex v : vertex_list)
        {
            v.visited = false;
        }
        DSAGraphVertex v = vertex_list.peekFirst();
        v.visited = true;
        S.push(v);
        while(!S.isEmpty())
        {
            
            while(hasNew(S.top().adjacency_list))
            {
                v = getNew(S.top().adjacency_list);
                T.enqueue(S.top().label);
                T.enqueue(v.label);
                v.visited = true;
                S.push(v);
            }
            S.pop();
        }
        return T;
    }
    
    /* Function: breadthFirstSearch
     * Import: None.
     * Export: DSAQueue T.
     *
     * Performs breadth first search on the graph and returns a queue of vertex
     * labels.
     */
    public DSAQueue<Integer> breadthFirstSearch()
    {
        DSAQueue<Integer> T = new DSAQueue<Integer>();
        DSAQueue<DSAGraphVertex> Q = new DSAQueue<DSAGraphVertex>();
        for(DSAGraphVertex v : vertex_list)
        {
            v.visited = false;
        }
        DSAGraphVertex v = vertex_list.peekFirst();
        v.visited = true;
        Q.enqueue(v);
        while(!Q.isEmpty())
        {
            v = Q.dequeue();
            for(DSAGraphVertex w : v.adjacency_list)
            {
                if(!w.visited)
                {
                    T.enqueue(v.label);
                    T.enqueue(w.label);
                    w.visited = true;
                    Q.enqueue(w);
                }
            }
        }
        return T;
    }

    /* Function: hasNew
     * Import: DSALinkedList<DSAGraphVertex> list.
     * Export: boolean newVertex.
     * Returns true if there is a vertex in the list that hasn't been visited.
     */
    private boolean hasNew(DSALinkedList<DSAGraphVertex> list)
    {
        boolean newVertex = true;
        for(DSAGraphVertex v : list)
        {
            newVertex = newVertex && v.visited; 
        }
        return !newVertex;
    }

    /* Function: getNew
     * Import: DSALinkedList<DSAGraphVertex> list.
     * Export: DSAGraphVertex v.
     *
     * Loops through the passed list and returns the first vertex that hasn't
     * been visited. Should only call this method if you know there is a new 
     * vertex by calling hasNew() first.
     */
    private DSAGraphVertex getNew(DSALinkedList<DSAGraphVertex> list)
    {
        DSAGraphVertex v = null;
        boolean found = false;
        Iterator<DSAGraphVertex> iter = list.iterator();
        while(iter.hasNext() && !found)
        {
            v = iter.next();
            if(!v.visited)
            {
                found = true;
            }
        }
        return v;
    }

    /* Function displayMatrix
     * Import: None.
     * Export: String adjacency_matrix_str.
     * Returns a formatted string representing the graph as an adjacency matrix.
     */
    public String displayMatrix()
    {
        boolean[][] adjacency_matrix = new boolean[vertex_count][vertex_count];
        String adjacency_matrix_str = null;
        int index = 0;
        for(DSAGraphVertex v : vertex_list)
        {
            v.adjacencyMatrixRow(adjacency_matrix[index]);
            index++;
        }
        adjacency_matrix_str = "Vertex Matrix\n";
        adjacency_matrix_str += "    ";
        String row_border = "   |";
        for(int i = 0; i < vertex_count; i++)
        {
            if(i < vertex_count - 1)
            {
                row_border += "---|";
            }
            else
            {
                row_border += "---|\n";
            }
        }
        for(DSAGraphVertex v : vertex_list)
        {
            adjacency_matrix_str += String.format("%3c|", v.label);
        }
        adjacency_matrix_str += "\n";
        adjacency_matrix_str += row_border;
        int i = 0;
        for(DSAGraphVertex v : vertex_list)
        {
            adjacency_matrix_str += String.format("%3c|", v.label);
            for(int j = 0; j < vertex_count; j++)
            {
                adjacency_matrix_str += String.format("%3d|", (adjacency_matrix[i][j]) ? 1 : 0);
            }
            adjacency_matrix_str += "\n";
            adjacency_matrix_str += row_border;
            i++;
        }
        return adjacency_matrix_str;
       /*  1 | 2 | 3 | 4 | 5
          -------------------
         1|0 | 1 | 1 | 1 | 1|
         -|--|---|---|---|--|
         2|1 | 0 | 1 | 1 | 1|
         -|--|---|---|---|--|
         3|1 | 1 | 0 | 1 | 1|
         -|--|---|---|---|--|
         4|1 | 1 | 1 | 0 | 1|
         -|--|---|---|---|--|
         5|1 | 1 | 1 | 1 | 0|
         --------------------*/
    }
}
