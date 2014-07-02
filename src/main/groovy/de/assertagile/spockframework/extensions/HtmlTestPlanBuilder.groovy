package de.assertagile.spockframework.extensions

import groovy.xml.MarkupBuilder
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * Created by mkutz on 27.05.14.
 */
class HtmlTestPlanBuilder extends TestPlanBuilder {

    private final String jiraUrl
    private final String jiraPid
    private final boolean jiraEnabled

    private MarkupBuilder _htmlWriter

    public HtmlTestPlanBuilder(String filePath, Locale locale = Locale.ENGLISH, String jiraUrl = null, String jiraPid = null) {
        super(filePath, locale)

        this.jiraEnabled = jiraUrl && jiraPid
        this.jiraUrl = jiraUrl
        this.jiraPid = jiraPid
    }

    @Override
    void appendHeader() {}

    @Override
    void appendSpec(Manual annotation, SpecInfo spec) {
        htmlWriter.h2 getSpecTitle(annotation, spec)
    }

    @Override
    void appendFeature(Manual annotation, FeatureInfo feature) {
        htmlWriter.h3 {
            if (annotation?.story()) {
                if (jiraEnabled) {
                    a(class: "story", href: issueJiraLink(annotation.story()), annotation.story())
                } else {
                    span(class: "story", annotation.story())
                }
            }
            span(class: "featureName", feature.name)
        }

        htmlWriter.p {
            if (annotation?.knownBugs()) {
                h4 "Bugs: "
                ul(class: "bugList") {
                    annotation?.knownBugs()?.each { String bugId ->
                        li(class: "bug") {
                            jiraEnabled ? a(href: issueJiraLink(bugId), bugId) : span(bugId)
                        }
                    }
                }
            }
        }

        if (jiraEnabled) htmlWriter.p {
            a(href: openBugJiraLink(feature), "create Bug")
        }

        htmlWriter.dl(class: "testStepList") {
            feature.blocks.each { BlockInfo block ->
                dt blockKindToString(block.kind)
                dd {
                    ul {
                        block.texts.each { String text ->
                            li text
                        }
                    }
                }
            }
        }
    }

    private String openBugJiraLink(FeatureInfo feature) {
        StringBuilder description = new StringBuilder()
        feature.blocks.each { BlockInfo block ->
            description << "${block.kind} ${block.texts.join("\nand")}\n"
        }

        "${jiraUrl}/secure/CreateIssueDetails!init.jspa?" +
                "pid=${jiraPid}&" +
                "issuetype=1&" +
                "summary=${URLEncoder.encode(feature.name, "utf-8")}&" +
                "description=${URLEncoder.encode(description.toString(), "utf-8")}"
    }

    private String issueJiraLink(String issueId) {
        "${jiraUrl}/browse/${issueId}"
    }

    private MarkupBuilder getHtmlWriter() {
        if (!_htmlWriter) {
            _htmlWriter = new MarkupBuilder(writer)
            _htmlWriter.escapeAttributes = false
        }
        return _htmlWriter
    }
}