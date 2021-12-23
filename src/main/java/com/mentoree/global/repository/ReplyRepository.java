package com.mentoree.global.repository;

import com.mentoree.reply.domain.Reply;
import com.mentoree.reply.repository.ReplyCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyCustomRepository {


}
