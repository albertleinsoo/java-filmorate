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
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FilmControllerTest {

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
    void TestExceptionWhenPostFailDataOfFilm(String exception,  int err1, int err2, Film film) {
        String jsonFilm = gson.toJson(film);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "films/"))
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
    @ParameterizedTest(name = "{index} Test:  {0}. Should be code: {1}")
    void Test2ExceptionWhenPutFailDataOfFilm(String exception,  int err1, int err2, Film film) {
        String jsonFilm = gson.toJson(film);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "films/"))
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

    private static Stream<Arguments> MethodSource1() {
        return Stream.of(
                Arguments.of("InvalidName", 400, 500, Film.builder()
                        .id(0)
                        .name("")
                        .description("film description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(100)
                        .build()),
                Arguments.of("InvalidDescriptionMoreThen200Symbols", 400, 500, Film.builder()
                        .id(0)
                        .name("film")
                        .description("asdqeaasdfqedfafasdqwegasdfadfsqeqdfasdfdfgqweefadfadfqerqwerasdfadsfagasdfasd" +
                                "qwerasdfasdfqwerasdfasdfdsfwerqwsadfadsfejhlsdfffhjkhgjdhfgfghrtydffghdafgdfgsdfgsd" +
                                "qsdlpkskvasldkfjkvjgfskhfikhsdfadsfqerdsfersdfgfghdfghrtwrtyfghjafdafasdfasdfrwerui" +
                                "asdfasdfwerfjkrtyasdfgnnmtgnsfdgbafgrtjtysgbghfjbafdgvdfghjnmsfgvfhnsdfytbtrynsngfh")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(100)
                        .build()),
                Arguments.of("InvalidFilmReleaseDateException", 400, 500, Film.builder()
                        .id(0)
                        .name("film")
                        .description("film description")
                        .releaseDate(LocalDate.of(1800, 1, 1))
                        .duration(100)
                        .build()),
                Arguments.of("InvalidDurationException", 400, 500, Film.builder()
                        .id(0)
                        .name("film")
                        .description("film description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(-1)
                        .build()));
    }

}
