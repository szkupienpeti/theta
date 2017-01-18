package hu.bme.mit.theta.frontend.benchmark;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import hu.bme.mit.theta.analysis.algorithm.SafetyStatus;
import hu.bme.mit.theta.analysis.algorithm.Statistics;
import hu.bme.mit.theta.common.table.TableWriter;
import hu.bme.mit.theta.common.table.impl.SimpleTableWriter;
import hu.bme.mit.theta.core.expr.impl.Exprs;
import hu.bme.mit.theta.core.utils.impl.ExprMetrics;
import hu.bme.mit.theta.core.utils.impl.ExprUtils;
import hu.bme.mit.theta.formalism.sts.STS;
import hu.bme.mit.theta.formalism.sts.dsl.StsDslManager;
import hu.bme.mit.theta.formalism.sts.dsl.StsSpec;
import hu.bme.mit.theta.formalism.sts.utils.impl.StsIteTransformation;
import hu.bme.mit.theta.frontend.aiger.impl.AigerParserSimple;
import hu.bme.mit.theta.frontend.benchmark.StsConfigurationBuilder.Domain;
import hu.bme.mit.theta.frontend.benchmark.StsConfigurationBuilder.InitPrecision;
import hu.bme.mit.theta.frontend.benchmark.StsConfigurationBuilder.Refinement;
import hu.bme.mit.theta.frontend.benchmark.StsConfigurationBuilder.Search;

public class Main {

	public static void main(final String[] args) {
		final Options options = new Options();

		final Option optModel = new Option("m", "model", true, "Path of the input model");
		optModel.setRequired(true);
		optModel.setArgName("MODEL");
		options.addOption(optModel);

		final Option optProp = new Option("p", "property", true, "Property to be verified");
		optProp.setArgName("PROPERTY");
		options.addOption(optProp);

		final Option optDomain = new Option("d", "domain", true, "Abstract domain");
		optDomain.setRequired(true);
		optDomain.setArgName("DOMAIN");
		options.addOption(optDomain);

		final Option optRefinement = new Option("r", "refinement", true, "Refinement strategy");
		optRefinement.setRequired(true);
		optRefinement.setArgName("REFINEMENT");
		options.addOption(optRefinement);

		final Option optInitPrecision = new Option("i", "initprec", true, "Initial precision");
		optInitPrecision.setArgName("INITPRECISION");
		options.addOption(optInitPrecision);

		final Option optSearch = new Option("s", "search", true, "Search strategy");
		optSearch.setArgName("SEARCH");
		options.addOption(optSearch);

		final Option optExpected = new Option("e", "expected", true, "Expected result (safe)");
		optExpected.setArgName("true|false");
		options.addOption(optExpected);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter helpFormatter = new HelpFormatter();
		final CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			helpFormatter.printHelp("theta.jar", options, true);
			return;
		}

		final String model = cmd.getOptionValue(optModel.getOpt());
		final Domain domain = Domain.valueOf(cmd.getOptionValue(optDomain.getOpt()));
		final Refinement refinement = Refinement.valueOf(cmd.getOptionValue(optRefinement.getOpt()));

		final InitPrecision initPrecision = InitPrecision
				.valueOf(cmd.getOptionValue(optInitPrecision.getOpt(), InitPrecision.EMPTY.toString()));
		final Search search = Search.valueOf(cmd.getOptionValue(optSearch.getOpt(), Search.BFS.toString()));

		final Optional<Boolean> expected = cmd.hasOption(optExpected.getOpt())
				? Optional.of(Boolean.parseBoolean(cmd.getOptionValue(optExpected.getOpt()))) : Optional.empty();

		final TableWriter tableWriter = new SimpleTableWriter(System.out, ",", "", "");

		try {

			// Output: configuration parameters
			tableWriter.cell(model);

			STS sts = null;

			if (model.endsWith(".aag")) {
				sts = new AigerParserSimple().parse(model);
			} else {
				final String prop = cmd.getOptionValue(optProp.getOpt());
				final InputStream inputStream = new FileInputStream(model);
				final StsSpec spec = StsDslManager.createStsSpec(inputStream);
				sts = new StsIteTransformation().transform(spec.createProp(prop));
			}

			tableWriter.cell(sts.getVars().size());
			tableWriter.cell(ExprUtils.size(Exprs.And(Exprs.And(sts.getInit()), Exprs.And(sts.getTrans())),
					ExprMetrics.absoluteSize()));
			tableWriter.cell(domain.toString()).cell(refinement.toString()).cell(initPrecision.toString())
					.cell(search.toString());
			System.out.flush();

			final Configuration<?, ?, ?> configuration = new StsConfigurationBuilder(domain, refinement)
					.initPrecision(initPrecision).search(search).build(sts);
			final SafetyStatus<?, ?> status = configuration.check();
			final Statistics stats = status.getStats().get();

			if (expected.isPresent() && !expected.get().equals(status.isSafe())) {
				tableWriter.cell("ERROR: expected safe = " + expected.get());
			} else {

				tableWriter.cell(status.isSafe() + "").cell(stats.getElapsedMillis() + "")
						.cell(stats.getIterations() + "").cell(status.getArg().size() + "")
						.cell(status.getArg().getDepth() + "");

				if (status.isUnsafe()) {
					tableWriter.cell(status.asUnsafe().getTrace().length() + "");
				}
			}
		} catch (final Exception ex) {
			tableWriter.cell("EX: " + ex.getClass().getSimpleName());
		}

		tableWriter.newRow();
	}

}
