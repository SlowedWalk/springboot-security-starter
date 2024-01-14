package tech.hidetora.securityStarter.service;

import org.springframework.scheduling.annotation.Async;
import tech.hidetora.securityStarter.entity.AppUser;

/**
 * @author Hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
public interface MailService {
    @Async
    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    @Async
    void sendEmailFromTemplate(AppUser user, String templateName, String titleKey);

    @Async
    void sendActivationEmail(AppUser user);

    @Async
    void sendCreationEmail(AppUser user);

    @Async
    void sendPasswordResetMail(AppUser user);
}
