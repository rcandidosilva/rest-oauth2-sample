package conference.config;

import conference.oauth.UserApprovalHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class OAuth2ServerConfig {

	private static final String RESOURCE_ID = "conference";

	@Configuration
	@Order(10)
	protected static class UiResourceConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
                 .requestMatchers().antMatchers("/rest/**")
			.and()
                .authorizeRequests()
                    .antMatchers("/rest/speakers").access("hasRole('ROLE_USER')")
                    .antMatchers("/rest/test").access("hasRole('ROLE_USER')")
                    .antMatchers("/rest/trusted/**").access("hasRole('ROLE_USER')");
		}
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_ID);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http
				.requestMatchers().antMatchers("/rest/**", "/oauth/users/**", "/oauth/clients/**")
			.and()
				.authorizeRequests()
					.antMatchers("/rest/speakers").access("#oauth2.hasScope('read')")
                    .antMatchers("/rest/test").access("#oauth2.hasScope('read')")
					.antMatchers("/rest/trusted/**").access("#oauth2.hasScope('trust')")
					.regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER')" +
                                " or #oauth2.isClient()) and #oauth2.hasScope('write')")
					.regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() " +
                                "and #oauth2.hasScope('read')");
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private TokenStore tokenStore;

		@Autowired
		private org.springframework.security.oauth2.provider.approval.UserApprovalHandler userApprovalHandler;

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Value("${redirect:http://localhost:8080/client/conference/redirect}")
		private String redirectUri;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("client")
			 			.resourceIds(RESOURCE_ID)
			 			.authorizedGrantTypes("authorization_code", "implicit")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
			 			.secret("secret")
			 		.and()
			 		.withClient("client-with-redirect")
			 			.resourceIds(RESOURCE_ID)
			 			.authorizedGrantTypes("authorization_code", "implicit")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
			 			.secret("secret")
			 			.redirectUris(redirectUri)
			 		.and()
		 		    .withClient("my-client-with-registered-redirect")
	 			        .resourceIds(RESOURCE_ID)
	 			        .authorizedGrantTypes("authorization_code", "client_credentials")
	 			        .authorities("ROLE_CLIENT")
	 			        .scopes("read", "trust")
	 			        .redirectUris("http://anywhere?key=value")
		 		    .and()
	 		        .withClient("my-trusted-client")
 			            .authorizedGrantTypes("password", "authorization_code",
                                "refresh_token", "implicit")
 			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
 			            .scopes("read", "write", "trust")
 			            .accessTokenValiditySeconds(60)
		 		    .and()
	 		        .withClient("my-trusted-client-with-secret")
 			            .authorizedGrantTypes("password", "authorization_code",
                                "refresh_token", "implicit")
 			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
 			            .scopes("read", "write", "trust")
 			            .secret("somesecret")
	 		        .and()
 		            .withClient("my-less-trusted-client")
			            .authorizedGrantTypes("authorization_code", "implicit")
			            .authorities("ROLE_CLIENT")
			            .scopes("read", "write", "trust")
     		        .and()
		            .withClient("my-less-trusted-autoapprove-client")
		                .authorizedGrantTypes("implicit")
		                .authorities("ROLE_CLIENT")
		                .scopes("read", "write", "trust")
		                .autoApprove(true);
		}

		@Bean
		public TokenStore tokenStore() {
			return new InMemoryTokenStore();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
					.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.realm("conference/client");
		}

	}
	
	protected static class Stuff {
	
		@Autowired
		private ClientDetailsService clientDetailsService;

		@Autowired
		private TokenStore tokenStore;

		@Bean
		public ApprovalStore approvalStore() throws Exception {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}

		@Bean
		@Lazy
		@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
		public UserApprovalHandler userApprovalHandler() throws Exception {
			UserApprovalHandler handler = new UserApprovalHandler();
			handler.setApprovalStore(approvalStore());
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
			handler.setClientDetailsService(clientDetailsService);
			handler.setUseApprovalStore(true);
			return handler;
		}
	}

}
