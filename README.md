# DiceFramework
All sorts of support libraries for data handling.
An educational effort as well as my attempt to improve certain things of the java.util.Collection framework.

## BASIC STRUCTURE:
### AbstractClass - DiceAbstractCollection ->
The abstract Collection all the datastructures implemented in DiceFrameWork extend. It itself implements the Collection-Interface to allow all Datastructures to work with
the java.util Collections. So far the Datastructures extend DiceAbstractCollection directly, since I have not yet identifed the need for downstream Abstract structures
the likes of DiceAbstractList or DiceAbstractTree etc.

### DiceIterator
Implements the Iterator Interface and expands on both the functionaltiy of the java.util.Iterator as well as javas ListIterator. A design choice is that datastructure
modification happens by using DiceIterator for all the methods the datastructures implements.

### General Functionality
- All datastructures can be set to ReadOnly which forbades all operations that modify the structure or content of the datastructure.
- All datastructures offer a constructor parameter to make the datastructure nonnullable. If so it does not allow the adding of null elements. I am still unsure whether
a NoNullObject wrapper for all Datastructures would be a better idea.
- The datastructures save information about whether they are sorted or not (If their structure allows for that differentation). If they are in a sorted states
operations such as removal of specific elements, retrieving elements and so on use a binarysearch instead of lineal traversal.

### Project Meta
-In the nature of keeping a complete history I will opt to merge topic branches instead of rebasing them.

## CURRENTLY IMPLEMENTED (on development branch):
This is very much a work in progress. The project state on the development branch is not completely tested. A complete test suite for DiceAbstractCollection has yet to 
be written.

- DiceArrayList
- DiceLinkedList

## FUTURE
I am especially looking forward to construct graph like datastructures and play around with their functionality. For educational purposes I also want to implement as 
synchronizing wrapper class that allows for save multithreaded interaction with a specific datastructure by using Semaphors. For tree structures I intend to 
have the datastructure keep track of the number of modification and element retrieval operation and use that data to switch between an AVL balancing tree and a 
black red balancing tree.

Thx for reading all this, tho I doubt anyone ever will ...;-)
