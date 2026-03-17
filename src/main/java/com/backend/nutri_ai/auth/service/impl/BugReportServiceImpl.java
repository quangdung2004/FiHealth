package com.backend.nutri_ai.auth.service.impl;

import com.backend.nutri_ai.auth.dto.request.CreateBugReportRequest;
import com.backend.nutri_ai.auth.dto.response.BugReportAdminResponse;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.auth.entity.BugReport;
import com.backend.nutri_ai.auth.mapper.BugReportMapper;
import com.backend.nutri_ai.auth.repository.BugReportRepository;
import com.backend.nutri_ai.auth.service.impl.analytics.UserEventService;
import com.backend.nutri_ai.auth.service.inf.BugReportService;
import com.backend.nutri_ai.common.enums.BugPriority;
import com.backend.nutri_ai.common.enums.BugStatus;
import com.backend.nutri_ai.common.enums.UserEventType;
import com.backend.nutri_ai.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BugReportServiceImpl implements BugReportService {

    private final BugReportRepository bugReportRepository;
    private final SecurityUtils securityUtils;
    private final UserEventService eventService;
    @Override
    public void create(CreateBugReportRequest req) {
        AppUser currentUser = securityUtils.getCurrentUser();

        BugReport bug = new BugReport();
        bug.setUser(currentUser);
        bug.setTitle(req.getTitle());
        bug.setDescription(req.getDescription());
        bug.setScreen(req.getScreen());

        bugReportRepository.save(bug);
    }

    @Override
    public Page<BugReportAdminResponse> list(Pageable pageable) {
        return bugReportRepository.findAll(pageable)
                .map(BugReportMapper::toAdminResponse);
    }


    @Override
    public void updatePriority(UUID id, BugPriority priority) {
        BugReport bug = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BugReport not found: " + id));

        bug.setPriority(priority);
        bugReportRepository.save(bug);
    }

    @Override
    public void updateStatus(UUID id, BugStatus status) {
        BugReport bug = bugReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BugReport not found: " + id));

        bug.setStatus(status);
        bugReportRepository.save(bug);
    }
}
