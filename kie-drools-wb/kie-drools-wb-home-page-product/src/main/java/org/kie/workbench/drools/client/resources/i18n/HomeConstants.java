/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.drools.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * i18n constants for the Home Page
 */
public interface HomeConstants
        extends
        Messages {

    HomeConstants INSTANCE = GWT.create( HomeConstants.class );

    String authoring_header();

    String deploy_header();

    String analyze_header();

    String authoring_paragraph();

    String deploy_paragraph();

    String analyze_paragraph();

    String authoring_image();

    String deploy_image();

    String analyze_image();

    String home_title();

    String home_subtitle();

}