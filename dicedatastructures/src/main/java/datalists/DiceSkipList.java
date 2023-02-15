package datalists;

import java.util.*;

public class DiceSkipList<T  extends Comparable<? super T>> {

    //Attributes
    public final static float layerProbability = 0.5f;
    Comparator<T> comparator;
    SkipNode head = new SkipNode(null);
    //Constructors
    public DiceSkipList(){
        this.comparator = Comparator.naturalOrder();
    }
    public DiceSkipList(Comparator<T> comparator){this.comparator = comparator;}

    //METHODS

    public boolean add(T value) {
        if(value == null) return false;

        Stack<SkipNode> path= getPath(value);
        var random = new Random();
        var insertNode = new SkipNode(value);
        link(path.pop(), insertNode);

        while(random.nextFloat() < 0.5){
            if(path.isEmpty()){
                head = head.addLayer();
                link(head, insertNode.addLayer());

            }
            else{
                link(path.pop(), insertNode.addLayer());
            }
        }

        return true;
    }

    public boolean addAll(Collection<? extends T> collection){
        boolean bFlag = false;
        for(T value: collection){
            bFlag |= this.add(value);
        }
        return bFlag;
    }

    public boolean contains(T value){
        if(value == null) return false;

        return value.equals(getPath(value).pop().getValue());

    }

    public boolean remove(T value){
        if(value == null) return false;

        var path = getPath(value);
        if(!path.peek().getValue().equals(value)) return false;

        unlink(path.pop());
        return true;

    }
    private Stack<SkipNode> getPath(T key){
        var current = this.head;
        var resultStack = new Stack<SkipNode>();

        while(true){
            if(current.getNext() != null && comparator.compare(current.getNext().getValue(), key) <= 0) current = current.getNext();

            else if(current.getBelow() != null){
                resultStack.add(current);
                current = current.getBelow();
            }

            else{
             resultStack.add(current);
             return resultStack;
            }
        }
    }

    private void link (SkipNode previousNode, SkipNode insertNode){
        insertNode.setPrevious(previousNode);
        insertNode.setNext(previousNode.getNext());

        previousNode.setNext(insertNode);

        if(insertNode.getNext() == null) return;
        else insertNode.getNext().setPrevious(insertNode);
    }

    private void unlink(SkipNode deleteNode){
        if(deleteNode == null) return;

        unlink(deleteNode.getAbove());

        deleteNode.getPrevious().setNext(deleteNode.getNext());
        if(deleteNode.getNext() != null) deleteNode.getNext().setPrevious(deleteNode.getPrevious());
    }

    //DiceIterator

    //NODE CLASS
    private class SkipNode{
        //Attributes
        private T value;
        private SkipNode next;
        private SkipNode previous;
        private SkipNode below;
        private SkipNode above;

        //CONSTRUCTOR
        SkipNode(T value){
            this.value = value;
        }

        //METHODS
        T getValue(){return this.value;}
        SkipNode getNext(){return this.next;}
        SkipNode getPrevious(){return this.previous;}
        SkipNode getBelow(){return this.below;}
        SkipNode getAbove(){return this.above;}
        void setValue(T value){this.value = value;}
        void setNext(SkipNode next){this.next = next;}
        void setPrevious(SkipNode previous){this.previous = previous;}
        void setBelow(SkipNode below){this.below = below;}
        void setAbove(SkipNode above){this.above = above;}

        private SkipNode addLayer(){
            this.setAbove(new SkipNode(this.getValue()));
            this.getAbove().setBelow(this);
            return this.getAbove();
        }

    }

}
