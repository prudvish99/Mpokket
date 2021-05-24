package com.mpokketassessment.test;


import static io.restassured.RestAssured.given;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.mpokketassessment.base.AccessTokens;
import com.mpokketassessment.base.EndPoint;
import com.mpokketassessment.base.UserPojoClass;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
/**
 *  
 * @author Lavu Prudvish
 *
 */
public class GetListOfUsersTest {

	@Test
	public void getListofuserTest() {
		//Get all the user list
		String emailId="Colocolo@gmail.com";
		 Response response = given()
			.contentType(ContentType.JSON)
			.baseUri("https://gorest.co.in")
		.when()
			.get(EndPoint.GETUSERS);
		response.then()
			.log().all();
		//capturing the user mail
		ArrayList<String> listUser = response.jsonPath().get("data.email");
		
		int index=0;
		//Getting the user index 
		for(int i=0;i<listUser.size();i++)
		{
			if(listUser.get(i).equals(emailId)) {
				index=i;
				break;
			}
		}
		//getting the user id based on email index
		String userID=response.jsonPath().get("data["+index+"].id");
		
		//Updating user email id
		UserPojoClass userObj = new UserPojoClass("Prudvish Lavu", "assignment@mpokket.com", "Male", "Active");
		given()
			.contentType(ContentType.JSON)
			.body(userObj)
			.header(new Header("Authorization", "Bearer "+AccessTokens.VALID_TOKEN))
			.pathParam("userid", userID)
		.when()
			.patch("/public-api/users/{userid}")
		.then()
		//Validating the email by adding assertion
			.assertThat().body("data.email", Matchers.equalTo(userObj.getEmail()))
		.and()
			.log().all();
		
		//Counting active and inactive users
		 Response res = given()
			.contentType(ContentType.JSON)
			.baseUri("https://gorest.co.in")
		.when()
			.get(EndPoint.CREATE_NEW_USER);
		response.then()
			.log().all();
		int active_user=0;
		int inactive_user=0;
		ArrayList<String> status=res.jsonPath().get("data.status");
		for(int i=0;i<status.size();i++) {
			if(status.get(i).equalsIgnoreCase("Active")) {
				active_user++;
			}
			else if(status.get(i).equalsIgnoreCase("Inactive")) {
				inactive_user++;
			}
		}
		
		System.out.println("\nActive users count: "+active_user);
		System.out.println("\nInActive users count: "+inactive_user);
	}
}
