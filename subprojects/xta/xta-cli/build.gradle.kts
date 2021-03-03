plugins {
    id("java-common")
    id("cli-tool")
}

dependencies {
    compile(project(":theta-xta"))
    compile(project(":theta-xta-analysis"))
    compile(project(":theta-solver-z3"))
    compile(project(":theta-xta-testgen"))
}

application {
    mainClassName = "hu.bme.mit.theta.xta.cli.XtaCli"
}
