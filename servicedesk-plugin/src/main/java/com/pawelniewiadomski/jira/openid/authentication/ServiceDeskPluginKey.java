package com.pawelniewiadomski.jira.openid.authentication;

import org.springframework.stereotype.Component;

@Component
public class ServiceDeskPluginKey implements PluginKey {

	public static final String KEY = "com.pawelniewiadomski.jira.servicedesk-openid-authentication-plugin";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String configurationContext() {
		return "jira-servicedesk-configuration";
	}

	public String getRestKey() {
		return "easy-sign-ups";
	}

	@Override
	public String getCallbackPath() {
		return "/easy-sign-ups/oauth2-callback";
	}
}
