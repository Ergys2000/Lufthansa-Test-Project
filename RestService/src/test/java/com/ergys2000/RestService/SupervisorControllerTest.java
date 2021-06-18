package com.ergys2000.RestService;


import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Admin controller response formats")
public class SupervisorControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@DisplayName("Admin should return proper response format")
	public void should_return_proper_response_format_error() throws Exception {
		this.mockMvc.perform(get("/supervisor/0")).andDo(print())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status", is("ERROR")))
			.andExpect(jsonPath("$", hasKey("result")))
			.andExpect(jsonPath("$", hasKey("status")))
			.andExpect(jsonPath("$", hasKey("message")));
	}
	
	@Test
	@DisplayName("Admin should return proper response format")
	public void should_return_proper_response_format_ok() throws Exception {
		this.mockMvc.perform(get("/supervisor/27")).andDo(print())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status", is("OK")))
			.andExpect(jsonPath("$", hasKey("result")))
			.andExpect(jsonPath("$", hasKey("status")))
			.andExpect(jsonPath("$", hasKey("message")));
	}
	
	@Test
	@DisplayName("Should return proper list of users")
	public void return_list_of_users() throws Exception {
		this.mockMvc.perform(get("/supervisor/27/requests")).andDo(print())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status", is("OK")))
			.andExpect(jsonPath("$", hasKey("result")))
			.andExpect(jsonPath("$", hasKey("status")))
			.andExpect(jsonPath("$", hasKey("message")));
	}
}
