/*
 * MIT License
 *
 * Copyright (c) 2023 CyberAlexander
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cyberalexander.messengerio;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * {@link org.mockito.Mockito#mock(Class)} - allows us to create a mock object of a class or an interface.
 * We can then use the mock to stub return values for its methods and verify if they were called.
 * <p>
 * {@link org.mockito.Mock} annotation is a shorthand for the Mockito.mock() method. It's important to note that
 * we should only use it in a test class. Unlike the mock() method, we need to enable Mockito annotations to
 * use this annotation. We can do this either by using the MockitoJUnitRunner to run the test, or by calling
 * the MockitoAnnotations.initMocks() method explicitly.
 * <p>
 * {@link MockBean} is a Spring Boot's annotation. We can use the @MockBean to add mock objects to the Spring
 * application context. The mock will replace any existing bean of the same type in the application context.
 * If no bean of the same type is defined, a new one will be added. This annotation is useful in integration
 * tests where a particular bean, like an external service, needs to be mocked.
 * <p>
 * Created : 18/03/2023 09:26
 * Project : messenger-io
 * IDE : IntelliJ IDEA
 *
 * @author CyberAlexander
 * @version 1.0
 */
@Slf4j
@ActiveProfiles(value = {"test"})
@WebMvcTest(controllers = {MessageController.class})
class MessageControllerTests {
    private static final EasyRandom R = new EasyRandom();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageSender messageSender;

    @Autowired
    @InjectMocks
    private MessageController controller;

    @Test
    void testControllerInstantiated() {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    @SneakyThrows
    void sendMessage_whenMessageSent() {
        MessageRequest request = R.nextObject(MessageRequest.class);

        Mockito.when(messageSender.sendMessage(request)).thenReturn(Boolean.TRUE);

        mockMvc.perform(post("/api/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Operation executed with the result : [true]"));
    }

    @Test
    @SneakyThrows
    void sendMessage_whenMessageWasNotSent() {
        MessageRequest request = R.nextObject(MessageRequest.class);

        Mockito.when(messageSender.sendMessage(request)).thenReturn(Boolean.FALSE);

        mockMvc.perform(post("/api/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Operation executed with the result : [false]"));
    }
}