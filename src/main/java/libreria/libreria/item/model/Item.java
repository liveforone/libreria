package libreria.libreria.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import libreria.libreria.user.model.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String author;  //책의 저자

    @JsonBackReference //순환 참조 방지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;  //상품 등록자

    private String saveFileName;  //파일 이름
    private int remaining;  //재고수량
    private String category;
    private String year;  //출판년도

    @Column(columnDefinition = "integer default 0")
    private int good;  //좋아요

    @Builder
    public Item(Long id, String title, String content, Users users, String author, String saveFileName, int remaining, String category, String year, int good) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.users = users;
        this.author = author;
        this.saveFileName = saveFileName;
        this.remaining = remaining;
        this.category = category;
        this.year = year;
        this.good = good;
    }
}
