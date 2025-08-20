package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardCategoryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardCategoryDto;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.model.BoardCategory;
import it.vitalegi.cosucce.budget.service.BoardCategoryService;
import it.vitalegi.cosucce.budget.service.BudgetAuthorizationService;
import it.vitalegi.cosucce.budget.service.BudgetUtil;
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

import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ICON1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ICON2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.LABEL1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.LABEL2;
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
import static org.mockito.ArgumentMatchers.eq;
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
    @Autowired
    BudgetUtil budgetUtil;

    @Nested
    class AddBoardCategory {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();
            var request = budgetUtil.addBoardCategoryDto1().categoryId(categoryId).build();
            var expected = budgetUtil.category1().boardId(boardId).categoryId(categoryId).creationDate(now()).lastUpdate(now()).build();

            when(boardCategoryService.addBoardCategory(any(), eq(request))).thenReturn(expected);

            mockMvc.perform(request(boardId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.etag").value(expected.getEtag())) //
                    .andExpect(jsonPath("$.label").value(expected.getLabel())) //
                    .andExpect(jsonPath("$.icon").value(expected.getIcon())) //
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled())) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardCategoryService, times(1)).addBoardCategory(boardId, request);
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

            var request = budgetUtil.updateBoardCategoryDto1().build();
            var expected = budgetUtil.category2().boardId(boardId).categoryId(categoryId).build();
            when(boardCategoryService.updateBoardCategory(boardId, categoryId, request)).thenReturn(expected);

            mockMvc.perform(request(boardId, categoryId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.etag").value(ETAG2)) //
                    .andExpect(jsonPath("$.label").value(LABEL2)) //
                    .andExpect(jsonPath("$.icon").value(ICON2)) //
                    .andExpect(jsonPath("$.enabled").value(true)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
            verify(boardCategoryService, times(1)).updateBoardCategory(boardId, categoryId, request);
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
        void given_invalidEtag_when_authenticated_then_409() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            var request = budgetUtil.updateBoardCategoryDto1().build();
            when(boardCategoryService.updateBoardCategory(boardId, categoryId, request)).thenThrow(new OptimisticLockException(boardId, "5", "10"));

            assert409(mockMvc.perform(request(boardId, categoryId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
            verify(boardCategoryService, times(1)).updateBoardCategory(boardId, categoryId, request);
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

            mockMvc.perform(request(boardId, categoryId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
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
            var boardId = UUID.randomUUID();
            var categoryId1 = UUID.randomUUID();
            var categoryId2 = UUID.randomUUID();
            var expected1 = budgetUtil.category1().boardId(boardId).categoryId(categoryId1).build();
            var expected2 = budgetUtil.category2().boardId(boardId).categoryId(categoryId2).build();
            var expected = Arrays.asList(expected1, expected2);

            when(boardCategoryService.getBoardCategories(boardId)).thenReturn(expected);

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].categoryId").value(categoryId1.toString()))
                    .andExpect(jsonPath("$[0].etag").value(ETAG1)) //
                    .andExpect(jsonPath("$[0].label").value(LABEL1)) //
                    .andExpect(jsonPath("$[0].icon").value(ICON1)) //
                    .andExpect(jsonPath("$[0].enabled").value(true)) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //


                    .andExpect(jsonPath("$[1].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[1].categoryId").value(categoryId2.toString())) //
                    .andExpect(jsonPath("$[1].etag").value(ETAG2)) //
                    .andExpect(jsonPath("$[1].label").value(LABEL2)) //
                    .andExpect(jsonPath("$[1].icon").value(ICON2)) //
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
