import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsApi extends BaseApi {

    @Step("Получение списка ингредиентов")
    public Response getIngredientsList() {
        return given()
                .spec(getBaseSpecification())
                .get(Constants.INGREDIENTS_ENDPOINT);
    }

}