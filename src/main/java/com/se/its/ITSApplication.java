package com.se.its;

import com.se.its.domain.issue.application.IssueService;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.application.MemberService;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.application.ProjectService;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.domain.project.presentation.SwingProjectController;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import com.se.its.view.pages.LoginPage;
import javax.swing.SwingUtilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class})
@EnableJpaAuditing
public class ITSApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = SpringApplication.run(ITSApplication.class, args);
        //SpringApplication.run(ITSApplication.class, args);
        MemberRepository memberRepository = context.getBean(MemberRepository.class);
        DtoConverter dtoConverter = context.getBean(DtoConverter.class);
        EntityValidator entityValidator = context.getBean(EntityValidator.class);

        ProjectRepository projectRepository = context.getBean(ProjectRepository.class);
        ProjectMemberRepository projectMemberRepository = context.getBean(ProjectMemberRepository.class);
        IssueRepository issueRepository = context.getBean(IssueRepository.class);

        MemberService memberService = new MemberService(memberRepository, dtoConverter,entityValidator);
        ProjectService projectService = new ProjectService(projectRepository, projectMemberRepository,issueRepository, dtoConverter, entityValidator);
        IssueService issueService = new IssueService(issueRepository, dtoConverter, entityValidator);

        SwingMemberController swingMemberController = new SwingMemberController(memberService);
        SwingProjectController swingProjectController = new SwingProjectController(projectService);
        SwingIssueController swingIssueController = new SwingIssueController(issueService);

        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage(swingMemberController, swingProjectController, swingIssueController);
            loginPage.setVisible(true);
        });
    }

}
