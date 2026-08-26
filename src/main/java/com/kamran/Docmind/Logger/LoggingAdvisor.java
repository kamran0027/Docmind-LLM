package com.kamran.Docmind.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


@Component
public class LoggingAdvisor implements BaseAdvisor{

    public static final Logger log=LoggerFactory.getLogger(LoggingAdvisor.class); 

    public static final String START_TIME="logging.start_time";

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain advisorChain) {
        long start=System.currentTimeMillis();
        request.context().put(START_TIME,start);

        log.info("============ AI-request Start ==================");
        log.info("Request : {}",request);
        log.info("==================================================");
        System.out.println("============ AI-request Start ==================");
        System.out.println("Request : "+request);
        System.out.println("==================================================");
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain advisorChain) {
        long start=(Long)response.context().get(START_TIME);
        long duration=(System.currentTimeMillis()-start);
        System.out.println("================ AI Response ====================");
        System.out.println("response : "+response);
        System.out.println("AI request completed in = "+duration+ "ms");
        System.out.println("===================================================");
        log.info("================ AI Response ====================");
        log.info("response : {}",response);
        log.info("AI request completed int {} ms",duration);
        log.info("===================================================");
        return response;
    }

    

    @Override
    public String getName() {
        return "Logging Advisor";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }



}