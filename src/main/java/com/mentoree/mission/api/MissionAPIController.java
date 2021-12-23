package com.mentoree.mission.api;

import com.mentoree.board.repository.BoardRepository;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.mission.service.MissionService;
import com.mentoree.mission.api.dto.MissionDTO;
import com.mentoree.board.api.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.mentoree.board.api.dto.BoardDTO.*;
import static com.mentoree.mission.api.dto.MissionDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MissionAPIController {

    private final MissionService missionService;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;


    @GetMapping("/program/{programId}/mission/open")
    public ResponseEntity getCurrentMission(@PathVariable("programId") long programId) {
        List<MissionDTO> currentMission = missionRepository.findCurrentMission(programId);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", currentMission);
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/program/{programId}/mission/close")
    public ResponseEntity getPastMission(@PathVariable("programId") long programId) {
        List<MissionDTO> endedMission = missionRepository.findEndedMission(programId);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", endedMission);
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity getMissionInfo(@PathVariable("missionId") long missionId) {
        MissionDTO findMission = missionRepository.findMissionById(missionId).orElseThrow(NoSuchElementException::new);
        List<BoardInfo> findBoards = boardRepository.findAllBoardInfoById(missionId);
        Map<String, Object> data = new HashMap<>();
        data.put("mission", findMission);
        data.put("boardList", findBoards);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/mission")
    public ResponseEntity createMission(@RequestBody MissionCreateRequest missionDTO) {
        missionService.createMission(missionDTO);
        return ResponseEntity.ok().body("success");
    }



}
