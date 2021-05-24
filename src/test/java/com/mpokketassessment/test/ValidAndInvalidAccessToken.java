package com.mpokketassessment.test;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.mpokketassessment.base.AccessTokens;
import com.mpokketassessment.base.EndPoint;
import com.mpokketassessment.base.UserPojoClass;

import static io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;


public class ValidAndInvalidAccessToken {

	@Test
	public void ValidTokenTest() {
		//Testing the token url for valid access token
		given()
		.contentType(ContentType.JSON)
		.baseUri("https://gorest.co.in")
		.when()
		.get("/public-api/users?access-token="+AccessTokens.VALID_TOKEN)
		.then()
		.assertThat().statusCode(200)
		.log().all();
		System.out.println("meta_message: Ok");

		//Testing the token url for invalid access token
		given()
		.contentType(ContentType.JSON)
		.baseUri("https://gorest.co.in")
		.when()
		.get("/public-api/users?access-token="+AccessTokens.INVALID_TOKEN)
		.then()
		.assertThat().statusCode(200)
		.and()
		.log().all();
		System.out.println("meta_message: Authentication failed");
		
		//Creating new user with first name and lastname
		UserPojoClass obj = new UserPojoClass("Prudvish Lavu", "ursprudvish@gmail.com", "Male", "Active");
		Response response = given()
				.contentType(ContentType.JSON)
				.header(new Header("Authorization", "Bearer "+AccessTokens.VALID_TOKEN))
				.body(obj)
				.baseUri("https://gorest.co.in")
				.when()
				.post(EndPoint.CREATE_NEW_USER);
		response.then().log().all();
		String userId = response.jsonPath().get("data.id");

		//Validating the user id after user creation
		given()
		.baseUri("https://gorest.co.in")
		.contentType(ContentType.JSON)
		.pathParam("userid", userId)
		.when()
		.get(EndPoint.GET_SINGLE_USER)
		.then()
			.assertThat().body("id", Matchers.equalTo(userId))
		.log().all();

	}

	


}
