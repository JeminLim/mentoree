package com.mentoree.mission.api;

import com.mentoree.board.repository.BoardRepository;
import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.NoAuthorityException;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.mission.service.MissionService;
import com.mentoree.mission.api.dto.MissionDTOCollection.MissionDTO;
import com.mentoree.participants.repository.ParticipantRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static com.mentoree.mission.api.dto.MissionDTOCollection.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api("Mission Controller API")
public class MissionAPIController {

    private final MissionService missionService;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;
    private final ParticipantRepository participantRepository;

    @ApiOperation(value = "미션 리스트 요청", notes = "현재 진행 여부 설정에 따른 미션 리스트 반환")
    @GetMapping("/missions/list")
    public ResponseEntity getMissionList(@ApiParam(value = "해당 미션의 프로그램 ID", required = true) @RequestParam("programId") long programId,
                                            @ApiParam(value = "미션 진행 여부", defaultValue = "true") @RequestParam(value = "isOpen", defaultValue = "true") boolean isOpen) {
        List<MissionDTO> currentMission = missionRepository.findMissionList(programId, isOpen);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", currentMission);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "미션 및 해당 수행 게시판 리스트", notes = "해당 미션의 내용 및 수행 게시판 리스트 반환")
    @GetMapping("/missions/{missionId}")
    public ResponseEntity getMissionInfo(@ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId) {
        MissionDTO findMission = missionRepository.findMissionById(missionId).orElseThrow(NoSuchElementException::new);
        List<BoardInfo> findBoards = boardRepository.findAllBoardInfoById(missionId);
        Map<String, Object> data = new HashMap<>();
        data.put("mission", findMission);
        data.put("boardList", findBoards);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "미션 생성 요청", notes = "미션 생성 요청 및 결과 반환")
    @PostMapping("/missions/new")
    public ResponseEntity createMission(@ApiParam(value = "미션 생성 폼", required = true) @RequestBody MissionCreateRequest missionDTO,
                                        BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 미션 작성 요청입니다.");
        }


        String loginUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!participantRepository.isParticipantByEmailAndProgramId(loginUser, missionDTO.getProgramId())
                || !participantRepository.isMentor(loginUser, missionDTO.getProgramId())){
            throw new NoAuthorityException("참가자가 아닙니다.");
        }

        missionService.createMission(missionDTO);
        return ResponseEntity.ok().body("success");
    }



}
