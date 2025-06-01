//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.fpstree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import tree.base.MineResult;
import tree.base.Query;
import tree.base.TreeBase;
import utils.*;

public class FPSTree extends TreeBase{
    public Map<Integer, FPSTreeNode> aidToLastNode = new HashMap();
    private Map<Integer, Integer> aidToCount = new HashMap();
    public FPSTreeNode root = new FPSTreeNode();
    public double Threshold;
    public double Threshold100;
    public List<Record> listofS;
    public FPSTree() {
    }
    //插入结点
    private FPSTreeNode insert(List<Pair> transaction) {
        FPSTreeNode currentNode = this.root;
        FPSTreeNode child;
        for(Iterator var4 = transaction.iterator(); var4.hasNext(); currentNode = child) { //遍历一个sequence
            Pair pair = (Pair)var4.next(); //遍历要插入的元素
            child = currentNode.getChildByName(pair.getFirst());//1主要是这一步耗时间
            if (child == null) {
                child = new FPSTreeNode(pair, currentNode);//
            }
        }
        return currentNode;
    }
    //建树
    public void createTree(Object var1, double threshold, List<Record> listofS) {
        Map<Integer, List<Pair>> aidToPairList;
        if (var1 instanceof Map) {
            aidToPairList = (Map<Integer, List<Pair>>) var1;
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
        this.listofS = listofS;
        this.Threshold = threshold;
        this.Threshold100 = threshold * 100.0D; //确保精度不丢失

        for(Map.Entry<Integer,List<Pair>> entry : aidToPairList.entrySet()){
            Integer aid = entry.getKey();
            List<Pair> pairs = entry.getValue();
            FPSTreeNode lastNode = this.insert(pairs);
            this.aidToLastNode.put(aid, lastNode);
            this.aidToCount.put(aid, pairs.size());
        }
    }

    //查询集生成
    public List<Query> generateQuery(List<Record> lisofR) {
        List<Query> querylist = new ArrayList<>(lisofR.size());
        for(int i = 0; i < lisofR.size(); i++){ //对于每一行r
            ArrayList<Integer> tokens = lisofR.get(i).tokens;
            Query query = new Query(lisofR.get(i).recordid, tokens.size()); // 一行 r 的查询集，需要对其中的 List<Pair> aidPairs 进行插入，插入完进行排序
            for(Integer j : tokens)
                query.addPair(new Pair(j, tokens.size()));
            querylist.add(query);
        }
        return querylist;
    }

    //搜索阶段
    public MineResult search(Query query) {
        Map<FPSTreeNode, Integer> aidSet = new HashMap();
        int size = query.getUidSize();
        for(Pair i : query.getAidPairs()){
            FPSTreeNode node = this.aidToLastNode.get(i.getFirst());
            if(node != null)
            aidSet.put(node,aidSet.getOrDefault(node,0)+1);
        }
        Map<Integer, CandidateData> candidateSet = new HashMap<>();
        Map<Integer,Integer> minoverlapcache = new HashMap<>();

        int reclen = query.getUidSize();
        int minsize = (int) (Math.ceil(reclen*Threshold));
        if(reclen != 0){
            int maxsize = (int)(reclen/Threshold);
            for(int i = minsize; i <= maxsize; i++){
                int overlap = (int)Math.ceil( Threshold100 * (reclen + i) / (100.0D + Threshold100) );
                overlap = Math.min(i, Math.min( reclen, overlap));
                minoverlapcache.put(i, overlap);
            }
        }
        // 遍历r的每个元素
        Iterator tokens = query.getAidPairs().iterator();
        while(true){
            FPSTreeNode currentNode;
            do{
                //验证
                if(!tokens.hasNext()){
                    MineResult result = new MineResult(query.getUid());
                    for (Map.Entry<Integer, CandidateData> entry : candidateSet.entrySet()) {
                        Integer sid = entry.getKey();
                        CandidateData candi = entry.getValue();
                        if(candi.count >= candi.minoverlap){
                            double weight =  candi.count * 1.0 / (size + candi.size - candi.count); //r,s
                            result.add(sid, weight);
                        }
                    }
                    return result;
                }
                Pair aidPair = (Pair) tokens.next();
                currentNode = this.aidToLastNode.get(aidPair.getFirst());
                if(currentNode == null);
            }while(aidSet.getOrDefault(currentNode,0) == 0);
            int support = 0;
            for(; currentNode != this.root && currentNode.getSize() * Threshold <= size; currentNode = currentNode.getParent()){
                int cursize = currentNode.getSize(), nodeName = currentNode.getName();
                int c = aidSet.getOrDefault(currentNode,0);
                if(c != 0){
                    aidSet.put(currentNode,0);
                    support += c; //交集大小 += c
                }
                if(cursize >= minsize){
                    CandidateData candidateData = candidateSet.get(nodeName);
                    if (candidateData == null) {
                        candidateSet.put(nodeName, new CandidateData(support, minoverlapcache.get(cursize), cursize));
                    }
                    else{
                        candidateData.count += support;
                    }
                }
            }
        }
    }

    public long info() {
        Stack<FPSTreeNode> st = new Stack();
        for(FPSTreeNode child : root.children.values()){
            st.push(child);
        }
        int count = 0;
        while(!st.empty()) {
            ++count;
            FPSTreeNode node = st.pop();
            for(FPSTreeNode child : node.children.values()){
                st.push(child);
            }
        }
        System.out.println("count of the tree node is = " + count); // 树结点的个数
        long A = 2;
        return A;
    }
}
