package com.br.requirementhub.dtos.comments;

import com.br.requirementhub.entity.CommentReaction;
import com.br.requirementhub.entity.Requirement;
import com.br.requirementhub.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentsRequestDto {

    private String description;
    private Requirement requirement;
    private User user;
    private String avatarUser;
    private List<CommentReaction> reactions;
}
