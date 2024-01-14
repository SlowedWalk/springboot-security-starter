package tech.hidetora.securityStarter.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
@Getter
@Setter
public abstract class AbstractAuditingEntity<T> {
    private static final long serialVersionUID = 1L;

    public abstract T getId();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private String createdAt = Instant.now().toString();

    @LastModifiedDate
    @Column(name = "updated_at")
    private String updatedAt = Instant.now().toString();
}
