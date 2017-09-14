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
package hu.bme.mit.theta.formalism.cfa.dsl;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Bool;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;

import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslBaseVisitor;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.BoolTypeContext;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.IntTypeContext;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.TypeContext;

final class CfaType {

	private final TypeContext context;

	public CfaType(final TypeContext context) {
		this.context = checkNotNull(context);
	}

	public Type instantiate() {
		final Type result = TypeCreatorVisitor.INSTANCE.visit(context);
		if (result == null) {
			throw new AssertionError();
		} else {
			return result;
		}
	}

	private static class TypeCreatorVisitor extends CfaDslBaseVisitor<Type> {
		private static final TypeCreatorVisitor INSTANCE = new TypeCreatorVisitor();

		private TypeCreatorVisitor() {
		}

		@Override
		public Type visitBoolType(final BoolTypeContext ctx) {
			return Bool();
		}

		@Override
		public Type visitIntType(final IntTypeContext ctx) {
			return Int();
		}

	}

}
