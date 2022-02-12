package com.mentoree.global.interceptors;

import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.participants.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
public class AuthorityInterceptor implements HandlerInterceptor {

    private final ParticipantRepository participantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        String requestURI = request.getRequestURI();

        // 접근하는 URI 가 mission 인 경우
        if(requestURI.contains("missions")) {
            String programIdString = request.getParameter("programId");
            if(programIdString == null) {
                Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                // missionId가 path variable 로 들어온 경우
                if(pathVariables.containsKey("missionId")) {
                    long missionId = Long.parseLong((String) pathVariables.get("missionId"));
                    if(!participantRepository.isParticipantByEmailAndMissionId(email, missionId)) {
                        throw new NoAuthorityException("참가자가 아닙니다");
                    }
                }
                return true;
            }

            long programId = Long.parseLong(programIdString);
            if(!participantRepository.isParticipantByEmailAndProgramId(email, programId)) {
                throw new NoAuthorityException("참가자가 아닙니다");
            }
        } // 접근하는 URI 가 board 인 경우
        else if(requestURI.contains("boards")) {
            Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if(pathVariables.containsKey("boardId")) {
                long boardId = Long.parseLong((String) pathVariables.get("boardId"));
                if(!participantRepository.isParticipantByEmailAndBoardId(email, boardId)) {
                    throw new NoAuthorityException("참가자가 아닙니다");
                }
            }
        } // 접근하는 URI 가 reply 인 경우
        else if(requestURI.contains("replies")) {
            String boardIdString = request.getParameter("boardId");
            if(boardIdString != null) {
                long boardId = Long.parseLong(boardIdString);
                if(!participantRepository.isParticipantByEmailAndBoardId(email, boardId)){
                    throw new NoAuthorityException("참가자가 아닙니다");
                }
            }
        }
        return true;
    }
}
