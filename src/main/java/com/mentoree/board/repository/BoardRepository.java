package com.mentoree.board.repository;

import com.mentoree.board.domain.Board;
import com.mentoree.member.domain.Member;
import com.mentoree.mission.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository{

}
