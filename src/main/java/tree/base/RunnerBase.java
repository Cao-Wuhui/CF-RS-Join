package tree.base;

import utils.Record;

public abstract class RunnerBase extends Thread {
    public RunnerBase() {
    }
    public abstract void add(Query query);
}
