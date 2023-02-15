# DiceFramework
All sorts of support libraries for data handling.
Mostly an educational effort to better understand different datastructures and how to extend the Collections Framework.

## Datastructures
### 1. List Structures
All Datastructures that have list like behaviour. At present an ArrayList, LinkedList and SkipList is implemented.
#### 1.1 LinkedList and ArrayList
The interface contract for LinkedList and ArrayList is mostly the same, as both are unordered. 
##### AbstractClass - DiceAbstractCollection
An Abstract Class that contains the base functionality for a doubly LinkedList and an ArrayList implementation. Still in the process of figuring out the best mix of abstract classes
and interfaces.
##### DiceIterator
Implements the ListIterator Interface and expands on the functionality. A design choice is that List modification happens by using DiceIterator 
for all the methods the LinkedList and ArrayList implements. This also allows for fail fast concurrent modification detection.
##### General Functionality of LinkedList and ArrayList
- Can be set to ReadOnly which forbids all operations that modify the structure or content. By default all copyOf() methods return an unmodifiable copy.
- Offer a constructor parameter to make the Lists **nonnullable**. If so it does not allow the adding of null elements. I am still unsure whether
a NoNullObject wrapper for all Datastructures would be a better idea.
- This project uses Java Modules (Java 9) and it intends to offer different datastructures through a service like structure (tho yet to be implemented).
#### 1.2 SkipList
One Implementation of a SkipList that offers the usual time complexity. Since it is a sorted Structure it doesn't allow for random modification. DiceIterator can therefore not be used.

### Project Meta
- In the nature of keeping a complete history I will opt to merge topic branches instead of rebasing them.
- Since this is an educational effort, all implementations are largely my own produce with as few references as possible. Therefore they might not always be optimal. 



## FUTURE
I am especially looking forward to construct graph like datastructures and play around with their functionality. For educational purposes I also want to implement as 
synchronizing wrapper class that allows for save multithreaded interaction with a specific datastructure by using Semaphores.


Thx for reading all this, tho I doubt anyone ever will ...;-)
