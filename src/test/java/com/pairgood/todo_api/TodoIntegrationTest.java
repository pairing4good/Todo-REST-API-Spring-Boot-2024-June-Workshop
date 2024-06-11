package com.pairgood.todo_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pairgood.todo_api.config.LoggingConfiguration;
import com.pairgood.todo_api.todo.Todo;
import com.pairgood.todo_api.todo.TodoController;
import com.pairgood.todo_api.todo.TodoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TodoController.class)
@Import({LoggingConfiguration.class})
public class TodoIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoService service;
    @MockBean
    private Logger logger;
    @Autowired
    private ObjectMapper mapper;
    @Test
    void shouldCreateTodo_WhenTodoSuccessfullySaved() throws Exception{
        long savedId = 1L;
        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);
        when(service.AddItemToThelist(any(Todo.class))).thenReturn(savedId);
        this.mockMvc.perform(post("/api/v1/todo/additem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("{" +
                        "\"message\":\"Item added to todo list\"," +
                        "\"code\":201," +
                        "\"httpStatus\":\"CREATED\"" +
                        "}"));

        verify(logger).info("Item added to todo list. code: 201");
    }

    @Test
    void shouldNotCreateTodo_WhenTodoUnsuccessfullySaved() throws Exception {
        long unsavedIndicator = 0L;

        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.AddItemToThelist(any(Todo.class))).thenReturn(unsavedIndicator);

        this.mockMvc.perform(post("/api/v1/todo/additem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print())


                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"message\":\"Item Not added to todo list\"," +
                        "\"code\":400," +
                        "\"httpStatus\":\"BAD_REQUEST\"" +
                        "}"));

        verify(logger).info("Item Not added to todo list. code: 400");
    }

    @Test
    void shouldHandleEntityNotFound() throws Exception {
        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);
        when(service.AddItemToThelist(any(Todo.class))).thenThrow(new EntityNotFoundException("test message"));

        this.mockMvc.perform(post("/api/v1/todo/additem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))

                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus", org.hamcrest.Matchers.is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("test message")));
    }

}

