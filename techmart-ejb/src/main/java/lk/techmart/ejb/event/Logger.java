package lk.techmart.ejb.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lk.techmart.core.annotation.Console;
import lk.techmart.core.annotation.Order;

@ApplicationScoped
public class Logger {
    public void log(@Observes String message){
        System.out.println("Logger: "+message);
    }

    public void consoleLog(@Observes @Console String message){
        System.out.println("TechMart Logger: "+message);
    }

    public void orderLog(@Observes @Order String message){
        System.out.println("Order Logger: "+message);
    }

}
