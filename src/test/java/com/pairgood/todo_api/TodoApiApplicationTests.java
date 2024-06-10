package com.pairgood.todo_api;

import com.pairgood.todo_api.todo.ResponseTodoList;
import com.pairgood.todo_api.todo.Todo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoApiApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {

    }

    @Test
    void shouldCreateTodo() {
        Todo todo = new Todo(0, "Spring Testing Session", "Spring Testing", false, LocalDate.now(), null, null);
        ResponseEntity<ResponseTodoList> response = testRestTemplate.postForEntity("http://localhost:" + port + "/api/v1/todo/additem", todo, ResponseTodoList.class);

        response.getStatusCode();
        assertEquals(201, response.getStatusCode().value());
        ResponseTodoList body = response.getBody();
        assertNotNull(body);
        assertEquals("Item added to todo list", body.getMessage());
        assertEquals(201, body.getCode());
        assertEquals(HttpStatus.CREATED, body.getHttpStatus());
    }

    @Test
    void shouldGetToDoList() {

        Todo todo = new Todo(0, "Spring Testing Session", "Spring Testing", false, LocalDate.now(), null, null);
        Todo todoTwo = new Todo(0, "Spring Testing Session two", "Spring Testing two", false, LocalDate.now(), null, null);
        ResponseEntity<ResponseTodoList> response = testRestTemplate.postForEntity("http://localhost:" + port + "/api/v1/todo/additem", todo, ResponseTodoList.class);
        ResponseEntity<ResponseTodoList> response2 = testRestTemplate.postForEntity("http://localhost:" + port + "/api/v1/todo/additem", todoTwo, ResponseTodoList.class);

        List<LinkedHashMap> responseList = testRestTemplate.getForObject("http://localhost:" + port + "/api/v1/todo/todolist", List.class);
        assertNotNull(responseList);

        int size = responseList.size();
        Assertions.assertTrue(size >= 2);

        assertEquals(1110, responseList.get(0).get("todoId"));
    }

}
