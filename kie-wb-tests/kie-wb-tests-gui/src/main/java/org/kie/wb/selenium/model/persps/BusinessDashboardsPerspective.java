
package org.kie.wb.selenium.model.persps;

import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;

public class BusinessDashboardsPerspective extends AbstractPerspective {

    private static final By CONTENT_MANAGER_TITLE = By.cssSelector("[title='Content Manager']");

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(CONTENT_MANAGER_TITLE);
    }
}
