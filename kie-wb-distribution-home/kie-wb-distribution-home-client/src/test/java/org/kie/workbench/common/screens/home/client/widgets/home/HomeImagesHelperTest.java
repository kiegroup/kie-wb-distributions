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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HomeImagesHelper.class })
public class HomeImagesHelperTest {

    private static final String EN_US = "en_US";
    private static final String JA_JP = "ja_JP";
    private static final String PT_BR = "pt_BR";
    private static final String ZH_CN = "zh_CN";
    private static final String ZH_TW = "zh_TW";

    private String[] availableLocaleNames = new String[]{ EN_US, JA_JP, ZH_CN, ZH_TW };

    @Before
    public void setup() {
        PowerMockito.mockStatic( HomeImagesHelper.class );
        PowerMockito.when( HomeImagesHelper.getAvailableLocaleNames() ).thenReturn( availableLocaleNames );
    }

    @Test
    public void testNoLocale() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( "" );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/06_Analyze_Graphic.png",
                      url );
    }

    @Test
    public void testDefaultLocale() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( "default" );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/06_Analyze_Graphic.png",
                      url );
    }

    @Test
    public void testAvailableLocaleEN_US() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( EN_US );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/06_Analyze_Graphic.png",
                      url );
    }

    @Test
    public void testAvailableLocaleJA_JP() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( JA_JP );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/ja/06_Analyze_Graphic-ja.png",
                      url );
    }

    @Test
    public void testAvailableLocaleZh_CN() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( ZH_CN );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/zh_cn/06_Analyze_Graphic-zh_cn.png",
                url );
    }

    @Test
    public void testAvailableLocaleZh_TW() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( ZH_TW );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/zh_tw/06_Analyze_Graphic-zh_tw.png",
                url );
    }

    @Test
    public void testUnavailableLocale() {
        PowerMockito.when( HomeImagesHelper.getLocaleName() ).thenReturn( PT_BR );
        final String url = HomeImagesHelper.Images.Analyze.getLocalisedImageUrl();
        assertEquals( "images/home/06_Analyze_Graphic.png",
                      url );
    }

}
