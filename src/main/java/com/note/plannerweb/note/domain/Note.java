package com.note.plannerweb.note.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.note.plannerweb.member.domain.Member;
import com.note.plannerweb.note.dto.NoteUpdateRequest;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long number;//문제 번호

    @Column(nullable = false)
    private String subject;//문제 제목

    @Column(nullable = false)
    private String description;//문제 설명

    @Column(nullable = false)
    private String category;//오답 유형

    @Column(nullable = false)
    private String code;//코드 내용

    private String memo;//메모

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime targetDate;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "note",cascade = CascadeType.REMOVE)
    private List<NoteReview> noteReviews=new ArrayList<>();

    public void update(Long number,String subject,String description,String category,String code,String memo,List<NoteReview> noteReviews){
        this.number=number;
        this.subject=subject;
        this.description=description;
        this.category=category;
        this.code=code;
        this.memo=memo;
        this.noteReviews=noteReviews;
    }

    public void setMember(Member member){
        this.member=member;
    }

}
