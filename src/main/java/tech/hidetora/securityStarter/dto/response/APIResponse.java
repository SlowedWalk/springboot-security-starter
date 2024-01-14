package tech.hidetora.securityStarter.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse extends JdkSerializationRedisSerializer implements Serializable {
    @Serial
    private static final long serialVersionUID = 7156526077883281623L;
    private String message;
    private Object data;
    private boolean success;
    private HttpStatus status;
    private String error;
    private int statusCode;
    private String timestamp;
}
