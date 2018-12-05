public class Node {
    double val;
    double syn;
    double inh;
    Node leftNode;
    Node rightNode;

    Node(Node leftNode, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        val = 0.0;
        syn = 0.0;
        inh = 0.0;
    }

    Node(){
        this(null, null);
    }
}
