package it;

import com.atlassian.jira.pageobjects.BaseJiraWebTest;
import it.pageobjects.ConfigurationPage;
import it.pageobjects.EditProviderPage;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.pageobjects.elements.query.Poller.waitUntilFalse;
import static com.atlassian.pageobjects.elements.query.Poller.waitUntilTrue;

public class TestEditProvider extends BaseJiraWebTest {

    private ConfigurationPage configurationPage;

    @Before
    public void setUp() {
        jira.backdoor().restoreBlankInstance();
        jira.backdoor().project().addProject("Test", "TST", "admin");

        configurationPage = jira.gotoLoginPage().loginAsSysAdmin(ConfigurationPage.class);
    }

    @Test
    public void editingBuiltInProviderShouldSucceed()
    {
        EditProviderPage editPage = configurationPage.editProvider("Google");
        waitUntilFalse(editPage.isEndpointUrlVisible());
        waitUntilFalse(editPage.isExtensionNamespaceVisible());
        waitUntilFalse(editPage.isNameVisible());
        waitUntilTrue(editPage.isAllowedDomainsVisible());
        editPage.save();

        // saving should cause no errors and should not display any additional fields
        waitUntilFalse(editPage.isEndpointUrlVisible());
        waitUntilFalse(editPage.isExtensionNamespaceVisible());
        waitUntilFalse(editPage.isNameVisible());
    }
}
