package com.mentoree.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserFactory.class)
public @interface WithCustomMockUser {

    String username() default "test@email.com";
    String password() default "";
    String role() default "USER";

}
