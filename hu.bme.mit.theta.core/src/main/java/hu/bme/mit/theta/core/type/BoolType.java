package hu.bme.mit.theta.core.type;

import static hu.bme.mit.theta.core.type.booltype.BoolExprs.False;

import java.util.Optional;

import hu.bme.mit.theta.core.expr.LitExpr;

public final class BoolType implements Type {
	private static final BoolType INSTANCE = new BoolType();
	private static final int HASH_SEED = 754364;
	private static final String TYPE_LABEL = "Bool";

	private BoolType() {
	}

	public static BoolType getInstance() {
		return INSTANCE;
	}

	@Override
	public LitExpr<BoolType> getAny() {
		return False();
	}

	@Override
	public boolean isLeq(final Type type) {
		return this.equals(type);
	}

	@Override
	public Optional<BoolType> meet(final Type type) {
		if (type.isLeq(this)) {
			assert type instanceof BoolType;
			final BoolType that = (BoolType) type;
			return Optional.of(that);
		} else {
			assert !this.isLeq(type);
			return Optional.empty();
		}
	}

	@Override
	public Optional<BoolType> join(final Type type) {
		if (type.isLeq(this)) {
			return Optional.of(this);
		} else {
			assert !this.isLeq(type);
			return Optional.empty();
		}
	}

	@Override
	public int hashCode() {
		return HASH_SEED;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoolType;
	}

	@Override
	public String toString() {
		return TYPE_LABEL;
	}

}
