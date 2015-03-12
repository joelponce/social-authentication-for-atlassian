package com.pawelniewiadomski.jira.openid.authentication.providers;

import com.atlassian.fugue.Either;
import com.atlassian.jira.util.lang.Pair;
import com.atlassian.sal.api.message.I18nResolver;
import com.pawelniewiadomski.jira.openid.authentication.activeobjects.OpenIdDao;
import com.pawelniewiadomski.jira.openid.authentication.activeobjects.OpenIdProvider;
import com.pawelniewiadomski.jira.openid.authentication.openid.VkConnectResponse;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.pawelniewiadomski.jira.openid.authentication.OpenIdConnectReturnToHelper.getReturnTo;

public class VkProviderType extends AbstractOAuth2ProviderType {

    public VkProviderType(I18nResolver i18nResolver, OpenIdDao openIdDao) {
        super(i18nResolver, openIdDao);
    }

    @Nonnull
    @Override
    public String getAuthorizationUrl() {
        return "https://oauth.vk.com/authorize";
    }

    @Nonnull
    @Override
    public String getCallbackId() {
        return "vk";
    }

    @Nonnull
    @Override
    public String getId() {
        return OpenIdProvider.VK_TYPE;
    }

    @Nonnull
    @Override
    public String getName() {
        return i18nResolver.getText("openid.provider.type.vk");
    }

    @Override
    public OAuthClientRequest createOAuthRequest(@Nonnull OpenIdProvider provider,
                                                 @Nonnull String state,
                                                 @Nonnull HttpServletRequest request) throws Exception {
        return OAuthClientRequest
                .authorizationLocation(getAuthorizationUrl())
                .setClientId(provider.getClientId())
                .setResponseType(ResponseType.CODE.toString())
                .setState(state)
                .setScope("email")
                .setRedirectURI(getReturnTo(provider, request))
                .buildQueryMessage();
    }

    @Override
    public Either<Pair<String, String>, String> getUsernameAndEmail(@Nonnull String authorizationCode, @Nonnull OpenIdProvider provider, HttpServletRequest request) throws Exception {
        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        final OAuthClientRequest oAuthRequest = OAuthClientRequest.tokenLocation("https://oauth.vk.com/access_token")
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(provider.getClientId())
                .setClientSecret(provider.getClientSecret())
                .setRedirectURI(getReturnTo(provider, request))
                .setCode(authorizationCode)
                .buildQueryMessage();

        final VkConnectResponse token = oAuthClient.accessToken(oAuthRequest, VkConnectResponse.class);
        final String accessToken = token.getAccessToken();

        final OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest("https://api.vk.com/method/users.get")
                .setAccessToken(accessToken)
                .buildQueryMessage();

        final OAuthResourceResponse userInfoResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        final Map<String, Object> userInfo = JSONUtils.parseJSON(userInfoResponse.getBody());

        final Object[] response = (Object[]) userInfo.get("response");
        final JSONObject profile = (JSONObject) response[0];
        return Either.left(Pair.of(profile.get("first_name") + " " + profile.get("last_name"), token.getEmail()));
    }

}
