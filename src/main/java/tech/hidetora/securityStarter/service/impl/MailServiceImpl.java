package tech.hidetora.securityStarter.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.service.MailService;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${base.url}")
    private String baseUrl;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    @Autowired
    @Lazy
    private MailService self;

    @Async
    @Override
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(emailFrom);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    @Override
    public void sendEmailFromTemplate(AppUser user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getUsername());
            return;
        }
        Locale locale = Locale.forLanguageTag(Locale.ENGLISH.toLanguageTag());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, baseUrl);
        String content = templateEngine.process(templateName, context);
        self.sendEmail(user.getEmail(), titleKey, content, false, true);
    }

    @Async
    @Override
    public void sendActivationEmail(AppUser user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        String subject = "Demo01 account activation is required";
        self.sendEmailFromTemplate(user, "mail/activationEmail", subject);
    }

    @Async
    @Override
    public void sendCreationEmail(AppUser user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        self.sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    @Override
    public void sendPasswordResetMail(AppUser user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        self.sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }
}
