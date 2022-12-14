package com.note.plannerweb.note.repository;

import com.note.plannerweb.member.domain.Member;
import com.note.plannerweb.note.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Page<Note> findAll(Pageable pageable);
    Optional<Note> findByMember(Member member);
}
