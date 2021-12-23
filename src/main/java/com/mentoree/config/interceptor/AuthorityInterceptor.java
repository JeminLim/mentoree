package com.mentoree.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mentoree.program.api.dto.ProgramDTO.*;

@Slf4j
@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        List<ParticipatedProgramDTO> participatedProgram = (List<ParticipatedProgramDTO>) request.getSession().getAttribute("participatedPrograms");

        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String programId = (String)pathVariables.get("programId");

        Optional<ParticipatedProgramDTO> findProgram = participatedProgram.stream().filter(p -> p.getId() == Long.parseLong(programId)).findAny();

        if(findProgram.isPresent()){
            return true;
        } else {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.println("<script> alert('프로그램 참가자가 아닙니다.'); history.go(-1);</script>");
            writer.flush();
            writer.close();
            return false;
        }
    }



}
