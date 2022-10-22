package libreria.libreria.comment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import libreria.libreria.item.model.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;  //users와 연관관계를 맺을 이유가 딱히 없음, 작성자.

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private Item item;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Builder
    public Comment(Long id, String user, String content, Item item, LocalDateTime createdDate) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.item = item;
        this.createdDate = createdDate;
    }
}
