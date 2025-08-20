package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.dto.AddBoardDto;
import it.vitalegi.cosucce.budget.dto.UpdateBoardDto;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.service.BoardService;
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
import static it.vitalegi.cosucce.budget.service.BudgetUtil.NAME1;
import static it.vitalegi.cosucce.budget.service.BudgetUtil.NAME2;
import static it.vitalegi.cosucce.util.JsonUtil.json;
import static it.vitalegi.cosucce.util.MockAuth.guest;
import static it.vitalegi.cosucce.util.MockAuth.member;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert401;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert403;
import static it.vitalegi.cosucce.util.MockMvcUtil.assert409;
import static it.vitalegi.cosucce.util.MockMvcUtil.getUserId;
import static java.time.Instant.now;
import static org.mockito.ArgumentMatchers.any;
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
public class BudgetBoardResourceTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardService boardService;
    @MockitoBean
    BudgetAuthorizationService budgetAuthorizationService;
    @Autowired
    BudgetUtil budgetUtil;

    @Nested
    class AddBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var request = budgetUtil.addBoardDto1().boardId(boardId).build();
            var expected = budgetUtil.board1().boardId(boardId).build();

            when(boardService.addBoard(request, userId)).thenReturn(expected);

            mockMvc.perform(request(request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.etag").value(ETAG1)) //
                    .andExpect(jsonPath("$.name").value(NAME1)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardService, times(1)).addBoard(request, userId);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request(budgetUtil.addBoardDto1().build())).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request(budgetUtil.addBoardDto1().build()).with(guest())).andDo(print()));
        }

        protected MockHttpServletRequestBuilder request(AddBoardDto payload) {
            var request = post("/budget/board");
            return request.contentType(MediaType.APPLICATION_JSON).content(json(payload));
        }
    }

    @Nested
    class UpdateBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var request = budgetUtil.updateBoardDto1().build();
            var expected = budgetUtil.board2().boardId(boardId).build();
            when(boardService.updateBoard(boardId, request)).thenReturn(expected);

            mockMvc.perform(request(boardId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.name").value(NAME2)) //
                    .andExpect(jsonPath("$.etag").value(ETAG2)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardService, times(1)).updateBoard(boardId, request);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request(UUID.randomUUID(), budgetUtil.updateBoardDto1().build())).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request(UUID.randomUUID(), budgetUtil.updateBoardDto1().build()).with(guest())).andDo(print()));
        }

        @Test
        void given_invalidEtag_when_authenticated_then_409() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();

            var request = budgetUtil.updateBoardDto1().build();
            when(boardService.updateBoard(boardId, request)).thenThrow(new OptimisticLockException(boardId, "5", "10"));

            assert409(mockMvc.perform(request(boardId, request).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardService, times(1)).updateBoard(boardId, request);
        }

        protected MockHttpServletRequestBuilder request() {
            return request(UUID.randomUUID(), budgetUtil.updateBoardDto1().build());
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, UpdateBoardDto dto) {
            var request = put("/budget/board/" + boardId);
            return request.contentType(MediaType.APPLICATION_JSON) //
                    .content("{\"name\": \"" + dto.getName() + "\", \"etag\": \"" + dto.getEtag() + "\", \"newETag\": \"" + dto.getNewETag() + "\" }");
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardService, times(1)).deleteBoard(boardId);
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
            var request = delete("/budget/board/" + boardId);
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class GetBoards {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId1 = UUID.randomUUID();
            var boardId2 = UUID.randomUUID();
            var expected = Arrays.asList( //
                    Board.builder().boardId(boardId1).name("foo").etag("1").creationDate(now()).lastUpdate(now()).build(), //
                    Board.builder().boardId(boardId2).name("bar").etag("2").creationDate(now()).lastUpdate(now()).build());

            when(boardService.getVisibleBoards(any())).thenReturn(expected);

            mockMvc.perform(request().with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId1.toString())) //
                    .andExpect(jsonPath("$[0].name").value("foo")) //
                    .andExpect(jsonPath("$[0].etag").value("1")) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //
                    .andExpect(jsonPath("$[1].boardId").value(boardId2.toString())) //
                    .andExpect(jsonPath("$[1].name").value("bar")) //
                    .andExpect(jsonPath("$[1].etag").value("2")) //
                    .andExpect(jsonPath("$[1].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[1].lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardService, times(1)).getVisibleBoards(userId);
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
            var request = get("/budget/board");
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }
}
