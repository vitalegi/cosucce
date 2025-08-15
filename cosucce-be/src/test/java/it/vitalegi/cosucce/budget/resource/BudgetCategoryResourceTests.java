package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.service.BoardCategoryService;
import it.vitalegi.cosucce.budget.service.BudgetAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.UUID;

import static it.vitalegi.cosucce.util.JsonUtil.json;
import static it.vitalegi.cosucce.util.MockAuth.guest;
import static it.vitalegi.cosucce.util.MockAuth.member;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert401;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert403;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert409;
import static it.vitalegi.cosucce.util.MockMvcUtil.getUserId;
import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class BudgetCategoryResourceTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardCategoryService boardCategoryService;
    @MockitoBean
    BudgetAuthorizationService budgetAuthorizationService;

    @Nested
    class AddBoardCategory {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();
            var expected = BoardCategory.builder().boardId(boardId).categoryId(categoryId).label("lab").icon("ico").enabled(true).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardCategoryService.addBoardCategory(any(), any(), any(), any())).thenReturn(expected);

            mockMvc.perform(request(boardId, AddBoardCategoryDto.builder().categoryId(categoryId).label("lab").icon("ico").enabled(true).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.label").value("lab")) //
                    .andExpect(jsonPath("$.icon").value("ico")) //
                    .andExpect(jsonPath("$.enabled").value(true)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardCategoryService, times(1)).addBoardCategory(boardId, categoryId, "lab", "ico");
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request(UUID.randomUUID())).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request(UUID.randomUUID()).with(guest())).andDo(print()));
        }

        protected MockHttpServletRequestBuilder request(UUID boardId) {
            return request(boardId, AddBoardCategoryDto.builder().build());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, AddBoardCategoryDto payload) {
            var request = post("/budget/board/" + boardId + "/category").contentType(MediaType.APPLICATION_JSON).content(json(payload));
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class UpdateBoardCategory {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();
            var expected = BoardCategory.builder().boardId(boardId).categoryId(categoryId).label("lab2").icon("ico2").enabled(true).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardCategoryService.updateBoardCategory(any(), any(), any(), any(), anyBoolean(), anyInt())).thenReturn(expected);

            mockMvc.perform(request(boardId, categoryId, UpdateBoardCategoryDto.builder().label("lab2").icon("ico2").enabled(true).version(5).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.label").value("lab2")) //
                    .andExpect(jsonPath("$.icon").value("ico2")) //
                    .andExpect(jsonPath("$.enabled").value(true)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardCategoryService, times(1)).updateBoardCategory(boardId, categoryId, "lab2", "ico2", true, 5);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request()).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request().with(guest())).andDo(print()));
        }

        @Test
        void given_invalidVersion_when_authenticated_then_409() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            when(boardCategoryService.updateBoardCategory(any(), any(), any(), any(), anyBoolean(), anyInt())).thenThrow(new OptimisticLockException(boardId, 5, 10));

            assert409(mockMvc.perform(request(boardId, categoryId, UpdateBoardCategoryDto.builder().label("bar").icon("ico").enabled(true).version(5).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardCategoryService, times(1)).updateBoardCategory(boardId, categoryId, "bar", "ico", true, 5);
        }

        protected MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID(), UUID.randomUUID(), UpdateBoardCategoryDto.builder().build());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, UUID categoryId, UpdateBoardCategoryDto payload) {
            var request = put("/budget/board/" + boardId + "/category/" + categoryId).contentType(MediaType.APPLICATION_JSON).content(json(payload));
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class DeleteBoardCategory {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();
            var expected = BoardCategory.builder().boardId(boardId).categoryId(categoryId).label("lab").icon("ico").enabled(true).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardCategoryService.deleteBoardCategory(boardId, categoryId)).thenReturn(expected);

            mockMvc.perform(request(boardId, categoryId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.label").value("lab")) //
                    .andExpect(jsonPath("$.icon").value("ico")) //
                    .andExpect(jsonPath("$.enabled").value(true)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardCategoryService, times(1)).deleteBoardCategory(boardId, categoryId);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request()).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request().with(guest())).andDo(print()));
        }

        protected MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID(), UUID.randomUUID());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, UUID categoryId) {
            var request = delete("/budget/board/" + boardId + "/category/" + categoryId);
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class GetBoardCategories {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var categoryId1 = UUID.randomUUID();
            var categoryId2 = UUID.randomUUID();
            var expected1 = BoardCategory.builder().boardId(boardId).categoryId(categoryId1).label("lab1").icon("ico1").enabled(true).version(1).creationDate(now()).lastUpdate(now()).build();
            var expected2 = BoardCategory.builder().boardId(boardId).categoryId(categoryId2).label("lab2").icon("ico2").enabled(true).version(2).creationDate(now()).lastUpdate(now()).build();
            var expected = Arrays.asList(expected1, expected2);

            when(boardCategoryService.getBoardCategories(boardId)).thenReturn(expected);

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].categoryId").value(categoryId1.toString())) //
                    .andExpect(jsonPath("$[0].version").value(1)) //
                    .andExpect(jsonPath("$[0].label").value("lab1")) //
                    .andExpect(jsonPath("$[0].icon").value("ico1")) //
                    .andExpect(jsonPath("$[0].enabled").value(true)) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //

                    .andExpect(jsonPath("$[1].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[1].categoryId").value(categoryId2.toString())) //
                    .andExpect(jsonPath("$[1].version").value(2)) //
                    .andExpect(jsonPath("$[1].label").value("lab2")) //
                    .andExpect(jsonPath("$[1].icon").value("ico2")) //
                    .andExpect(jsonPath("$[1].enabled").value(true)) //
                    .andExpect(jsonPath("$[1].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[1].lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardCategoryService, times(1)).getBoardCategories(boardId);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request()).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request().with(guest())).andDo(print()));
        }

        protected MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId) {
            var request = get("/budget/board/" + boardId + "/category");
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }
}
