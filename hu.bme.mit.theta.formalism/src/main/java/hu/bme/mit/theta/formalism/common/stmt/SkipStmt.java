package hu.bme.mit.theta.formalism.common.stmt;

import hu.bme.mit.theta.formalism.utils.StmtVisitor;

public interface SkipStmt extends Stmt {

	@Override
	public default <P, R> R accept(StmtVisitor<? super P, ? extends R> visitor, P param) {
		return visitor.visit(this, param);
	}
	
}