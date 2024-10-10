import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class UserUpdateDataTest {
    UserSetGet user;
    UserApi userClient;
    String authToken;




    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        userClient = new UserApi();
        Response createUserResponse = userClient.createNewUser(user);
        authToken = createUserResponse.path("accessToken");
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Изменение всех данных пользователя с авторизацией")
    public void changeAuthorizedUserData() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        user.setName(user.getName() + "somerandomletters");
        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);
        assertThat("В ответе отсутствует параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Код ответа отличается от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ответ содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
        assertThat("Ответ содержит неверное имя", response.path("user.name"), equalTo(user.getName()));

    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void changeAuthorizedUserEmail() {
        user.setEmail(user.getEmail() + "somerandomletters");
        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);
        assertThat("В ответе отсутствует параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Код ответа отличается от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ответ содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    public void changeAuthorizedUserPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);
        assertThat("В ответе отсутствует параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Код ответа отличается от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ответ содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void changeAuthorizedUserName() {
        user.setName(user.getName() + "somerandomletters");
        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);
        assertThat("В ответе отсутствует параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Код ответа отличается от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ответ содержит неверное имя", response.path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение всех данных пользователя без авторизации")
    public void changeUnauthorizedUserData() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        user.setName(user.getName() + "somerandomletters");
        Response response = userClient.changeUserDataWithoutToken(user);
        assertThat("В ответе отсутствует параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Код ответа отличается от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ответ содержит текст ошибки, отличный от ожидаемого", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    public void changeUnauthorizedUserEmail() {
        user.setEmail(user.getEmail() + "somerandomletters");
        Response response = userClient.changeUserDataWithoutToken(user);
        assertThat("В ответе отсутствует параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Код ответа отличается от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ответ содержит текст ошибки, отличный от ожидаемого", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    public void changeUnauthorizedUserPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.changeUserDataWithoutToken(user);
        assertThat("В ответе отсутствует параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Код ответа отличается от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ответ содержит текст ошибки, отличный от ожидаемого", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void changeUnauthorizedUserName() {
        user.setName(user.getName() + "somerandomletters");
        Response response = userClient.changeUserDataWithoutToken(user);
        assertThat("В ответе отсутствует параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ответ содержит текст ошибки, отличный от ожидаемого", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение email на уже существующий в системе")
    public void changeEmailOnAlreadyExisting() {
        UserSetGet userTwo = UserSetGet.generateUser();
        userClient.createNewUser(userTwo);
        user.setEmail(userTwo.getEmail());
        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);
        assertThat("В ответе отсутствует параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Код ответа, отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ответ содержит текст ошибки, отличный от ожидаемого", response.path("message"), equalTo(Constants.DOUBLE_EMAIL_ERROR_MESSAGE));

    }
}