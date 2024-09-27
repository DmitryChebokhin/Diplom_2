import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserApi extends BaseApi {
    @Step("Создание нового пользователя")
    public Response createNewUser( UserSetGet user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .post(Constants.USER_CREATE_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .delete(Constants.USER_DATA_ENDPOINT);
    }

    @Step("Вход пользователя")
    public Response loginUser(UserSetGet user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .post(Constants.USER_LOGIN_ENDPOINT);
    }

    @Step("Изменение данных о пользователе с токеном")
    public Response changeUserDataWithToken(String token, UserSetGet user) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .body(user)
                .patch(Constants.USER_DATA_ENDPOINT);
    }

    @Step("Изменение данных о пользователе без токена")
    public Response changeUserDataWithoutToken(UserSetGet user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .patch(Constants.USER_DATA_ENDPOINT);
    }

}