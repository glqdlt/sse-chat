package com.glqdlt.ex.chat;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author glqdlt
 */
@Controller
public class ChatController {

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping
    public String getIndex(){
        return "index.html";
    }

    @ResponseBody
    @PostMapping(path ="/chat")
    public void postChat(String message,HttpSession httpSession){
        sink.tryEmitNext(String.format("[%s] %s : %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                httpSession.getId(),
                message));
    }

    @GetMapping(path = "/sub",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getSseStreaming(){
        return sink.asFlux();
    }
}
