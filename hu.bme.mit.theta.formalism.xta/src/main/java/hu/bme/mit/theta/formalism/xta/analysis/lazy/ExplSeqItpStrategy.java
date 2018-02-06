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
package hu.bme.mit.theta.formalism.xta.analysis.lazy;

import java.util.Collection;

import hu.bme.mit.theta.analysis.algorithm.ArgEdge;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.prod4.Prod4State;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;

public final class ExplSeqItpStrategy extends ExplItpStrategy {

	private ExplSeqItpStrategy(final XtaSystem system) {
		super(system);
	}

	public static ExplSeqItpStrategy create(final XtaSystem system) {
		return new ExplSeqItpStrategy(system);
	}

	@Override
	protected ZoneState blockZone(
			final ArgNode<XtaState<Prod4State<ExplState, ZoneState, ExplState, ZoneState>>, XtaAction> node,
			final ZoneState zone,
			final Collection<ArgNode<XtaState<Prod4State<ExplState, ZoneState, ExplState, ZoneState>>, XtaAction>> uncoveredNodes,
			final LazyXtaStatistics.Builder stats) {

		final ZoneState abstractZone = node.getState().getState().getState4();
		if (abstractZone.isConsistentWith(zone)) {
			stats.refineZone();

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<Prod4State<ExplState, ZoneState, ExplState, ZoneState>>, XtaAction> inEdge = node
						.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<Prod4State<ExplState, ZoneState, ExplState, ZoneState>>, XtaAction> parent = inEdge
						.getSource();

				final ZoneState B_pre = pre(zone, action);
				final ZoneState A_pre = blockZone(parent, B_pre, uncoveredNodes, stats);

				final ZoneState B = zone;
				final ZoneState A = post(A_pre, action);

				final ZoneState interpolant = ZoneState.interpolant(A, B);

				strengthenZone(node, interpolant);
				maintainZoneCoverage(node, interpolant, uncoveredNodes);

				return interpolant;
			} else {
				final ZoneState concreteZone = node.getState().getState().getState2();

				final ZoneState interpolant = ZoneState.interpolant(concreteZone, zone);

				strengthenZone(node, interpolant);
				maintainZoneCoverage(node, interpolant, uncoveredNodes);

				return interpolant;
			}
		} else {
			return abstractZone;
		}
	}

}