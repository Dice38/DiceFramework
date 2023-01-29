package datalists;

import datacomponents.DiceAbstractCollection;
import datacomponents.Nullable;
import datacomponents.ReadOnly;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

public class DiceLinkedList<T> extends DiceAbstractCollection<T> {
    /*
    ATTRIBUTE
     */
    private ListNode<T> head;
    private ListNode<T> tail;


    /*
    CONSTRUCTOR
     */
    DiceLinkedList(boolean noNullElements){
    }
    DiceLinkedList(){this(false);}



    /*
        METHODS
    */
    @Override
    public boolean add(T value) {
        var iter = this.diceIterator();
        iter.add(value);
        return true;
    }

    @Override
    public void clear() {
        this.head = null;
        this.head = null;
        this.modCount.incrementAndGet();
    }

    @Override
    public boolean remove(Object obj) {
        var iter = this.diceIterator();
        int tempMod = 0;

        if(obj == null){
            while(iter.hasNext()){
                if(iter.next() == null){
                    iter.remove();
                    tempMod++;
                }
            }
        }else{
            while(iter.hasNext()){
                if(iter.next().equals(obj)){
                    iter.remove();
                    tempMod++;
                }
            }
        }
        return tempMod != 0;
    }


    public void setReadOnly(){this.readOnly = ReadOnly.READ_ONLY;}

