//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.fpstree;

import java.util.*;

import utils.Pair;

public class FPSTreeNode {
    private FPSTreeNode parent;
    public Map<Integer,FPSTreeNode> children = new HashMap<>();
    private Pair pair;
    public FPSTreeNode() {
    }

    public FPSTreeNode(Pair pair, FPSTreeNode parent) {
        this.pair = pair;
        this.parent = parent;
        parent.children.put(pair.getFirst(),this);
    }
    public Integer getName() {
        return this.pair.getFirst();
    }
    public int getSize() {
        return this.pair.getSecond();
    }
    public FPSTreeNode getParent() {
        return this.parent;
    }
    public FPSTreeNode getChildByName(Integer name) {
        return children.get(name);
    }

    public String toString() {
        return "FPSTreeNode{name='" + this.pair.getFirst() + '\'' + ", size=" + this.pair.getSecond() + ", parents=" + this.parent + ", children=" + this.children +  '\'' + '}';
    }
}
