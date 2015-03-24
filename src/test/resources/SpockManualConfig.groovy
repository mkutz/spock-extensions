import de.assertagile.spockframework.extensions.CsvTestPlanBuilder
import de.assertagile.spockframework.extensions.MarkDownTestPlanBuilder

Locale locale = Locale.ENGLISH
String issueTrackerBaseUrl = "http://assertagile.de/issues"
boolean markManualTestsAsExcluded = true

testPlanBuilders = [
    new MarkDownTestPlanBuilder("target/test_plan.md", issueTrackerBaseUrl, locale),
    new CsvTestPlanBuilder("target/test_plan.csv", issueTrackerBaseUrl, locale)
]

