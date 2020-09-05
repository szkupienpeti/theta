package hu.bme.mit.theta.xcfa.analysis.stateless;

import hu.bme.mit.theta.common.Tuple3;
import hu.bme.mit.theta.core.model.MutableValuation;
import hu.bme.mit.theta.core.stmt.*;
import hu.bme.mit.theta.core.stmt.xcfa.*;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.xcfa.XCFA;

public class XcfaStmtExecutionVisitor implements StmtVisitor<Tuple3<MutableValuation, State, XCFA.Process>, Void>, XcfaStmtVisitor<Tuple3<MutableValuation, State, XCFA.Process>, Void> {
    @Override
    public Void visit(SkipStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        /* Intentionally left empty. */
        return null;
    }

    @Override
    public Void visit(AssumeStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        /* Intentionally left empty. */
        return null;
    }

    @Override
    public <DeclType extends Type> Void visit(AssignStmt<DeclType> stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get1().put(stmt.getVarDecl(), stmt.getExpr().eval(param.get1()));
        return null;
    }

    @Override
    public <DeclType extends Type> Void visit(HavocStmt<DeclType> stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get1().remove(stmt.getVarDecl());
        return null;
    }

    @Override
    public Void visit(XcfaStmt xcfaStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        return xcfaStmt.accept(this, param);
    }

    @Override
    public Void visit(SequenceStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(NonDetStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(OrtStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(XcfaCallStmt stmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(StoreStmt storeStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get1().put(storeStmt.getRhs(), param.get1().eval(storeStmt.getLhs()).get());
        return null;
    }

    @Override
    public Void visit(LoadStmt loadStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get1().put(loadStmt.getLhs(), param.get1().eval(loadStmt.getRhs()).get());
        return null;
    }

    @Override
    public Void visit(AtomicBeginStmt atomicBeginStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get2().setCurrentlyAtomic(param.get3());
        return null;
    }

    @Override
    public Void visit(AtomicEndStmt atomicEndStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        param.get2().setCurrentlyAtomic(null);
        return null;
    }

    @Override
    public Void visit(NotifyAllStmt notifyAllStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(NotifyStmt notifyStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(WaitStmt waitStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(MtxLockStmt lockStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(MtxUnlockStmt unlockStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(ExitWaitStmt exitWaitStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(EnterWaitStmt enterWaitStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visit(XcfaInternalNotifyStmt enterWaitStmt, Tuple3<MutableValuation, State, XCFA.Process> param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
