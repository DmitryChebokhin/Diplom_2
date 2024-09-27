import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class OrderGetTest {
    UserSetGet user;
    OrderApi orderClient;
    UserApi userClient;
    String authToken;
    String correctIngredients = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa70\"]\n}";



    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        orderClient = new OrderApi();
        userClient = new UserApi();

        Response createUserResponse = userClient.createNewUser(user);
        authToken = createUserResponse.path("accessToken");
        orderClient.createOrderWithToken(authToken.substring(7), correctIngredients);
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void GetOrdersFromUserWithAuth(){
        Response response = orderClient.getUserOrdersListWithToken(authToken.substring(7));
        assertThat("Ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
        assertThat("Вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void GetOrdersFromUserWithoutAuth(){
        Response response = orderClient.getUserOrdersListWithoutToken();
        assertThat("Ответ содержит номер заказа", response.path("order.number"), nullValue());
        assertThat("Ответ не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }
}