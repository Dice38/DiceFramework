package datalists;

import datacomponents.DiceAbstractCollection;
import datacomponents.Nullable;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
        if(noNullElements) this.nullable = Nullable.NONNULLABLE;
    }
    DiceLinkedList(){}

    /*
    METHODS
     */

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
            this.nextNode = startNode;
            this.prevNode = startNode.getPrevNode();
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

            if(this.hasNext() && this.hasPrevious()){
                this.prevNode.linkNext(new ListNode<T>(value, this.prevNode, this.nextNode));
                this.prevNode = this.prevNode.getNextNode();
                this.nextNode.linkPrevious(this.prevNode);

            }else if(!this.hasNext() && this.hasPrevious()){
                this.prevNode.linkNext(new ListNode<T>(value,this.prevNode, null));
                this.prevNode = this.prevNode.getNextNode();

            }else if(this.hasNext() && !this.hasPrevious()){
                this.nextNode.linkPrevious(new ListNode<T>(value, null, this.nextNode));
                this.prevNode = this.nextNode.getPrevNode();

            }else{
                this.nextNode = new ListNode<T>(value, null, null);
                DiceLinkedList.this.head = this.nextNode;
            }

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
            return this.prevNode.getValue();
        }

        public T previous(){
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if(!this.hasPrevious()) throw new NoSuchElementException("LinkedList has no previous Element");

            this.moveBackward();
            return this.nextNode.getValue();
        }

        public void remove() {
            if(this.checkIllegalModification()) throw new ConcurrentModificationException("LinkedList has been illegally modified");
            if (DiceLinkedList.this.isReadOnly()) throw new UnsupportedOperationException("LinkedList is ReadOnly");
            if (this.lastOperation == LastOperation.MODIFIED)
                throw new IllegalStateException("Last Element has already been modified");

            if (this.lastOperation == LastOperation.NEXT) {

                if (this.hasNext() && this.hasPreviousPrevious()) {
                    prevNode.getPrevNode().linkNext(this.nextNode);
                    this.nextNode.linkPrevious(this.prevNode.getPrevNode());
                    this.prevNode = this.prevNode.getPrevNode();

                } else if (this.hasNext() && !this.hasPreviousPrevious()) {
                    this.nextNode.linkPrevious(null);
                    this.prevNode = null;

                }else if (!this.hasNext() && this.hasPreviousPrevious()){
                    this.prevNode.getPrevNode().linkNext(null);
                    this.prevNode = this.prevNode.getPrevNode();

                }else{
                    DiceLinkedList.this.head = null;
                    this.prevNode = null;
                }
            }else if(this.lastOperation == LastOperation.PREVIOUS){

                if(this.hasPrevious() && !hasNextNext()){
                    DiceLinkedList.this.head = null;
                    this.nextNode = null;

                }else if(this.hasPrevious()){
                    this.nextNode.getNextNode().linkPrevious(null);
                    this.nextNode = this.nextNode.getNextNode();

                }else if(this.nextNode.getNextNode() == null){
                    this.prevNode.linkNext(null);
                    this.nextNode = null;

                }else{
                    this.prevNode.linkNext(this.nextNode.getNextNode());
                    this.nextNode.linkPrevious(this.prevNode);
                    this.nextNode = this.nextNode.getNextNode();
                }
            }
            this.expectedModCount.incrementAndGet();
            DiceLinkedList.this.modCount.incrementAndGet();

        }

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
