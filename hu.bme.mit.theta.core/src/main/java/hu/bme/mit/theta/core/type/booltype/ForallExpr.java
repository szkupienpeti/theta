/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.core.type.booltype;

import hu.bme.mit.theta.core.decl.ParamDecl;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;

public final class ForallExpr extends QuantifiedExpr {

	private static final int HASH_SEED = 6871;

	private static final String OPERATOR_LABEL = "Forall";

	ForallExpr(final Iterable<? extends ParamDecl<?>> paramDecls, final Expr<BoolType> op) {
		super(paramDecls, op);
	}

	@Override
	public LitExpr<BoolType> eval(final Valuation val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ForallExpr with(final Expr<BoolType> op) {
		if (op == getOp()) {
			return this;
		} else {
			return new ForallExpr(getParamDecls(), op);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ForallExpr) {
			final ForallExpr that = (ForallExpr) obj;
			return this.getParamDecls().equals(that.getParamDecls()) && this.getOp().equals(that.getOp());
		} else {
			return false;
		}
	}

	@Override
	protected int getHashSeed() {
		return HASH_SEED;
	}

	@Override
	protected String getOperatorLabel() {
		return OPERATOR_LABEL;
	}
}
