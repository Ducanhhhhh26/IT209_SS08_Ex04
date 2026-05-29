package springboot.hackathon_it211.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Pointcut("execution(* springboot.hackathon_it211.service.RoomService.createRoom(..)) || " +
              "execution(* springboot.hackathon_it211.service.RoomService.updateRoom(..)) || " +
              "execution(* springboot.hackathon_it211.service.RoomService.partialUpdateRoom(..))")
    public void createOrUpdateMethods() {}

    @Before("createOrUpdateMethods()")
    public void logMethodName(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Đang thực thi phương thức: " + methodName);
    }
}
