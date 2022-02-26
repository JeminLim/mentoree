package com.mentoree.integration;

import com.mentoree.board.domain.Board;
import com.mentoree.board.repository.BoardRepository;
import com.mentoree.category.domain.Category;
import com.mentoree.category.repository.CategoryRepository;
import com.mentoree.config.security.UserPrincipal;
import com.mentoree.config.security.util.AESUtils;
import com.mentoree.config.security.util.EncryptUtils;
import com.mentoree.config.security.util.JwtUtils;
import com.mentoree.config.security.util.SecurityConstant;
import com.mentoree.global.domain.UserRole;
import com.mentoree.global.repository.ReplyRepository;
import com.mentoree.global.repository.TokenRepository;
import com.mentoree.member.domain.Member;
import com.mentoree.member.repository.MemberRepository;
import com.mentoree.mission.domain.Mission;
import com.mentoree.mission.repository.MissionRepository;
import com.mentoree.participants.domain.Participant;
import com.mentoree.participants.repository.ParticipantRepository;
import com.mentoree.program.domain.Program;
import com.mentoree.program.domain.ProgramRole;
import com.mentoree.program.repository.ProgramRepository;
import com.mentoree.program.service.ProgramService;
import com.mentoree.reply.domain.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.*;

import static com.mentoree.config.security.util.SecurityConstant.ACCESS_TOKEN_COOKIE;
import static com.mentoree.config.security.util.SecurityConstant.UUID_COOKIE;

@Component
public class DataPreparation {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    EncryptUtils encryptUtils;
    @Autowired
    TokenRepository tokenRepository;

    private Map<String, Object> entityMap = new HashMap<>();
    private Map<String, Cookie> cookieMap = new HashMap<>();


    /**
     * ProgramA - host : MemberA, Mentor: MemberA, Participants: MemberA, MemberB(mentee)
     * ProgramB - host : MemberB, Mentor: MemberB, Participants: MemberB
     *
     * MemberC - programA applicant - accept
     * MemberD - programA applicant - reject
     *
     */
    public void init() {
        Member memberA = createMember("memberA@email.com", "memberA", "nickA", "1234qwer!@QW");
        Member memberB = createMember("memberB@email.com", "memberB", "nickB", "1234qwer!@QW");
        Member memberC = createMember("memberC@email.com", "memberC", "nickC", "1234qwer!@QW");
        Member memberD = createMember("memberD@email.com", "memberD", "nickD", "1234qwer!@QW");

        Category categoryA = createCategory("categoryA");
        Category categoryB = createCategory("categoryB");

        Program programA = createProgram("programA", categoryA, "programA goal", 6, LocalDate.now().plusDays(7), "programA description");
        Program programB = createProgram("programB", categoryB, "programB goal", 6, LocalDate.now().plusDays(7), "programB description");
        Program programC = createProgram("programC", categoryA, "programC goal", 4, LocalDate.now().plusDays(7), "programC description");

        Participant pgApartA = createParticipant(memberA, programA, true, true, ProgramRole.MENTOR, "");
        Participant pgApartB = createParticipant(memberB, programA, true, false, ProgramRole.MENTEE, "apply message");
        Participant pgBpartB = createParticipant(memberB, programB, true, true, ProgramRole.MENTOR, "");
        Participant pgBpartA = createParticipant(memberA, programB, true, false, ProgramRole.MENTEE, "apply message");
        Participant pgCpartA = createParticipant(memberC, programC, true, true, ProgramRole.MENTOR, "");

        Participant pgAapplicantA = createParticipant(memberC, programA, false, false, ProgramRole.MENTEE, "apply message");
        Participant pgAapplicantB = createParticipant(memberD, programA, false, false, ProgramRole.MENTEE, "apply message");
        Participant pgBapplicantA = createParticipant(memberA, programB, false, false, ProgramRole.MENTEE, "apply message");

        Mission missionA = createMission(programA, "missionA goal", "missionA content", "missionA", LocalDate.now().plusDays(3));
        Board boardA = createBoard(missionA, "boardA content", memberB);
        Reply replyA = createReply(boardA, "reply content", memberA);

        Mission missionB = createMission(programC, "missionA goal", "missionA content", "missionA", LocalDate.now().plusDays(3));
        Board boardB = createBoard(missionB, "boardA content", memberC);
        Reply replyB = createReply(boardB, "reply content", memberC);

        entityMap.put("memberA", memberA);
        entityMap.put("memberB", memberB);
        entityMap.put("memberC", memberC);
        entityMap.put("memberD", memberD);
        entityMap.put("categoryA", categoryA);
        entityMap.put("categoryB", categoryB);
        entityMap.put("programA", programA);
        entityMap.put("programB", programB);
        entityMap.put("programC", programC);
        entityMap.put("pgApartA", pgApartA);
        entityMap.put("pgApartB", pgApartB);
        entityMap.put("pgBpartA", pgBpartA);
        entityMap.put("pgBpartB", pgBpartB);
        entityMap.put("pgAapplicantA", pgAapplicantA);
        entityMap.put("pgAapplicantB", pgAapplicantB);
        entityMap.put("pgBapplicantA", pgBapplicantA);
        entityMap.put("missionA", missionA);
        entityMap.put("boardA", boardA);
        entityMap.put("replyA", replyA);
        entityMap.put("missionB", missionB);
        entityMap.put("boardB", boardB);
        entityMap.put("replyB", replyB);
    }

