package com.bank.module.log;

import com.bank.common.Result;
import com.bank.module.user.LoginRecord;
import com.bank.module.user.LoginRecordMapper;
import com.bank.rbac.RequireRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {

    private final LoginRecordMapper loginRecordMapper;

    @Value("${logging.file.name:logs/bank-system.log}")
    private String logPath;

    @GetMapping("/audit")
    @RequireRole({"AUDITOR", "SYS_ADMIN"})
    public Result<List<Map<String,String>>> auditLog(@RequestParam(defaultValue = "100") int lines) {
        List<Map<String,String>> result = new ArrayList<>();
        try {
            Path path = Path.of(logPath);
            if (!Files.exists(path)) return Result.success(result);
            List<String> allLines = Files.readAllLines(path);
            int start = Math.max(0, allLines.size() - lines);
            for (int i = start; i < allLines.size(); i++) {
                String line = allLines.get(i);
                if (line.contains("OperationLogAspect") || line.contains("操作=")) {
                    Map<String,String> entry = new LinkedHashMap<>();
                    entry.put("time", line.length() > 23 ? line.substring(0, 23) : "");
                    entry.put("level", extract(line));
                    entry.put("content", line.length() > 50 ? line.substring(Math.max(line.indexOf("com.bank"), 50)) : line);
                    result.add(entry);
                }
            }
        } catch (Exception ignored) {}
        return Result.success(result);
    }

    /** 安全审计员专用：全量登录记录 */
    @GetMapping("/monitor")
    @RequireRole("AUDITOR")
    public Result<Map<String,Object>> monitor() {
        List<LoginRecord> loginRecords = loginRecordMapper.selectList(
            new LambdaQueryWrapper<LoginRecord>().orderByDesc(LoginRecord::getLoginTime).last("LIMIT 50"));
        Map<String,Object> data = new LinkedHashMap<>();
        data.put("loginRecords", loginRecords);
        data.put("totalLogins", loginRecordMapper.selectCount(null));
        return Result.success(data);
    }

    private String extract(String line) {
        for (String k : new String[]{"ERROR", "WARN", "INFO", "DEBUG"})
            if (line.contains(k)) return k;
        return "INFO";
    }
}
