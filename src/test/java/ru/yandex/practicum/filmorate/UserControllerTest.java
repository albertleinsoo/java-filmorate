package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {
    private final URI uri = URI.create("http://localhost:8080/");
    private HttpClient httpClient;
    private HttpResponse<String> responseBody;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    public void beforeEach() {
        httpClient = HttpClient.newHttpClient();
    }

    @MethodSource("MethodSource1")
    @ParameterizedTest(name = "{index} Test:  {0}. Should give code: {1}")
    void TestExceptionOfPostInvalidUserData(String exception,  int err1, int err2, User user) {
        String jsonFilm = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "users/"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();

        try {
            responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (responseBody.statusCode() != 200) {
                System.out.println("Ошибка. Код: " + responseBody.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Возникла ошибка.\n" +
                    "Сообщение ошибки: " + e.getMessage());
        }
        assertTrue((responseBody.statusCode() == err1) || (responseBody.statusCode() == err2));
    }

    @MethodSource("MethodSource1")
    @ParameterizedTest(name = "{index} Test:  {0}. Should give code: {1}")
    void TestExceptionOfPutIncorrectUserData(String exception,  int err1, int err2, User user) {
        String jsonFilm = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "users/"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();

        try {
            responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (responseBody.statusCode() != 200) {
                System.out.println("Ошибка. Код: " + responseBody.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Возникла ошибка.\n" +
                    "Сообщение ошибки: " + e.getMessage());
        }
        assertTrue((responseBody.statusCode() == err1) || (responseBody.statusCode() == err2));
    }

    private Stream<Arguments> MethodSource1() {
        return Stream.of(
                Arguments.of("InvalidEmailException",  400, 500, User.builder()
                        .id(0)
                        .email("testusertest.ru")
                        .login("user1")
                        .name("user")
                        .birthday(LocalDate.of(1998, 4, 3))
                        .build()),
                Arguments.of("InvalidLoginEmptyException",  400, 500, User.builder()
                        .id(0)
                        .email("test@test.ru")
                        .login("")
                        .name("user")
                        .birthday(LocalDate.of(1998, 4, 3))
                        .build()),
                Arguments.of("InvalidEmailException", 400, 500, User.builder()
                        .id(0)
                        .email("")
                        .login("user1")
                        .name("user")
                        .birthday(LocalDate.of(1990, 1, 1))
                        .build()),
                Arguments.of("InvalidBirthdayException",  400, 500, User.builder()
                        .id(0)
                        .email("test@test.ru")
                        .login("user1")
                        .name("user")
                        .birthday(LocalDate.MAX)
                        .build()));
    }
}
