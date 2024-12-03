package com.ai.hackemotion.role;

import com.ai.hackemotion.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roleList")
    @JsonIgnore
    private List<User> userList;

    // Time
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime localDateTime;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
