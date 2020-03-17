package com.omrtb.restjpa.strava;

import javastrava.api.API;
import javastrava.api.util.RetrofitClientResponseInterceptor;
import javastrava.api.util.RetrofitErrorHandler;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.config.StravaConfig;
import javastrava.json.impl.JsonUtilImpl;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MyStravaAPI extends API {

	/**
	 * Construct an API instance with a given token
	 *
	 * @param token
	 *            The access token to be used with calls to the API
	 */
	public MyStravaAPI(final Token token) {
		super(token);
	}

	/**
	 * Construct an API with a previously know token value
	 *
	 * @param tokenValue
	 *            String value of the token
	 * @param scopes
	 *            Authorisation scopes granted to the token
	 */
	public MyStravaAPI(final String tokenValue, AuthorisationScope... scopes) {
		super(tokenValue, scopes);
	}

	/**
	 * Instance of authorisation API which is used for token exchange
	 */
	private static MyStravaAuthorisationAPI authorisationAPI;
	private static MyStravaRefreshTokenAPI authorisationAPIRefresh;
	/**
	 * <p>
	 * Get an instance of the authorisation API (cached)
	 * </p>
	 *
	 * @return Instance of the authorisation API
	 */
	public static MyStravaAuthorisationAPI myAuthorisationInstance() {
		if (authorisationAPI == null) {
			authorisationAPI = new RestAdapter.Builder().setClient(new RetrofitClientResponseInterceptor()).setConverter(new GsonConverter(new JsonUtilImpl().getGson()))
					.setLogLevel(API.logLevel(AuthorisationServiceImpl.class)).setEndpoint(StravaConfig.AUTH_ENDPOINT).setErrorHandler(new RetrofitErrorHandler()).build()
					.create(MyStravaAuthorisationAPI.class);
		}
		return authorisationAPI;
	}
	/**
	 * <p>
	 * Get an instance of the authorisation API (cached)
	 * </p>
	 *
	 * @return Instance of the authorisation API
	 */
	public static MyStravaRefreshTokenAPI refreshAuthorisationInstance() {
		if (authorisationAPIRefresh == null) {
			authorisationAPIRefresh = new RestAdapter.Builder().setClient(new RetrofitClientResponseInterceptor()).setConverter(new GsonConverter(new JsonUtilImpl().getGson()))
					.setLogLevel(API.logLevel(AuthorisationServiceImpl.class)).setEndpoint(StravaConfig.AUTH_ENDPOINT).setErrorHandler(new RetrofitErrorHandler()).build()
					.create(MyStravaRefreshTokenAPI.class);
		}
		return authorisationAPIRefresh;
	}
}
