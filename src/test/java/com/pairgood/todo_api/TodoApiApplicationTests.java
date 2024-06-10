package com.pairgood.todo_api;

import com.pairgood.todo_api.todo.ResponseTodoList;
import com.pairgood.todo_api.todo.Todo;
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

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoApiApplicationTests {
    int counter = 0;
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

        createTodo();

        List<LinkedHashMap> responseList = testRestTemplate.getForObject("http://localhost:" + port +
                "/api/v1/todo/todolist", List.class);

        assertNotNull(responseList);
        assertTrue(responseList.size() > 0);
        assertEquals(1110, responseList.get(0).get("todoId"));
    }

    private void createTodo() {
        counter += 1;
        Todo todo = new Todo(0, "Spring Testing Session" + counter, "Spring Testing" +
                counter, false, LocalDate.now(), null, null);
        ResponseEntity<ResponseTodoList> response = testRestTemplate.postForEntity("http://localhost:" + port +
                "/api/v1/todo/additem", todo, ResponseTodoList.class);
        assertEquals(201, response.getStatusCode().value());

    }

    @Test
    void shouldUpdateItem() {
        createTodo();
        Todo updatetodo = new Todo(0, "Spring Testing Session updated", "Spring Testing updation", false, LocalDate.now(), null, null);
        testRestTemplate.put("http://localhost:" + port + "/api/v1/todo/updateitem/1110", updatetodo);
        List<LinkedHashMap> responseList = testRestTemplate.getForObject("http://localhost:" + port +
                "/api/v1/todo/todolist", List.class);
        assertEquals(1110, responseList.get(0).get("todoId"));
        assertEquals("Spring Testing Session updated", responseList.get(0).get("todoTitle"));
        assertEquals("Spring Testing updation", responseList.get(0).get("todoDescription"));


    }

    @Test
    void shouldDeleteItem() {
        createTodo();
        long beforeCount = testRestTemplate.getForObject("http://localhost:" + port + "/api/v1/todo/todocount",
                Long.class);
        testRestTemplate.delete("http://localhost:" + port + "/api/v1/todo/deleteitem/1110");
        long afterCount = testRestTemplate.getForObject("http://localhost:" + port + "/api/v1/todo/todocount",
                Long.class);

        assertEquals(beforeCount - 1, afterCount);


    }

}