    public void initLogin() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.getKey()));
        UserPrincipal principal = UserPrincipal.builder().email("memberA@email.com").password("").authorities(authorities).build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String randomUUID = UUID.randomUUID().toString();
        String encryptedUUID = encryptUtils.encrypt(randomUUID);
        String ip = "127.0.0.1";
        String accessToken = jwtUtils.generateAccessToken(encryptedUUID, encryptUtils.encrypt(ip));

        Cookie tokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        Cookie uuidCookie = new Cookie(UUID_COOKIE, encryptedUUID);

        cookieMap.put(ACCESS_TOKEN_COOKIE, tokenCookie);
        cookieMap.put(UUID_COOKIE, uuidCookie);
    }

    public Map<String, Object> getEntityMap() {
        return this.entityMap;
    }

    public Map<String, Cookie> getCookieMap() {
        return this.cookieMap;
    }

    private Member createMember(String email, String memberName, String nickname, String password) {
        return memberRepository.save(Member.builder()
                .email(email)
                .memberName(memberName)
                .nickname(nickname)
                .userPassword(passwordEncoder.encode(password))
                .build());
    }

    private Category createCategory(String categoryName) {
        return categoryRepository.save(Category.builder()
                .categoryName(categoryName)
                .build());
    }

    private Program createProgram(String programName, Category category, String goal, int maxMember, LocalDate dueDate, String description) {
        return programRepository.save(Program.builder()
                .programName(programName)
                .category(category)
                .goal(goal)
                .maxMember(maxMember)
                .dueDate(dueDate)
                .description(description)
                .build());
    }

    private Participant createParticipant(Member member, Program program, boolean approval, boolean isHost, ProgramRole role, String message) {
        return participantRepository.save(Participant.builder()
                .member(member)
                .program(program)
                .approval(approval)
                .isHost(isHost)
                .role(role)
                .message(message)
                .build());
    }

    private Mission createMission(Program program, String goal, String content, String title, LocalDate dueDate) {
        return missionRepository.save(Mission.builder()
                .program(program)
                .goal(goal)
                .content(content)
                .title(title)
                .dueDate(dueDate)
                .build());
    }

    private Board createBoard(Mission mission, String content, Member writer) {
        return boardRepository.save(Board.builder()
                .mission(mission)
                .content(content)
                .writer(writer)
                .build());
    }

    private Reply createReply(Board board, String content, Member writer) {
        return replyRepository.save(Reply.builder()
                .board(board)
                .content(content)
                .writer(writer)
                .build());
    }



}
