package com.example.taskmanager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskIntegrationTest {
    @Autowired MockMvc mvc;

    @Test void fullCreateGetUpdatePatchDeleteFlow() throws Exception {
        String created = mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Integration task\",\"description\":\"Flow test\",\"priority\":\"HIGH\",\"dueDate\":\"2026-11-01\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH")).andReturn().getResponse().getContentAsString();
        Number idValue = com.jayway.jsonpath.JsonPath.read(created, "$.id");
        long id = idValue.longValue();

        mvc.perform(get("/api/tasks/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Integration task"));
        mvc.perform(put("/api/tasks/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated task\",\"description\":\"Updated\",\"status\":\"IN_PROGRESS\",\"priority\":\"MEDIUM\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        mvc.perform(patch("/api/tasks/{id}/status", id).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DONE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(delete("/api/tasks/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tasks/{id}", id)).andExpect(status().isNotFound());
    }

    @Test void filtersTasksAndHealthEndpointIsUp() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Filter one\",\"status\":\"TODO\",\"priority\":\"LOW\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/tasks").param("status", "TODO").param("priority", "LOW"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Filter one"));
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
