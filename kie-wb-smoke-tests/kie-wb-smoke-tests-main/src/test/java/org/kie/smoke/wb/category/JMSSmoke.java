/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.smoke.wb.category;

/**
 * Some containers not (yet?) support JMS configuration, so the JMS tests need to be disabled when running
 * on such containers. This is done using the JUnit categories (and this particular one). When running
 * on non-JMS containers, this category is excluded from the run.
 */
public interface JMSSmoke {
}
