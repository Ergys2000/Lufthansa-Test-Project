package com.ergys2000.RestService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityFilterTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Secures admin supervisor and user paths")
	public void shouldReturnNotAuthorized() throws Exception {
		this.mockMvc.perform(get("/admin/0")).andDo(print()).andExpect(status().is4xxClientError());
		this.mockMvc.perform(get("/supervisor/0")).andDo(print()).andExpect(status().is4xxClientError());
		this.mockMvc.perform(get("/user/0")).andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("Does not secure the public paths")
	public void shouldReturnValidResponse() throws Exception {
		this.mockMvc.perform(get("/swagger-ui.html")).andDo(print()).andExpect(status().isOk());
	}
}
