package tech.hidetora.securityStarter.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

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
public class APIResponse {
    private String message;
    private Object data;
    private boolean success;
    private HttpStatus status;
    private String error;
    private int statusCode;
    private String timestamp;

}
