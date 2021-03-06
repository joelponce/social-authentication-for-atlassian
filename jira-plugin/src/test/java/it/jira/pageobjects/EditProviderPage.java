package it.jira.pageobjects;

public class EditProviderPage extends AddProviderPage {

    private final String pid;

    public EditProviderPage() {
        pid = null;
    }

    public EditProviderPage(String pid) {
        this.pid = pid;
    }

    @Override
    public String getUrl() {
        return "/plugins/servlet/openid-configuration#/edit/" + pid;
    }

    public EditProviderPage saveWithErrors() {
        saveProvider.click();
        return this;
    }
}
