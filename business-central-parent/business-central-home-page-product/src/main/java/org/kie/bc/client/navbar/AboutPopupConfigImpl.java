/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.bc.client.navbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.bc.client.resources.i18n.Constants;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupConfig;
import org.uberfire.preferences.shared.event.PreferenceUpdatedEvent;

@ApplicationScoped
@EntryPoint
public class AboutPopupConfigImpl implements AboutPopupConfig {

    private TranslationService translationService;
    
    private ProfilePreferences profilePreferences;
    
    String productNameConstant = Constants.ProductName;
    
    @Inject
    public AboutPopupConfigImpl(ProfilePreferences profilePreferences,
                                TranslationService translationService) {
        this.profilePreferences = profilePreferences;
        this.translationService = translationService;
    }
    
    @PostConstruct
    public void init() {
        profilePreferences.load(this::updateProductName, RuntimeException::new);
    }

    @Override
    public String productName() {
        return translationService.format(productNameConstant);
    }

    @Override
    public String productVersion() {
        return "${version.org.kie.workbench.app}";
    }

    @Override
    public String productLicense() {
        return translationService.format(Constants.License);
    }

    @Override
    public String productImageUrl() {
        return "banner/logo.png";
    }

    @Override
    public String backgroundImageUrl() {
        return "images/home-background.svg";
    }
    
    public void refreshMenuOnProfilesChange(@Observes PreferenceUpdatedEvent event) {
        if (event.getKey().equalsIgnoreCase("ProfilePreferences")) {
            ProfilePreferences pref = (ProfilePreferences) event.getValue();
            updateProductName(pref);
        }
    } 
    
    private void updateProductName(ProfilePreferences p) {
        switch(p.getProfile()) {
            case Profile.FULL:
                productNameConstant = Constants.ProductName;
            default:
                throw new RuntimeException(String.format("%s is not expected and profile to define product name", p.getProfile()));
        }
    }
}
