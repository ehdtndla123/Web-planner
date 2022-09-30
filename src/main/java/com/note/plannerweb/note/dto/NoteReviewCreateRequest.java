package com.note.plannerweb.note.dto;

import com.note.plannerweb.note.domain.NoteReview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class NoteReviewCreateRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate repeat_time;

    private Boolean repeat_complete;

    public NoteReview toEntity(){
        return NoteReview.builder()
                .repeat_time(repeat_time)
                .repeat_complete(repeat_complete)
                .build();
    }
}