    @Override
    public Iterator<T> iterator() {
        return new Iter(this.head);
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public DiceIterator<T> diceIterator() {
        return new Iter(this.head);
    }

    @Override
    public int size() {
        return this.size;
    }


    /*
    DICE ITERATOR CLASS
     */
    private class Iter implements DiceIterator<T> {
        /*
        ATTRIBUTES
         */
        private AtomicInteger expectedModCount;
        private ListNode<T> prevNode, nextNode;
        private int cursorPosition;
        private LastOperation lastOperation = LastOperation.MODIFIED;

        /*
        CONSTRUCTOR
         */
        Iter(ListNode<T> startNode) {
            if(startNode == null){
                this.prevNode = null;
                this.nextNode = null;
            }else{
                this.nextNode = startNode;
                this.prevNode = startNode.getPrevNode();
            }

            this.expectedModCount = DiceLinkedList.this.modCount;
            this.cursorPosition = -1;
        }

        /*
        METHODS
         */
        public void add(T value){
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if(DiceLinkedList.this.isReadOnly()) throw new UnsupportedOperationException("LinkedList is ReadOnly");
            if(value == null && !DiceLinkedList.this.allowsNullElements()) throw new IllegalArgumentException("No Null Elements allowed");
            if(this.lastOperation == LastOperation.MODIFIED) throw new IllegalStateException("Last returned element has already been modified");
            /*
            Testing of different cases of insertion position of the new Element.
            1. In the first case the element is inserted in the middle of the list. Head and Tail remain unchanged.
            2. In the second case the element is inserted at the end of the list. The new element becomes the new tail.
            3. In the third case the element is inserted at the beginning of the list. The new element becomes the new head.
            4. In the last case the element is the first element of the list. The new element becomes the head and tail.
             */
            if(this.hasNext() && this.hasPrevious()){
                this.prevNode.linkNext(new ListNode<T>(value, this.prevNode, this.nextNode));
                this.prevNode = this.prevNode.getNextNode();
                this.nextNode.linkPrevious(this.prevNode);

            }else if(!this.hasNext() && this.hasPrevious()){
                this.prevNode.linkNext(new ListNode<T>(value,this.prevNode, null));
                this.prevNode = this.prevNode.getNextNode();
                DiceLinkedList.this.tail = this.prevNode;

            }else if(this.hasNext() && !this.hasPrevious()){
                this.nextNode.linkPrevious(new ListNode<T>(value, null, this.nextNode));
                this.prevNode = this.nextNode.getPrevNode();
                DiceLinkedList.this.head = this.prevNode;

            }else{
                this.nextNode = new ListNode<T>(value, null, null);
                DiceLinkedList.this.head = this.nextNode;
                DiceLinkedList.this.tail = this.nextNode;
            }

            DiceLinkedList.this.size++;
            this.expectedModCount.incrementAndGet();
            DiceLinkedList.this.modCount.incrementAndGet();
        }
        public boolean hasNext() {
            return this.nextNode != null;
        }

        public boolean hasPrevious() {
            return this.prevNode != null;
        }
        public T next(){
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if(!this.hasNext())throw new NoSuchElementException("LinkedList has no next Element");

            this.moveForward();
            this.lastOperation = LastOperation.NEXT;
            return this.prevNode.getValue();
        }

        public T previous(){
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if(!this.hasPrevious()) throw new NoSuchElementException("LinkedList has no previous Element");

            this.moveBackward();
            this.lastOperation = LastOperation.PREVIOUS;
            return this.nextNode.getValue();
        }

        public void remove() {
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if (DiceLinkedList.this.isReadOnly()) throw new UnsupportedOperationException("LinkedList is ReadOnly");
            if (this.lastOperation == LastOperation.MODIFIED)
                throw new IllegalStateException("Last Element has already been modified");
            /*
            Testing if the removal of the element changes the lists head or tail.
            LAST OPERATION NEXT
                1. Removal in the middle of the list. Head and tail are unaffected.
                2. Removal of the first element of the list. The next element becomes the new head.
                3. Removal of the last element of the list. The previous-previous element becomes the new tail.
                4. Removal of the only element of the list. The list is now empty.

            Last Operation PREVIOUS
                1. Removal in the middle of the list. The head and tail are unaffected.
                2. Removal of the last element in the list. The prev element becomes the new tail.
                3. Removal of the first element in the list. The next element becomes the new head.
                4. Removal of the only element of the list. The list is now empty.
             */

            if (this.lastOperation == LastOperation.NEXT) {

                if (this.hasNext() && this.hasPreviousPrevious()) {
                    this.prevNode = this.prevNode.getPrevNode();
                    this.prevNode.linkNext(this.nextNode);
                    this.nextNode.linkPrevious(this.prevNode);


                } else if (this.hasNext() && !this.hasPreviousPrevious()) {
                    this.nextNode.linkPrevious(null);
                    this.prevNode = null;
                    DiceLinkedList.this.head = this.nextNode;

                }else if (!this.hasNext() && this.hasPreviousPrevious()){
                    this.prevNode.getPrevNode().linkNext(null);
                    this.prevNode = this.prevNode.getPrevNode();
                    DiceLinkedList.this.tail = this.prevNode;

                }else{
                    DiceLinkedList.this.head = null;
                    DiceLinkedList.this.tail = null;
                    this.prevNode = null;
                }
            }else if(this.lastOperation == LastOperation.PREVIOUS){

                if(this.hasPrevious() && hasNextNext()){
                    this.nextNode = this.nextNode.getNextNode();
                    this.prevNode.linkNext(this.nextNode);
                    this.nextNode.linkPrevious(this.prevNode);

                }else if(this.hasPrevious() && !this.hasNextNext()){
                    this.nextNode = null;
                    this.prevNode.linkNext(null);
                    DiceLinkedList.this.tail = this.prevNode;

                }else if(!this.hasPrevious() && this.hasNextNext()){
                    this.nextNode = this.nextNode.getNextNode();
                    this.nextNode.linkPrevious(null);
                    DiceLinkedList.this.head = this.nextNode;

                }else{
                    DiceLinkedList.this.head = null;
                    DiceLinkedList.this.tail = null;
                    this.nextNode = null;
                }
            }

            this.lastOperation = LastOperation.MODIFIED;
            DiceLinkedList.this.size--;
            this.expectedModCount.incrementAndGet();
            DiceLinkedList.this.modCount.incrementAndGet();

        }
        public void modify(UnaryOperator<T> operator){
            this.checkIllegalModification();
            if(DiceLinkedList.this.isReadOnly()) throw new IllegalStateException("Linked List is ReadOnly");

            T temp;
            if(this.lastOperation == LastOperation.NEXT){
                temp = operator.apply(this.prevNode.getValue());

            }else if(this.lastOperation == LastOperation.PREVIOUS){
                temp = operator.apply(this.prevNode.getValue());
            }else{temp = null;}

            if(temp == null && !DiceLinkedList.this.allowsNullElements())throw new IllegalArgumentException("Passed Operator returned null reference in a LinkedList that doesn't allow null elements");
            this.prevNode.setValue(temp);

            this.expectedModCount.incrementAndGet();
            DiceLinkedList.this.modCount.incrementAndGet();
        }

        public void goToFirst(){
            this.checkIllegalModification();
            this.prevNode = null;
            this.nextNode = DiceLinkedList.this.head;
        }

        public void goToLast(){
            this.checkIllegalModification();
            this.prevNode = DiceLinkedList.this.tail;
            this.nextNode = null;
        }
        /*
        INTERNAL METHODS
         */

        private boolean hasNextNext(){
            return this.nextNode.getNextNode() != null;
        }

        private boolean hasPreviousPrevious(){
            return this .prevNode.getPrevNode() != null;
        }

        private boolean checkIllegalModification(){
            return !Objects.equals(this.expectedModCount, DiceLinkedList.this.modCount);
        }

        /*
        CURSOR METHODS
         */
            void moveForward() {
                this.nextNode = this.nextNode.getNextNode();
                this.prevNode = this.prevNode.getNextNode();
                this.cursorPosition++;
            }

            void moveBackward() {
                this.nextNode = this.nextNode.getPrevNode();
                this.prevNode = this.prevNode.getPrevNode();
                this.cursorPosition--;
            }
    }
    /*
    NODE CLASS
     */
    private static class ListNode<T>{
        private T value;
        private ListNode<T> nextNode, prevNode;
        private NodeType nodeType = NodeType.HEAD;

        /*
        CONSTRUCTOR
         */
        ListNode(T value, ListNode<T> prevNode, ListNode<T> nextNode){
            this.value = value;
            this.linkNext(nextNode);
            this.linkPrevious(prevNode);
        }

        /*
        METHODS
         */
        void setValue(T value) {
            this.value = value;
        }

        T getValue() {
            return this.value;
        }

        void linkPrevious(ListNode<T> prevNode) {
            this.prevNode = prevNode;
            if (prevNode == null){
                this.nodeType = NodeType.HEAD;
            }else{
                this.nodeType = NodeType.NORMAL;
            }
        }

        void linkNext(ListNode<T> nextNode) {
            this.nextNode = nextNode;
            if (nextNode == null) {
                this.nodeType = NodeType.TAIL;
            }else{
                this.nodeType = NodeType.NORMAL;
            }
        }

        ListNode<T> getPrevNode() {
            return this.prevNode;
        }

        ListNode<T> getNextNode() {
            return this.nextNode;
        }


    }

    private enum NodeType{HEAD,TAIL,NORMAL}
    private enum LastOperation{NEXT,PREVIOUS,MODIFIED}
}
