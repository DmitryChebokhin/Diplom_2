import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class OrderGetTest {

    final IngredientsApi ingredientsApi = new IngredientsApi();
    UserSetGet user;
    OrderApi orderClient;
    UserApi userClient;
    String authToken;
    String correctIngredients;



    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        orderClient = new OrderApi();
        userClient = new UserApi();
        setUpIngredients();
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
    public void getOrdersFromUserWithAuth(){
        Response response = orderClient.getUserOrdersListWithToken(authToken.substring(7));
        assertThat("Ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
        assertThat("Вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getOrdersFromUserWithoutAuth(){
        Response response = orderClient.getUserOrdersListWithoutToken();
        assertThat("Ответ содержит номер заказа", response.path("order.number"), nullValue());
        assertThat("Ответ не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.UNAUTHORIZED_ERROR_MESSAGE));
    }

    private void setUpIngredients() {
        Response ingredientsResponse = ingredientsApi.getIngredientsList();
        if (Objects.nonNull(ingredientsResponse)) {
            var ingredientIds = ingredientsResponse.jsonPath().getList("data._id", String.class);

            correctIngredients = String.format("{\n\"ingredients\": %s \n}",
                    ingredientIds.stream()
                            .map(id -> "\"" + id + "\"") // оборачиваем каждый id в кавычки
                            .collect(Collectors.joining(", ", "[", "]"))); // объединяем с разделителем и в квадратных скобках
        }
    }
}