package org.kie.smoke.wb.category;

/**
 * Some containers not (yet?) support JMS configuration, so the JMS tests need to be disabled when running
 * on such containers. This is done using the JUnit categories (and this particular one). When running
 * on non-JMS containers, this category is excluded from the run.
 */
public interface JMSSmoke {
}
