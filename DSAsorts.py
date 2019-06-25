#
# Data Structures and Algorithms COMP1002
#
# Python file to hold all sorting methods
#

def bubbleSort(A):
    for i in range(0, len(A) - 1):
        for j in range(0, len(A) - 1 - i):
            if A[j] > A[j + 1]:
                temp = A[j]
                A[j] = A[j + 1]
                A[j + 1] = temp
                
def insertionSort(A):
    for i in range(1, len(A)):
        temp = A[i]
        index = i
        while A[index - 1] > temp and index > 0:
            A[index] = A[index - 1]
            index = index - 1

        A[index] = temp

def selectionSort(A):
    for i in range(0, len(A)):
        minval = A[i]
        minIdx = i
        for j in range(i, len(A)):
            if A[j] < minval:
                minval = A[j]
                minIdx = j


        temp = A[i]
        A[i] = minval
        A[minIdx] = temp

def mergeSort(A):
    if(len(A) > 1):
        midIdx = int(len(A) / 2)
        mergeSort(A[:midIdx])
        mergeSort(A[midIdx:])
        merge(A[:midIdx], A[midIdx:])

def merge(A, B):
    temp = []
    lenA = len(A)
    lenB = len(B)
    idxA = idxB = 0

    while idxA < lenA and idxB < lenB:
        if A[idxA] <= B[idxB]:
            temp.append(A[idxA])
            idxA = idxA + 1
        else:
            temp.append(B[idxB])
            idxB = idxB + 1

    while idxA < lenA:
        temp.append(A[idxA])
        idxA = idxA + 1

    while idxB < lenB:
        temp.append(B[idxB])
        idxB = idxB + 1

    idx = 0
    for e in temp:
        if idx < lenA:
            A[idx] = e
        else:
            B[idx - lenA] = e
        idx = idx + 1

def quickSort(A):
    if len(A) > 1:
        pivotIdx = int(len(A) / 2)
        pivot = A[pivotIdx]
        A[pivotIdx] = A[-1]
        A[-1] = pivot

        curIdx = 0
        for i in range(0, len(A) - 1):
            if A[i] < pivot:
                temp = A[i]
                A[i] = A[curIdx]
                A[curIdx] = temp
                curIdx = curIdx + 1

        A[-1] = A[curIdx]
        A[curIdx] = pivot
        quickSort(A[:curIdx])
        quickSort(A[curIdx + 1:])


