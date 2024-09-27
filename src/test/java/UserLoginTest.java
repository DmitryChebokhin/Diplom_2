import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginTest {
    UserSetGet user;
    UserApi userClient;
    String authToken;


    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        userClient = new UserApi();
        userClient.createNewUser(user);
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Авторизация пользователя с корректными логином и паролем")
    public void AuthorizeWithCorrectLoginAndPassword() {
        Response response = userClient.loginUser(user);
        assertThat("Ответ не содержит параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Ответ не содержит параметр токена авторизации", response.path("accessToken"), notNullValue());
        assertThat("Ответ содержит неверно сгенерированный токен", response.path("accessToken"), containsString("Bearer"));
        assertThat("Ответ содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
        assertThat("Ответ содержит неверное имя", response.path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректными логином и паролем")
    public void AuthorizeWithIncorrectLoginAndPassword() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.loginUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ содержит код отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректным логином и корректным паролем")
    public void AuthorizeWithIncorrectLoginAndCorrectPassword() {
        user.setEmail(user.getEmail() + "somerandomletters");
        Response response = userClient.loginUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ содержит код отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Авторизация пользователя с корректным логином и некорректным паролем")
    public void AuthorizeWithCorrectLoginAndIncorrectPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.loginUser(user);
        assertThat("Ответ не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ответ содержит код отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }
}