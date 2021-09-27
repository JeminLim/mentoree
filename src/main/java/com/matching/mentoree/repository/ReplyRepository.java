package com.matching.mentoree.repository;

import com.matching.mentoree.domain.Board;
import com.matching.mentoree.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyCustomRepository {


}
