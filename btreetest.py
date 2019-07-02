import BTree as bt
import numpy as np

tree = bt.BTree(4)
"""alph = 'abcdefghijklmnopqrstuvwxyz'

for i, e in enumerate(alph):
    tree.insert(e, i)
    print(tree)"""

keys = np.arange(1_000_000)
np.random.shuffle(keys)
for i in range(1_000_000):
    if i % 1000 == 0:
        print(f"insert, {i}/1 000 000")
    tree.insert(str(keys[i]), keys[i])


succ = True
for i in range(1_000_000):
    if i % 1000 == 0:
        print(f"find, {i}/1 000 000")
    a = tree.find(str(keys[i]))
    b = keys[i]
    succ = succ and a == b

print("success" if succ else "fail")
print(f"size = {tree.size}, height = {tree.height}")
