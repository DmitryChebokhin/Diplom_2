import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class UserCreateTest {
    UserSetGet user;
    UserApi userClient;
    String authToken;



    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        userClient = new UserApi();
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void CreateUserWithCorrectData(){
        Response response = userClient.createNewUser(user);

        assertThat("Ответ не содержит параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Ответ не содержит параметр токена авторизации", response.path("accessToken"), notNullValue());
        assertThat("Ответ содержит неверно сгенерированный токен", response.path("accessToken"), containsString("Bearer"));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован в системе")
    public void CreateAlreadyRegisteredUser(){
        userClient.createNewUser(user);
        Response responseSecondRequest = userClient.createNewUser(user);
        assertThat("Ответ не содержит параметр success со значением false", responseSecondRequest.path("success"), equalTo(false));
        assertThat("Ответ от сервера отличный от ожидаемого 403 forbidden", responseSecondRequest.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ответ не содержит параметр message", responseSecondRequest.path("message"), equalTo(Constants.DOUBLE_USER_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания email")
    public void CreateUserWithoutEmail(){
        user.setEmail(null);
        Response response = userClient.createNewUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ответа не содержит параметр message", response.path("message"), equalTo(Constants.REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания пароля")
    public void CreateUserWithoutPassword(){
        user.setPassword(null);
        Response response = userClient.createNewUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ответа не содержит параметр message", response.path("message"), equalTo(Constants.REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания имени")
    public void CreateUserWithoutName(){
        user.setName(null);
        Response response = userClient.createNewUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ответа не содержит параметр message", response.path("message"), equalTo(Constants.REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }
}