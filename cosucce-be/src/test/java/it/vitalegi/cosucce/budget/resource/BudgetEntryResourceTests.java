package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardEntryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardEntryDto;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.service.BoardEntryService;
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

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import static it.vitalegi.cosucce.budget.service.BudgetUtil.AMOUNT1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.AMOUNT2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DATE1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DATE2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DESCRIPTION1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.DESCRIPTION2;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.ETAG2;
import static it.vitalegi.cosucce.util.JsonUtil.json;
import static it.vitalegi.cosucce.util.MockAuth.guest;
import static it.vitalegi.cosucce.util.MockAuth.member;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert401;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert403;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert409;
import static it.vitalegi.cosucce.util.MockMvcUtil.getUserId;
import static org.mockito.ArgumentMatchers.any;
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
public class BudgetEntryResourceTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardEntryService boardEntryService;
    @MockitoBean
    BudgetAuthorizationService budgetAuthorizationService;
    @Autowired
    BudgetUtil budgetUtil;

    final static UUID ACCOUNT1 = UUID.randomUUID();
    final static UUID ACCOUNT2 = UUID.randomUUID();
    final static UUID CATEGORY1 = UUID.randomUUID();
    final static UUID CATEGORY2 = UUID.randomUUID();

    @Nested
    class AddBoardEntry {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var entryId = UUID.randomUUID();

            var request = budgetUtil.addBoardEntryDto1(ACCOUNT1, CATEGORY1).entryId(entryId).build();
            var expected = budgetUtil.entry1().boardId(boardId).entryId(entryId).accountId(ACCOUNT1).categoryId(CATEGORY1).lastUpdatedBy(userId).build();

            when(boardEntryService.addBoardEntry(any(), eq(request), eq(userId))).thenReturn(expected);

            mockMvc.perform(request(boardId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$.etag").value(ETAG1)) //
                    .andExpect(jsonPath("$.date").value(DATE1.format(DateTimeFormatter.ISO_DATE))) //
                    .andExpect(jsonPath("$.accountId").value(ACCOUNT1.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(CATEGORY1.toString())) //
                    .andExpect(jsonPath("$.description").value(DESCRIPTION1)) //
                    .andExpect(jsonPath("$.amount").value(AMOUNT1)) //
                    .andExpect(jsonPath("$.lastUpdatedBy").value(userId.toString())) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardEntryService, times(1)).addBoardEntry(boardId, request, userId);
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
            return request(boardId, AddBoardEntryDto.builder().build());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, AddBoardEntryDto payload) {
            var request = post("/budget/board/" + boardId + "/entry").contentType(MediaType.APPLICATION_JSON).content(json(payload));
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class UpdateBoardEntry {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var entryId = UUID.randomUUID();

            var request = budgetUtil.updateBoardEntryDto1().build();
            var expected = budgetUtil.entry2().entryId(entryId).boardId(boardId).accountId(ACCOUNT2).categoryId(CATEGORY2).lastUpdatedBy(userId).build();

            when(boardEntryService.updateBoardEntry(boardId, entryId, request, userId)).thenReturn(expected);

            mockMvc.perform(request(boardId, entryId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$.etag").value(ETAG2)) //
                    .andExpect(jsonPath("$.date").value(DATE2.format(DateTimeFormatter.ISO_DATE))) //
                    .andExpect(jsonPath("$.accountId").value(ACCOUNT2.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(CATEGORY2.toString())) //
                    .andExpect(jsonPath("$.description").value(DESCRIPTION2)) //
                    .andExpect(jsonPath("$.amount").value(AMOUNT2)) //
                    .andExpect(jsonPath("$.lastUpdatedBy").value(userId.toString())) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
            verify(boardEntryService, times(1)).updateBoardEntry(boardId, entryId, request, userId);
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
            var entryId = UUID.randomUUID();
            var dto = budgetUtil.updateBoardEntryDto1().build();

            when(boardEntryService.updateBoardEntry(boardId, entryId, dto, userId)).thenThrow(new OptimisticLockException(boardId, "5", "10"));

            assert409(mockMvc.perform(request(boardId, entryId, dto).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
            verify(boardEntryService, times(1)).updateBoardEntry(boardId, entryId, dto, userId);
        }

        protected MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID(), UUID.randomUUID(), UpdateBoardEntryDto.builder().build());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, UUID entryId, UpdateBoardEntryDto payload) {
            var request = put("/budget/board/" + boardId + "/entry/" + entryId).contentType(MediaType.APPLICATION_JSON).content(json(payload));
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class DeleteBoardEntry {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var entryId = UUID.randomUUID();

            mockMvc.perform(request(boardId, entryId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.WRITE);
            verify(boardEntryService, times(1)).deleteBoardEntry(boardId, entryId);
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

        protected MockHttpServletRequestBuilder request(UUID boardId, UUID entryId) {
            var request = delete("/budget/board/" + boardId + "/entry/" + entryId);
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class GetBoardEntries {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            var entryId1 = UUID.randomUUID();
            var entryId2 = UUID.randomUUID();
            var expected1 = budgetUtil.entry1().boardId(boardId).entryId(entryId1).accountId(accountId).categoryId(categoryId).lastUpdatedBy(userId).build();
            var expected2 = budgetUtil.entry2().boardId(boardId).entryId(entryId2).accountId(accountId).categoryId(categoryId).lastUpdatedBy(userId).build();
            var expected = Arrays.asList(expected1, expected2);

            when(boardEntryService.getBoardEntries(boardId)).thenReturn(expected);

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].entryId").value(entryId1.toString())) //
                    .andExpect(jsonPath("$[0].etag").value(ETAG1)) //
                    .andExpect(jsonPath("$[0].date").value(DATE1.format(DateTimeFormatter.ISO_DATE))) //
                    .andExpect(jsonPath("$[0].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$[0].description").value(DESCRIPTION1)) //
                    .andExpect(jsonPath("$[0].amount").value(AMOUNT1)) //
                    .andExpect(jsonPath("$[0].lastUpdatedBy").value(userId.toString())) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //

                    .andExpect(jsonPath("$[1].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[1].entryId").value(entryId2.toString())) //
                    .andExpect(jsonPath("$[1].etag").value(ETAG2)) //
                    .andExpect(jsonPath("$[1].date").value(DATE2.format(DateTimeFormatter.ISO_DATE))) //
                    .andExpect(jsonPath("$[1].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[1].categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$[1].description").value(DESCRIPTION2)) //
                    .andExpect(jsonPath("$[1].amount").value(AMOUNT2)) //
                    .andExpect(jsonPath("$[1].lastUpdatedBy").value(userId.toString())) //
                    .andExpect(jsonPath("$[1].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[1].lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardEntryService, times(1)).getBoardEntries(boardId);
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
            var request = get("/budget/board/" + boardId + "/entry");
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }
}
