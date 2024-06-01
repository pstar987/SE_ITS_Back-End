package com.se.its;

import com.se.its.domain.member.application.MemberService;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.application.ProjectService;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.domain.project.presentation.SwingProjectController;
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
        MemberRepository memberRepository = context.getBean(MemberRepository.class);
        MemberService memberService = new MemberService(memberRepository);
        SwingMemberController swingMemberController = new SwingMemberController(memberService);

        ProjectRepository projectRepository = context.getBean(ProjectRepository.class);
        ProjectMemberRepository projectMemberRepository = context.getBean(ProjectMemberRepository.class);
        ProjectService projectService = new ProjectService(projectRepository,memberRepository,projectMemberRepository);
        SwingProjectController swingProjectController = new SwingProjectController(projectService);
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage(swingMemberController,swingProjectController);
            loginPage.setVisible(true);
        });
    }

}
