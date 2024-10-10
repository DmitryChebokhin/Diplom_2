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

public class OrderCreateTest {

    String correctIngredients;
    String emptyIngredients = "{\n\"ingredients\": []\n}";
    String wrongIngredients = "{\n\"ingredients\": [\"somerandomletters1\",\"somerandomletters2\"]\n}";

    final IngredientsApi ingredientsApi = new IngredientsApi();
    UserSetGet user;
    OrderApi orderClient;
    UserApi userClient;
    String authToken;

    @Before
    public void setUp() {
        user = UserSetGet.generateUser();
        orderClient = new OrderApi();
        userClient = new UserApi();
        setUpIngredients();
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
    @DisplayName("Создание заказа с авторизацией и списком ингредиентов")
    public void createOrderWithAuthAndWithIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), correctIngredients);
        assertThat("Ответ не содержит номер заказа", response.path("order.number"), notNullValue());
        assertThat("Ответ не содержит параметра success со значением true", response.path("success"), equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и со списком ингредиентов")
    public void createOrderWithoutAuthAndWithIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(correctIngredients);
        assertThat("Ответа не содержит номер заказа", response.path("order.number"), notNullValue());
        assertThat("Ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без списка ингредиентов")
    public void createOrderWithAuthAndWithoutIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), emptyIngredients);
        assertThat("Вернулся код ответа, отличный от ожидаемого 400 bad request", response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat("Ответ не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.INGREDIENTS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без списка ингредиентов")
    public void createOrderWithoutAuthAndWithoutIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(emptyIngredients);
        assertThat("Вернулся код ответа, отличный от ожидаемого 400 bad request", response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat("Ответ не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Вернулось сообщение, не соответствующее ожидаемому", response.path("message"), equalTo(Constants.INGREDIENTS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хэшем ингредиентов")
    public void createOrderWithAuthAndWrongIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), wrongIngredients);
        assertThat("Вернулся код ответа, отличный от ожидаемого 500 internal server error", response.statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хэшем ингредиентов")
    public void createOrderWithoutAuthAndWrongIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(wrongIngredients);
        assertThat("Вернулся код ответа, отличный от ожидаемого 500 internal server error", response.statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
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