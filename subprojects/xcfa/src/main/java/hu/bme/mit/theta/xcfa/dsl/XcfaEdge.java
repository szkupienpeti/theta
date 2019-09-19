package hu.bme.mit.theta.xcfa.dsl;

import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.xcfa.XCFA;
import hu.bme.mit.theta.xcfa.dsl.gen.XcfaDslParser;

import java.util.ArrayList;
import java.util.List;

public class XcfaEdge implements Instantiatable<XCFA.Process.Procedure.Edge> {

    private XCFA.Process.Procedure.Edge built = null;

    private final XcfaProcedureSymbol scope;

    private final XcfaLocationSymbol source;
    private final XcfaLocationSymbol target;

    private final List<XcfaStatement> stmts;

    XcfaEdge(final XcfaProcedureSymbol scope, final XcfaDslParser.EdgeContext context) {
        this.scope = scope;
        source = (XcfaLocationSymbol) scope.resolve(context.source.getText()).get();
        target = (XcfaLocationSymbol) scope.resolve(context.target.getText()).get();

        stmts = new ArrayList<>();
        context.stmts.forEach(stmtContext -> stmts.add(new XcfaStatement(scope, stmtContext)));
    }

    @Override
    public XCFA.Process.Procedure.Edge instantiate() {
        if(built != null) return built;
        List<Stmt> stmts = new ArrayList<>();
        this.stmts.forEach(xcfaStatement -> stmts.add(xcfaStatement.instantiate()));
        return built = new XCFA.Process.Procedure.Edge(source.instantiate(), target.instantiate(), stmts);
    }

    public List<XcfaStatement> getStatements(){
        return stmts;
    }
}
