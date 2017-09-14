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
package hu.bme.mit.theta.formalism.cfa.creator;

import static hu.bme.mit.theta.core.stmt.Stmts.Assign;
import static hu.bme.mit.theta.core.stmt.Stmts.Assume;
import static hu.bme.mit.theta.core.stmt.Stmts.Havoc;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;

import java.util.List;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.common.product.Product2;
import hu.bme.mit.theta.common.product.Tuple;
import hu.bme.mit.theta.core.stmt.AssertStmt;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.AssumeStmt;
import hu.bme.mit.theta.core.stmt.BlockStmt;
import hu.bme.mit.theta.core.stmt.DeclStmt;
import hu.bme.mit.theta.core.stmt.DoStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.IfElseStmt;
import hu.bme.mit.theta.core.stmt.IfStmt;
import hu.bme.mit.theta.core.stmt.ReturnStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.stmt.WhileStmt;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.utils.StmtVisitor;
import hu.bme.mit.theta.formalism.cfa.CFA;
import hu.bme.mit.theta.formalism.cfa.CFA.Builder;
import hu.bme.mit.theta.formalism.cfa.CFA.Loc;

final class SbeCreator {

	static CFA create(final Stmt stmt) {
		final Builder cfa = CFA.builder();
		cfa.setInitLoc(cfa.createLoc("init"));
		cfa.setFinalLoc(cfa.createLoc("end"));
		cfa.setErrorLoc(cfa.createLoc("error"));
		final SBECreatorVisitor visitor = new SBECreatorVisitor(cfa);
		stmt.accept(visitor, Tuple.of(cfa.getInitLoc(), cfa.getFinalLoc()));
		return cfa.build();
	}

	private static class SBECreatorVisitor implements StmtVisitor<Product2<Loc, Loc>, Void> {

		private final CFA.Builder cfaBuilder;
		private int nextIndex;

		private SBECreatorVisitor(final CFA.Builder cfaBuilder) {
			this.cfaBuilder = cfaBuilder;
			nextIndex = 0;
		}

		private Void visitSimple(final Stmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();
			cfaBuilder.createEdge(source, target, ImmutableList.of(stmt));
			return null;
		}

		@Override
		public Void visit(final SkipStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();
			cfaBuilder.createEdge(source, target, ImmutableList.of());
			return null;
		}

		@Override
		public <DeclType extends Type> Void visit(final DeclStmt<DeclType> stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final List<Stmt> stmts;
			if (stmt.getInitVal().isPresent()) {
				stmts = ImmutableList.of(Assign(stmt.getVarDecl(), stmt.getInitVal().get()));
			} else {
				stmts = ImmutableList.of(Havoc(stmt.getVarDecl()));
			}

			cfaBuilder.createEdge(source, target, stmts);

			return null;
		}

		@Override
		public Void visit(final AssumeStmt stmt, final Product2<Loc, Loc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public Void visit(final AssertStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();
			cfaBuilder.createEdge(source, target, ImmutableList.of(Assume(stmt.getCond())));
			cfaBuilder.createEdge(source, cfaBuilder.getErrorLoc(), ImmutableList.of(Assume(Not(stmt.getCond()))));
			return null;
		}

		@Override
		public <DeclType extends Type> Void visit(final AssignStmt<DeclType> stmt, final Product2<Loc, Loc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public <DeclType extends Type> Void visit(final HavocStmt<DeclType> stmt, final Product2<Loc, Loc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public Void visit(final BlockStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final List<? extends Stmt> stmts = stmt.getStmts();

			if (stmts.isEmpty()) {
				cfaBuilder.createEdge(source, target, ImmutableList.of());
			} else {
				final Stmt head = stmts.get(0);
				final List<? extends Stmt> tail = stmts.subList(1, stmts.size());
				processNonEmptyBlock(cfaBuilder, source, target, head, tail);
			}

			return null;
		}

		private void processNonEmptyBlock(final CFA.Builder cfaBuilder, final Loc source, final Loc target,
				final Stmt head, final List<? extends Stmt> tail) {

			if (head instanceof ReturnStmt<?> || tail.isEmpty()) {
				head.accept(this, Tuple.of(source, target));
			} else {
				final Loc middle = cfaBuilder.createLoc("L" + nextIndex++);
				head.accept(this, Tuple.of(source, middle));

				final Stmt newHead = tail.get(0);
				final List<? extends Stmt> newTail = tail.subList(1, tail.size());

				processNonEmptyBlock(cfaBuilder, middle, target, newHead, newTail);
			}
		}

		@Override
		public <ReturnType extends Type> Void visit(final ReturnStmt<ReturnType> stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			cfaBuilder.createEdge(source, cfaBuilder.getFinalLoc(), ImmutableList.of(stmt));
			return null;
		}

		@Override
		public Void visit(final IfStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final Loc thenLoc = cfaBuilder.createLoc("L" + nextIndex++);
			cfaBuilder.createEdge(source, thenLoc, ImmutableList.of(Assume(stmt.getCond())));
			stmt.getThen().accept(this, Tuple.of(thenLoc, target));

			cfaBuilder.createEdge(source, target, ImmutableList.of(Assume(Not(stmt.getCond()))));

			return null;
		}

		@Override
		public Void visit(final IfElseStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final Loc thenLoc = cfaBuilder.createLoc("L" + nextIndex++);
			cfaBuilder.createEdge(source, thenLoc, ImmutableList.of(Assume(stmt.getCond())));
			stmt.getThen().accept(this, Tuple.of(thenLoc, target));

			final Loc elseLoc = cfaBuilder.createLoc("L" + nextIndex++);
			cfaBuilder.createEdge(source, elseLoc, ImmutableList.of(Assume(Not(stmt.getCond()))));
			stmt.getElse().accept(this, Tuple.of(elseLoc, target));

			return null;
		}

		@Override
		public Void visit(final WhileStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final Loc doLoc = cfaBuilder.createLoc("L" + nextIndex++);
			cfaBuilder.createEdge(source, doLoc, ImmutableList.of(Assume(stmt.getCond())));
			stmt.getDo().accept(this, Tuple.of(doLoc, source));
			cfaBuilder.createEdge(source, target, ImmutableList.of(Assume(Not(stmt.getCond()))));
			return null;
		}

		@Override
		public Void visit(final DoStmt stmt, final Product2<Loc, Loc> param) {
			final Loc source = param._1();
			final Loc target = param._2();

			final Loc doLoc = cfaBuilder.createLoc("L" + nextIndex++);
			stmt.getDo().accept(this, Tuple.of(source, doLoc));
			cfaBuilder.createEdge(doLoc, source, ImmutableList.of(Assume(stmt.getCond())));
			cfaBuilder.createEdge(doLoc, target, ImmutableList.of(Assume(Not(stmt.getCond()))));
			return null;
		}

	}

}
