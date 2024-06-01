package com.se.its.domain.comment.application;

import com.se.its.domain.comment.domain.repository.CommentRepository;
import com.se.its.global.util.dto.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final DtoConverter dtoConverter;
}
