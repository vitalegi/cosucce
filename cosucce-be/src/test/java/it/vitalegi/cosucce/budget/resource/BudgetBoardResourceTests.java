package it.vitalegi.cosucce.budget.resource;

import it.vitalegi.cosucce.budget.constants.BoardUserPermission;
import it.vitalegi.cosucce.budget.exception.OptimisticLockException;
import it.vitalegi.cosucce.budget.model.Board;
import it.vitalegi.cosucce.budget.service.BoardService;
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
public class BudgetBoardResourceTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardService boardService;
    @MockitoBean
    BudgetAuthorizationService budgetAuthorizationService;

    @Nested
    class AddBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var expected = Board.builder().boardId(boardId).name("bar").creationDate(now()).lastUpdate(now()).build();

            when(boardService.addBoard(any())).thenReturn(expected);

            mockMvc.perform(request().with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.name").value("bar")) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(0)).checkPermission(any(), any(), any());
            verify(boardService, times(1)).addBoard(userId);
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
            var request = post("/budget/board");
            return request.contentType(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class UpdateBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var expected = Board.builder().boardId(boardId).name("bar").version(1).creationDate(now()).lastUpdate(now()).build();

            when(boardService.updateBoard(any(), any(), anyInt())).thenReturn(expected);

            mockMvc.perform(request(boardId, "bar").with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.name").value("bar")) //
                    .andExpect(jsonPath("$.version").value(1)) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardService, times(1)).updateBoard(boardId, "bar", 0);
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            assert401(mockMvc.perform(request(UUID.randomUUID(), "foo")).andDo(print()));
        }

        @Test
        void when_notAuthorized_then_403() throws Exception {
            assert403(mockMvc.perform(request(UUID.randomUUID(), "foo").with(guest())).andDo(print()));
        }

        @Test
        void given_invalidVersion_when_authenticated_then_409() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();

            when(boardService.updateBoard(any(), any(), anyInt())).thenThrow(new OptimisticLockException(boardId, 5, 10));

            assert409(mockMvc.perform(request(boardId, "bar", 5).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(jsonPath("$.error").value("OptimisticLockException")));

            verify(budgetAuthorizationService, times(1)).checkPermission(boardId, userId, BoardUserPermission.ADMIN);
            verify(boardService, times(1)).updateBoard(boardId, "bar", 5);
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, String name) {
            return request(boardId, name, 0);
        }

        protected MockHttpServletRequestBuilder request(UUID boardId, String name, int version) {
            var request = put("/budget/board/" + boardId);
            return request.contentType(MediaType.APPLICATION_JSON) //
                    .content("{\"name\": \"" + name + "\", \"version\": " + version + "}");
        }
    }

    @Nested
    class DeleteBoard {
        @Test
        void when_authenticated_then_serviceIsCalled() throws Exception {
            var auth = member();
            var userId = getUserId(mockMvc, auth);
            var boardId = UUID.randomUUID();
            var expected = Board.builder().boardId(boardId).name("bar").creationDate(now()).lastUpdate(now()).build();

            when(boardService.deleteBoard(any())).thenReturn(expected);

            mockMvc.perform(request(boardId).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.boardId").value(boardId.toString())) //
                    .andExpect(jsonPath("$.name").value("bar")) //
                    .andExpect(jsonPath("$.creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$.lastUpdate").isNotEmpty());

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
                    Board.builder().boardId(boardId1).name("foo").creationDate(now()).lastUpdate(now()).build(), //
                    Board.builder().boardId(boardId2).name("bar").creationDate(now()).lastUpdate(now()).build());

            when(boardService.getVisibleBoards(any())).thenReturn(expected);

            mockMvc.perform(request().with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$[0].boardId").value(boardId1.toString())) //
                    .andExpect(jsonPath("$[0].name").value("foo")) //
                    .andExpect(jsonPath("$[0].creationDate").isNotEmpty()) //
                    .andExpect(jsonPath("$[0].lastUpdate").isNotEmpty()) //
                    .andExpect(jsonPath("$[1].boardId").value(boardId2.toString())) //
                    .andExpect(jsonPath("$[1].name").value("bar")) //
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
