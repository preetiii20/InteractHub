package com.interacthub.admin_service.service;

import com.interacthub.admin_service.model.AuditLog;
import com.interacthub.admin_service.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String username, String role, String action, String endpoint, String method) {
        AuditLog log = new AuditLog(username, role, action, endpoint, method);
        auditLogRepository.save(log);
    }

    public void log(String username, String role, String action, String endpoint, String method, 
                   String ipAddress, String userAgent) {
        AuditLog log = new AuditLog(username, role, action, endpoint, method);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public List<AuditLog> getAuditLogsByUser(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }

    public List<AuditLog> getAuditLogsByRole(String role) {
        return auditLogRepository.findByRoleOrderByTimestampDesc(role);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
    }

    public long getTotalAuditLogs() {
        return auditLogRepository.count();
    }
}
