package de.ml.boot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;

public class AllowedUserProvider {

    private final String USER_FILE = "./users.txt";

    @Inject
    private Logger log;

    @Singleton
    @Produces
    @AllowedUsers
    private Map<String, String> getUsers(){
        Map<String,String> users = new HashMap<>();
        try(Scanner userFile  = new Scanner(new FileInputStream(USER_FILE), StandardCharsets.UTF_8.toString())) {
            while(userFile.hasNextLine()){
                String line = userFile.nextLine();
                String[] pair = line.split(":");
                //skip if no name:pw pair
                if(pair.length == 2 && !Strings.isNullOrEmpty(pair[0]) && !Strings.isNullOrEmpty(pair[1])){
                    users.put(pair[0],pair[1]);
                } else {
                    log.warn("Skipped line \"" + line + "\" because it has not form user:password" );
                }
            }
        } catch (FileNotFoundException e) {
            log.warn("users.txt not found in current directory.");
        }
        return users;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface AllowedUsers {

    }

}
