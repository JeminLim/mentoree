package com.mentoree.mission.api;

import com.mentoree.board.repository.BoardRepository;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.mission.service.MissionService;
import com.mentoree.mission.api.dto.MissionDTOCollection.MissionDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/programs")
@Api("Mission Controller API")
public class MissionAPIController {

    private final MissionService missionService;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;

    @ApiOperation(value = "현재 진행중인 미션 리스트 요청", notes = "마감시한이 지나지 않은 미션 리스트 반환")
    @GetMapping("/{programId}/missions/open")
    public ResponseEntity getCurrentMission(@ApiParam(value = "해당 미션의 프로그램 ID", required = true) @PathVariable("programId") long programId) {
        List<MissionDTO> currentMission = missionRepository.findCurrentMission(programId);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", currentMission);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "종료된 미션 리스트 요청", notes = "마감시한이 끝난 미션 리스트 반환")
    @GetMapping("/{programId}/missions/close")
    public ResponseEntity getPastMission(@ApiParam(value = "해당 미션의 프로그램 ID", required = true) @PathVariable("programId") long programId) {
        List<MissionDTO> endedMission = missionRepository.findEndedMission(programId);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", endedMission);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "미션 및 해당 수행 게시판 리스트", notes = "해당 미션의 내용 및 수행 게시판 리스트 반환")
    @GetMapping("/{programId}/missions/{missionId}")
    public ResponseEntity getMissionInfo(@ApiParam(value = "해당 미션의 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                         @ApiParam(value = "해당 미션 ID", required = true) @PathVariable("missionId") long missionId) {
        MissionDTO findMission = missionRepository.findMissionById(missionId).orElseThrow(NoSuchElementException::new);
        List<BoardInfo> findBoards = boardRepository.findAllBoardInfoById(missionId);
        Map<String, Object> data = new HashMap<>();
        data.put("mission", findMission);
        data.put("boardList", findBoards);
        return ResponseEntity.ok().body(data);
    }

    @ApiOperation(value = "미션 생성 요청", notes = "미션 생성 요청 및 결과 반환")
    @PostMapping("/{programId}/missions/new")
    public ResponseEntity createMission(@ApiParam(value = "해당 미션의 프로그램 ID", required = true) @PathVariable("programId") long programId,
                                        @ApiParam(value = "미션 생성 폼", required = true) @RequestBody MissionCreateRequest missionDTO,
                                        BindingResult bindingResult) {
        missionService.createMission(missionDTO);
        return ResponseEntity.ok().body("success");
    }



}
