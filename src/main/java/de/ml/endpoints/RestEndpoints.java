package de.ml.endpoints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.restlet.RestletEndpoint;
import org.restlet.data.Method;

import com.google.common.base.Preconditions;

import de.ml.boot.AllowedUserProvider.AllowedUsers;
import de.ml.boot.ArgsConfiguration.Port;

public class RestEndpoints {

    private Map<String, String> users;
    private CamelContext context;
    private Integer port;
    public static final String HEADER_AUTO_PARAMETER = "autoTime";
    public static final String HEADER_NAME_PARAMETER = "inName";
    public static final String AUTO_PATH = "auto";
    public static final String INFO_PATH = "info";

    @Inject
    private RestEndpoints(CamelContext context, @AllowedUsers Map<String, String> users, @Port Integer port) {
        this.context = Preconditions.checkNotNull(context);
        this.users = Preconditions.checkNotNull(users);
        this.port = Preconditions.checkNotNull(port);

    }

    @Produces
    @Next
    private Endpoint next() {
        return getRestEndpoint("/next");
    }

    @Produces
    @NextAuto
    private Endpoint nextAuto() {
        return getRestEndpoint("/next/" + AUTO_PATH);
    }

    @Produces
    @NextAutoTime
    private Endpoint nextAutoTime() {
        return getRestEndpoint("/next/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}");
    }

    @Produces
    @Update
    private Endpoint update() {
        return getRestEndpoint("/update");
    }

    @Produces
    @Prev
    private Endpoint prev() {
        return getRestEndpoint("/prev");
    }

    @Produces
    @Info
    private Endpoint info() {
        return getRestEndpoint("/" + INFO_PATH);
    }

    @Produces
    @Current
    private Endpoint current() {
        return getRestEndpoint("/current");
    }

    @Produces
    @FilterName
    private Endpoint filterName() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}");
    }

    @Produces
    @FilterNameAuto
    private Endpoint filterNameAuto() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH);
    }

    @Produces
    @FilterNameInfo
    private Endpoint filterNameInfo() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + INFO_PATH);
    }

    @Produces
    @FilterNameAutoTime
    private Endpoint filterNameAutoTime() {
        return getRestEndpoint("/{" + HEADER_NAME_PARAMETER + "}/" + AUTO_PATH + "/{" + HEADER_AUTO_PARAMETER + "}");
    }

    private Endpoint getRestEndpoint(String path) {
        RestletEndpoint endpoint = context.getEndpoint("restlet:http://localhost" + path, RestletEndpoint.class);
        endpoint.setRestletMethod(Method.GET);
        endpoint.setPort(port);
        endpoint.setRestletRealm(users);
        return endpoint;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Next {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface NextAuto {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface NextAutoTime {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Update {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Prev {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Info {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface Current {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface FilterName {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface FilterNameAuto {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface FilterNameInfo {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface FilterNameAutoTime {

    }
}
