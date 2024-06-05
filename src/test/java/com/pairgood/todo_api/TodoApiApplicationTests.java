package com.pairgood.todo_api;

import com.pairgood.todo_api.todo.ResponseTodoList;
import com.pairgood.todo_api.todo.Todo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoApiApplicationTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	@LocalServerPort
	private int port;

	@Test
	void contextLoads(){

	}
	@Test
	void shouldCreateTodo(){
		Todo todo= new Todo(0,"Spring Testing Session","Spring Testing",false, LocalDate.now(),null,null);
		ResponseEntity<ResponseTodoList> response=testRestTemplate.postForEntity("http://localhost:"+port+"/api/v1/todo/additem",todo, ResponseTodoList.class);

		response.getStatusCode();
		Assertions.assertEquals(201,response.getStatusCode().value());



	}


}
