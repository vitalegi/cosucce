package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardEntryDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardEntryDto;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.model.BoardEntry;
import it.vitalegi.cosucce.budget.service.BoardEntryService;
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

import java.math.BigDecimal;
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
public class BudgetEntryResourceTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardEntryService boardEntryService;
    @MockitoBean
    BudgetAuthorizationService budgetAuthorizationService;

    @Nested
    class AddBoardEntry {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var entryId = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            var expected = BoardEntry.builder().boardId(boardId).entryId(entryId).accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).lastUpdatedBy(UUID.randomUUID()).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardEntryService.addBoardEntry(any(), any(), any(), any(), any(), any(), any())).thenReturn(expected);

            mockMvc.perform(request(boardId, AddBoardEntryDto.builder().entryId(entryId).accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.description").value("desc")) //
                    .andExpect(jsonPath("$.amount").value("1")) //
                    .andExpect(jsonPath("$.lastUpdatedBy").isNotEmpty()) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardEntryService, times(1)).addBoardEntry(boardId, entryId, accountId, categoryId, "desc", BigDecimal.ONE, userId);
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
            var accountId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            var expected = BoardEntry.builder().boardId(boardId).entryId(entryId).accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).lastUpdatedBy(UUID.randomUUID()).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardEntryService.updateBoardEntry(any(), any(), any(), any(), any(), any(), any(), anyInt())).thenReturn(expected);

            mockMvc.perform(request(boardId, entryId, UpdateBoardEntryDto.builder().accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).version(5).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.description").value("desc")) //
                    .andExpect(jsonPath("$.amount").value("1")) //
                    .andExpect(jsonPath("$.lastUpdatedBy").isNotEmpty()) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardEntryService, times(1)).updateBoardEntry(boardId, entryId, accountId, categoryId, "desc", BigDecimal.ONE, userId, 5);
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
            var entryId = UUID.randomUUID();
            var accountId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            when(boardEntryService.updateBoardEntry(any(), any(), any(), any(), any(), any(), any(), anyInt())).thenThrow(new OptimisticLockException(boardId, 5, 10));

            assert409(mockMvc.perform(request(boardId, entryId, UpdateBoardEntryDto.builder().accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).version(5).build()).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardEntryService, times(1)).updateBoardEntry(boardId, entryId, accountId, categoryId, "desc", BigDecimal.ONE, userId, 5);
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
            var accountId = UUID.randomUUID();
            var categoryId = UUID.randomUUID();

            var expected = BoardEntry.builder().boardId(boardId).entryId(entryId).accountId(accountId).categoryId(categoryId).description("desc").amount(BigDecimal.ONE).lastUpdatedBy(UUID.randomUUID()).version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardEntryService.deleteBoardEntry(boardId, entryId)).thenReturn(expected);

            mockMvc.perform(request(boardId, entryId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.entryId").value(entryId.toString())) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$.description").value("desc")) //
                    .andExpect(jsonPath("$.amount").value("1")) //
                    .andExpect(jsonPath("$.lastUpdatedBy").isNotEmpty()) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
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
            var expected1 = BoardEntry.builder().boardId(boardId).entryId(entryId1).accountId(accountId).categoryId(categoryId).description("desc1").amount(BigDecimal.ONE).lastUpdatedBy(UUID.randomUUID()).version(1).creationDate(now()).lastUpdate(now()).build();
            var expected2 = BoardEntry.builder().boardId(boardId).entryId(entryId2).accountId(accountId).categoryId(categoryId).description("desc2").amount(BigDecimal.ONE).lastUpdatedBy(UUID.randomUUID()).version(2).creationDate(now()).lastUpdate(now()).build();
            var expected = Arrays.asList(expected1, expected2);

            when(boardEntryService.getBoardEntries(boardId)).thenReturn(expected);

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[0].entryId").value(entryId1.toString())) //
                    .andExpect(jsonPath("$[0].version").value(1)) //
                    .andExpect(jsonPath("$[0].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$[0].description").value("desc1")) //
                    .andExpect(jsonPath("$[0].amount").value("1")) //
                    .andExpect(jsonPath("$[0].lastUpdatedBy").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //

                    .andExpect(jsonPath("$[1].boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$[1].entryId").value(entryId2.toString())) //
                    .andExpect(jsonPath("$[1].version").value(2)) //
                    .andExpect(jsonPath("$[1].accountId").value(accountId.toString())) //
                    .andExpect(jsonPath("$[1].categoryId").value(categoryId.toString())) //
                    .andExpect(jsonPath("$[1].description").value("desc2")) //
                    .andExpect(jsonPath("$[1].amount").value("1")) //
                    .andExpect(jsonPath("$[1].lastUpdatedBy").isNotEmpty()) //
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
