package libreria.libreria.item.model;

import jakarta.persistence.*;
import libreria.libreria.user.model.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    private int remaining;
    private String category;
    private String publishedYear;

    @Column(columnDefinition = "integer default 0")
    private int good;

    @Builder
    public Item(Long id, String title, String content, Users users, String author, int remaining, String category, String publishedYear, int good) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.users = users;
        this.author = author;
        this.remaining = remaining;
        this.category = category;
        this.publishedYear = publishedYear;
        this.good = good;
    }
}
