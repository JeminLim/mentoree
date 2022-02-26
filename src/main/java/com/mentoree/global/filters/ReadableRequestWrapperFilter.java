package com.mentoree.global.filters;

import com.mentoree.global.filters.util.ReadableRequestWrapper;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ReadableRequestWrapperFilter implements Filter {

    private static final String[] excludePath = {"/api/members/join",
                                                "/swagger-ui",
                                                "/swagger-ui.html/**",
                                                "/swagger-resources/**",
                                                "/v2/api-docs",
                                                "/webjars/**"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean isMatch = false;
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        for (String path : excludePath) {
            if(pathMatcher.match(path, requestURI)){
                isMatch = true;
                break;
            }
        }

        if(!isMatch) {
            ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest) request);
            chain.doFilter(wrapper, response);
        } else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }
}
