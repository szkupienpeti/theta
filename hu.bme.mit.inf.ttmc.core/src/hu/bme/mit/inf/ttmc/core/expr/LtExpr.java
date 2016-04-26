package hu.bme.mit.inf.ttmc.core.expr;

import hu.bme.mit.inf.ttmc.core.type.BoolType;
import hu.bme.mit.inf.ttmc.core.type.RatType;

public interface LtExpr extends BinaryExpr<RatType, RatType, BoolType> {
	
	@Override
	public LtExpr withOps(final Expr<? extends RatType> leftOp, final Expr<? extends RatType> rightOp);
	
	@Override
	public LtExpr withLeftOp(final Expr<? extends RatType> leftOp);

	@Override
	public LtExpr withRightOp(final Expr<? extends RatType> rightOp);
}
