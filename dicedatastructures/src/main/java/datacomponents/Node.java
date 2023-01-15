package datacomponents;

abstract class Node<T> {
    //Attributes
    /* Three attributes provided. The value of the node, the previous node and the next node.
    Arranged in Arrays to allow for multiple previous nodes and succeeding nodes.
     */
    private ValueType valueType;
    private NodeType nodeType;
    private T value;

    //Constructors
    /*
    Two Constructor options are provided. Only the node value can be provided or the value and the NodeType. The NodeType indicates
    whether the Node is a LEAF, a HEAD, a TAIL or a NORMAL node. The constructor also checks the type of T and flags the nodes as either
    Comparable or Incomparable.
    */
    /**
     *
     * @param value
     * @param nodeType
     */
    Node(T value, NodeType nodeType){
        this.setValue(value);
        this.checkComparability(value);
        this.nodeType = nodeType;
    }
    Node(T value){
        this(value,NodeType.NORMAL);
    }
    //Methods
    /*
    Public Getter and Setter for value and only Getters for NodeType and ValueType
     */
    T getValue(){
        return this.value;
    }
    void setValue(T value){
        this.checkComparability(value);
    }
    NodeType getNodeType(){
        return this.nodeType;
    }
    ValueType getValueType(){
        return this.valueType;
    }
    //Internal Methods
    /*
    Checks value to flag the node for Comparibility, if value implements the Comparable<T> Interface
     */
    private void checkComparability(T value){
        this.valueType = (value instanceof Comparable<?>)?  ValueType.COMPARABLE:ValueType.INCOMPARABLE;
    }

}
/*
Two Enums are declared in the same file that allow for the configuration of individual nodes
 */
enum NodeType{
    LEAF, HEAD , TAIL , NORMAL
}

enum ValueType{
    COMPARABLE, INCOMPARABLE
}


