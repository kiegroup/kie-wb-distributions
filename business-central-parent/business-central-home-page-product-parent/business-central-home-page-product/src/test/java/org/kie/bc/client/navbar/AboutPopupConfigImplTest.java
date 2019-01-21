package org.kie.bc.client.navbar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.kie.bc.client.resources.i18n.Constants;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.mocks.ParametrizedCommandMock;
import org.uberfire.mvp.ParameterizedCommand;

public class AboutPopupConfigImplTest {
    
    @Test
    public void aboutPopUpForFullProfile() {
        ProfilePreferences profilePreferences = mockProfileServices(Profile.FULL);
        TranslationService translationService = mockTranslationservice();
        AboutPopupConfigImpl aboutPopup = new AboutPopupConfigImpl(profilePreferences, translationService);
        aboutPopup.init();
        aboutPopup.productName();
        Mockito.verify(translationService, times(0)).format(Constants.ProductNameRHDM);
        Mockito.verify(translationService).format(Constants.ProductName);
                                
    }
    
    @Test
    public void aboutPopUpForPlannerAndRulesProfile() {
        ProfilePreferences profilePreferences = mockProfileServices(Profile.PLANNER_AND_RULES);
        TranslationService translationService = mockTranslationservice();
        AboutPopupConfigImpl aboutPopup = new AboutPopupConfigImpl(profilePreferences, translationService);
        aboutPopup.init();
        aboutPopup.productName();
        Mockito.verify(translationService, times(0)).format(Constants.ProductName);
        Mockito.verify(translationService).format(Constants.ProductNameRHDM);
    }
    
    private ProfilePreferences mockProfileServices(Profile profile) {
        ProfilePreferences profilePreferences = mock(ProfilePreferences.class);
        ParametrizedCommandMock.executeParametrizedCommandWith(0, new ProfilePreferences(profile))
                               .when(profilePreferences).load(any(ParameterizedCommand.class), 
                                                              any(ParameterizedCommand.class));
        return profilePreferences;
    }
    
    private TranslationService mockTranslationservice() {
        TranslationService translationService = mock(TranslationService.class);
        doAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]).when(translationService).format(anyString());
        return translationService;
    }

}