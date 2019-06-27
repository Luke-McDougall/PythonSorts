import Error

class ListNode:
    def __init__(self, back, front, value):
        self.back = back
        self.front = front
        self.value = value

class LinkedList:
    def __init__(self):
        self.size = 0
        self.head = None
        self.tail = None

    def __iter__(self):
        self.cur = self.head
        return self

    def __next__(self):
        if not self.cur:
            raise StopIteration
        else:
            val = self.cur.value
            self.cur = self.cur.front
            return val

    def insert_first(self, value):
        if self.head == None:
            self.head = ListNode(None, None, value)
            self.tail = self.head
        else:
            newNode = ListNode(None, None, value)
            self.head.back = newNode
            newNode.front = self.head
            self.head = newNode
        self.size += 1

    def insert_last(self, value):
        if self.tail == None:
            self.tail = ListNode(None, None, value)
            self.head = self.tail
        else:
            newNode = ListNode(None, None, value)
            self.tail.front = newNode
            newNode.back = self.tail
            self.tail = newNode
        self.size += 1

    def remove_first(self):
        if self.head == None:
            raise Error.Underflow("Linked list is empty.")
        elif self.size == 1:
            n = self.head
            self.head = None
            self.tail = None
            self.size -= 1
            return n.value
        else:
            n = self.head
            self.head = n.front
            self.head.back = None
            self.size -= 1
            return n.value

    def remove_last(self):
        if self.tail == None:
            raise Error.Underflow("Linked list is empty.")
        elif self.size == 1:
            n = self.tail
            self.tail = None
            self.head = None
            self.size -= 1
            return n.value
        else:
            n = self.tail
            self.tail = n.back
            self.tail.front = None
            self.size -= 1
            return n.value

    def peek_first(self):
        if self.head == None:
            raise Error.Underflow("Linked list is empty")
        else:
            return self.head.value

    def peek_last(self):
        if self.tail == None:
            raise Error.Underflow("Linked list is empty")
        else:
            return self.tail.value

    def is_empty(self):
        return self.size == 0


class Queue:
    def __init__(self):
        self.queue = LinkedList() 

    def enqueue(self, value):
        self.queue.insert_first(value)

    def dequeue(self):
        if self.queue.size > 0:
            return self.queue.remove_last()
        else:
            raise Error.Underflow("Queue is empty")

    def peek(self):
        if self.queue.size > 0:
            return self.queue.peek_last()
        else:
            raise Error.Underflow("Queue is empty")

    def get_size(self):
        return self.queue.size

    def is_empty(self):
        return self.queue.is_empty()


class Stack:
    def __init__(self):
        self.stack = LinkedList()

    def push(self, value):
            self.stack.insert_first(value)
            
    def pop(self):
        if self.stack.size > 0:
            value = self.stack.remove_first()
            return value
        else:
            raise Error.Underflow("Stack is empty")

    def top(self):
        if self.stack.size > 0:
            return self.stack.peek_first()
        else:
            raise Error.Underflow("Stack is empty")

    def get_size(self):
        return self.stack.size

    def is_empty(self):
        return self.stack.is_empty()

class Vertex:
    def __init__(self, label: str):
        self.label = label
        self.adjacency_list = LinkedList()
        self.visited = False

    def present(self, label: str):
        present = False
        for e in self.adjacency_list:
            if e.label == label:
                present = True
                break
        return present

    def add_edge(self, vertex):
        if not Vertex.present(self, vertex.label):
            self.adjacency_list.insert_last(vertex)
        else:
            raise Error.VertexPresent("Error: Edge already present")

    def to_string(self):
        string = "{:7}|".format(self.label)
        for v in self.adjacency_list:
            string += "{} ".format(v.label)
        return string

    def adjacency_matrix_row(self, vertex_list):
        row = []
        for v in vertex_list:
            row.append(1 if present(v.label) else 0)
        return row

class Graph:
    def __init__(self):
        self.vertex_list = LinkedList()
        self.edge_count = 0

    def vertex_present(self, label: str):
        present = False
        for v in self.vertex_list:
            if v.label == label:
                present = True
                break
        return present

    def find_vertex(self, label):
        vertex = None
        for v in self.vertex_list:
            if v.label == label:
                vertex = v
                break
        return vertex

    def add_edge(self, label_one, label_two):
        if Graph.vertex_present(self, label_one) and Graph.vertex_present(self, label_two):
            v1 = Graph.find_vertex(self, label_one)
            v2 = Graph.find_vertex(self, label_two)
            v1.add_edge(v2)
            self.edge_count += 1
        else:
            raise Error.VertexNotPresent("One or more of the passed vertices are not present")

    def dislay_list(self):
        table = "Vertex |Adjacent\n"
        for v in self.vertex_list:
            table += v.to_string() + "\n"
        return table

    def __str__(self):
        adjacency_matrix = []

    def __init__(self, filename):
        self.vertex_list = LinkedList()
        self.vertex_count = 0
        self.edge_count = 0

        vertices = None
        with open(filename, 'r') as f:
            vertices = f.read().split('\n')
            print(len(vertices))
        for v in vertices:
            edge = v.split(' ')
            if len(edge) == 2:
                print(edge)
                if not Graph.vertex_present(self, edge[0]):
                    self.vertex_list.insert_last(Vertex(edge[0]))
                if not Graph.vertex_present(self, edge[1]):
                    self.vertex_list.insert_last(Vertex(edge[1]))
                Graph.add_edge(self, edge[0], edge[1])
            else:
                if len(edge) > 1:
                    raise Error.InvalidFile("Invalid file format")

