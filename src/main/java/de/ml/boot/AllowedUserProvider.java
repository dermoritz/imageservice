package de.ml.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;

public class AllowedUserProvider {

    @Singleton
    @Produces
    @AllowedUsers
    private Map<String, String> getUsers(){
        return ImmutableMap.of("user1", "awdrg");
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface AllowedUsers {

    }

}
