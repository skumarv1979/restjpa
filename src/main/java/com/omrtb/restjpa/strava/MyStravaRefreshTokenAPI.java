package com.omrtb.restjpa.strava;

import javastrava.auth.ref.AuthorisationScope;
import javastrava.service.exception.BadRequestException;
import javastrava.service.exception.UnauthorizedException;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface MyStravaRefreshTokenAPI {

	/**
	 * @see javastrava.auth.AuthorisationService#tokenExchange(java.lang.Integer, java.lang.String, java.lang.String, AuthorisationScope...)
	 * 
	 * @param clientId
	 *            application's ID, obtained during registration
	 * @param clientSecret
	 *            application's secret, obtained during registration
	 * @param code
	 *            authorisation code
	 * @return Returns an access_token and a detailed representation of the current athlete.
	 * @throws BadRequestException Where the request does not contain all the required information
	 * @throws UnauthorizedException If client secret is invalid
	 */
	@FormUrlEncoded
	@POST("/oauth/token")
	public MyStravaTokenResponse tokenExchangeMyStravaRefresh(@Field("client_id") final Integer clientId, 
			@Field("client_secret") final String clientSecret, 
			@Field("grant_type") final String grantType,
			@Field("refresh_token") final String refreshToken) throws BadRequestException, UnauthorizedException;
}
