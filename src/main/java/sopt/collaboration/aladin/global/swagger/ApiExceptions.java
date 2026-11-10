package sopt.collaboration.aladin.global.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sopt.collaboration.aladin.global.exception.BusinessException;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiExceptions {

    Class<? extends BusinessException>[] value();
}
