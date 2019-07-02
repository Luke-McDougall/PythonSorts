import numpy as np
import Error

class Entry:
    def __init__(self, key: str, value):
        self.key = key
        self.value = value

class BlockNode:
    def __init__(self, parent, max_key_size: int, max_child_size: int):
        #Max key size + 1 so that there's room for a key to be added before split
        self.keys = np.empty(max_key_size + 1, dtype=Entry) 
        self.key_index = 0
        self.children = np.empty(max_child_size + 1, dtype=BlockNode)
        self.child_index = 0
        self.parent = parent
    
    def key_present(self, key: str) -> bool:
        present = False
        index = 0
        while index < self.key_index and not present:
            if self.keys[index].key == key:
                present = True
            index += 1
        return present

    def insertion_sort_key(self):
        j = self.key_index - 1
        temp = self.keys[j]
        while j > 0 and self.keys[j - 1].key > temp.key:
            self.keys[j] = self.keys[j - 1];
            j -= 1
        self.keys[j] = temp

    def insertion_sort_child(self):
        j = self.child_index - 1
        temp = self.children[j]
        while j > 0 and self.children[j - 1].get_key(0) > temp.get_key(0):
            self.children[j] = self.children[j - 1]
            j -= 1
        self.children[j] = temp

    def insert(self, key: str, value):
        self.keys[self.key_index] = Entry(key, value)
        self.key_index += 1
        if self.key_index > 1:
            self.insertion_sort_key()

    def insert_child(self, child):
        child.parent = self
        self.children[self.child_index] = child
        self.child_index += 1
        if self.child_index > 1:
            self.insertion_sort_child()

    def remove_child(self, rem_node):
        found = False
        for i in range(self.child_index):
            if found:
                self.children[i - 1] = self.children[i]

            if self.children[i] == rem_node:
                found = True

        self.children[self.child_index] = None
        self.child_index -= 1

    def split(self):
        median_idx = int(self.key_index / 2)
        parent = self.parent

        left = BlockNode(None, len(self.keys) - 1, len(self.children) - 1)
        for i in range(median_idx):
            left.insert(self.keys[i].key, self.keys[i].value)
        if self.child_index > 0:
            for j in range(median_idx + 1):
                left.insert_child(self.children[j])

        right = BlockNode(None, len(self.keys) - 1, len(self.children) - 1)
        for i in range(median_idx + 1, self.key_index):
            right.insert(self.keys[i].key, self.keys[i].value)
        if self.child_index > 0:
            for j in range(median_idx + 1, self.child_index):
                right.insert_child(self.children[j])
               
        new_root = None
        if parent == None:
            new_root = BlockNode(None, len(self.keys) - 1, len(self.children) - 1)
            new_root.insert(self.keys[median_idx].key, self.keys[median_idx].value)
            new_root.insert_child(left)
            new_root.insert_child(right)
        else:
            parent.insert(self.keys[median_idx].key, self.keys[median_idx].value)
            parent.remove_child(self)
            parent.insert_child(left)
            parent.insert_child(right)
            if parent.key_index == (len(self.keys) - 1):
                new_root = parent.split()

        return new_root

    def get_value(self, index: int):
        return self.keys[index].value

    def get_child(self, index: int):
        return self.children[index]

    def get_key(self, index: int):
        return self.keys[index].key

class BTree:
    def __init__(self, order: int):
        self.max_key_size = 2 * order
        self.max_child_size = self.max_key_size + 1
        self.root = BlockNode(None, self.max_key_size, self.max_child_size)
        self.height = 0
        self.size = 0

    def __str__(self):
        return self.recursive_string(self.root, 1)

    def recursive_string(self, node, level):
        string = f"Level {level}\n"
        for i in range(0, node.key_index):
            string += node.keys[i].key + " "
        string += '\n'
        for j in range(0, node.child_index):
            string += self.recursive_string(node.children[j], level + 1)
        return string

    def size(self) -> int:
        return self.size

    def height(self) -> int:
        return self.height
       
    def insert_recurse(self, key: str, value, node):
        if node.key_present(key):
            raise Error.IllegalArgument(f'Keys must be unique: {key} already present')

        if node.child_index == 0:
            if node.key_index < self.max_key_size:
                node.insert(key, value)
            else:
                node.insert(key, value)
                temp = node.split()
                if temp:
                    self.root = temp
                    self.height += 1
            self.size += 1
        else:
            num_keys = node.key_index
            cur_idx = 0
            found = False

            while not found:
                temp = node.get_key(cur_idx)

                if cur_idx == 0 and key < temp:
                    self.insert_recurse(key, value, node.get_child(cur_idx))
                    found = True
                elif cur_idx == (num_keys - 1) and key > temp:
                    self.insert_recurse(key, value, node.get_child(num_keys))
                    found = True
                elif cur_idx > 0:
                    Prev = node.get_key(cur_idx - 1)
                    Next = node.get_key(cur_idx)
                    if (key > Prev) and (key < Next):
                        self.insert_recurse(key, value, node.get_child(cur_idx))
                        found = True
                cur_idx += 1

    def insert(self, key: str, value):
        if self.root.child_index == 0:
            if self.root.key_index < self.max_key_size:
                self.root.insert(key, value)
            else:
                self.root.insert(key, value)
                self.root = self.root.split()
                self.height += 1
            self.size += 1
        else:
            self.insert_recurse(key, value, self.root)

    def find_recurse(self, key: str, node):
        value = None
        found = False

        if node.child_index == 0:
            cur_idx = 0
            while not found:
                if key == node.get_key(cur_idx):
                    found = True
                    value = node.get_value(cur_idx)
                else:
                    cur_idx += 1
                    if cur_idx == node.key_index:
                        raise Error.IllegalArgument(f"Element with key {key} does not exist")
        else:
            num_keys = node.key_index
            cur_idx = 0

            while not found:
                temp = node.get_key(cur_idx)
                if key == temp:
                    value = node.get_value(cur_idx)
                    found = True
                elif cur_idx == 0 and key < temp:
                    value = self.find_recurse(key, node.get_child(cur_idx))
                    found = True
                elif cur_idx == (num_keys - 1) and key > temp:
                    value = self.find_recurse(key, node.get_child(num_keys))
                    found = True
                elif cur_idx < (num_keys - 1):
                    Next = node.get_key(cur_idx + 1)
                    if key > temp and key < Next:
                        value = self.find_recurse(key, node.get_child(cur_idx + 1))
                        found = True
                cur_idx += 1
        return value

    def find(self, key: str):
        value = None
        if self.root: 
            if self.root.child_index > 0:
                value = self.find_recurse(key, self.root)
            else:
                found = False
                currIdx = 0
                while not found:
                    if key == root.get_key(cur_idx):
                        found = True
                        value = root.get_value(cur_idx)
                    else:
                        cur_idx += 1
                        if currIdx == self.root.key_index:
                            raise Error.IllegalArgument(f"Element with key {key} does not exist")
        else:
            raise Error.IllegalArgument("Error: Tree is empty.")
        return value

