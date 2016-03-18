/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.home.client.widgets.home;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.i18n.client.LocaleInfo;

public class HomeImagesHelper {

    private static final String IMAGE_PREFIX = "images/home/";

    private static final String IMAGE_SUFFIX = "png";

    static String getLocaleName() {
        final LocaleInfo locale = LocaleInfo.getCurrentLocale();
        final String localeName = locale.getLocaleName();
        return localeName;
    }

    static String[] getAvailableLocaleNames() {
        return LocaleInfo.getAvailableLocaleNames();
    }

    public enum Images {

        Authoring( "01_Model_Graphic" ),
        Deploy( "02_Deploy_Graphic" ),
        ProcessManagement( "03_ManageProcesses_Graphic" ),
        Tasks( "04_ManageTasks_Graphic" ),
        Dashboard( "05_Monitor_Graphic" ),
        Analyze( "06_Analyze_Graphic" );

        private String baseName;

        Images( final String baseName ) {
            this.baseName = baseName;
        }

        public String getLocalisedImageUrl() {
            final String language = getLanguage();
            if ( language.equals( "" ) ) {
                return IMAGE_PREFIX + baseName + "." + IMAGE_SUFFIX;
            } else {
                return IMAGE_PREFIX + language + "/" + baseName + "-" + language + "." + IMAGE_SUFFIX;
            }
        }

        private String getLanguage() {
            final String localeName = getLocaleName();
            final Set<String> availableLocaleNames = new HashSet<String>();
            availableLocaleNames.addAll( Arrays.asList( getAvailableLocaleNames() ) );
            if ( !availableLocaleNames.contains( localeName ) ) {
                return "";
            }
            if ( localeName == null || localeName.isEmpty() ) {
                return "";
            }
            if ( localeName.equalsIgnoreCase( "default" ) ) {
                return "";
            }
            String language = localeName.toLowerCase();
            if ( language.contains( "_" ) ) {
                language = language.substring( 0,
                                               language.indexOf( "_" ) );
            }
            if ( language.equals( "en" ) ) {
                return "";
            }
            return language;
        }

    }

}
