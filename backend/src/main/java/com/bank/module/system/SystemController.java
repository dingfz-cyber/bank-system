package com.bank.module.system;

import com.bank.common.*;
import com.bank.rbac.RequireRole;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.lang.management.*;
import java.sql.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/system")
@RequireRole("SYS_ADMIN")
public class SystemController {

    private final DataSource dataSource;
    public SystemController(DataSource ds){ this.dataSource=ds; }

    @GetMapping("/monitor")
    public Result<Map<String,Object>> monitor(){
        Map<String,Object> m=new LinkedHashMap<>();
        try{
            // 数据库信息
            try(Connection c=dataSource.getConnection()){
                m.put("dbVersion",c.getMetaData().getDatabaseProductVersion());
                m.put("dbUrl",c.getMetaData().getURL());
                // 表数量
                var rs=c.createStatement().executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='bank_demo'");
                rs.next(); m.put("tableCount",rs.getInt(1));
                // 总数据行数
                var rs2=c.createStatement().executeQuery("SELECT SUM(table_rows) FROM information_schema.tables WHERE table_schema='bank_demo'");
                rs2.next(); m.put("totalRows",rs2.getLong(1));
            }
            // JVM信息
            Runtime rt=Runtime.getRuntime();
            m.put("javaVersion",System.getProperty("java.version"));
            m.put("totalMemory",rt.totalMemory()/1024/1024+" MB");
            m.put("freeMemory",rt.freeMemory()/1024/1024+" MB");
            m.put("usedMemory",(rt.totalMemory()-rt.freeMemory())/1024/1024+" MB");
            m.put("processors",rt.availableProcessors());
            // 运行时长
            long uptime=ManagementFactory.getRuntimeMXBean().getUptime();
            m.put("uptime",uptime/3600000+"h "+(uptime%3600000)/60000+"m");
            m.put("currentTime",LocalDateTime.now().toString().replace("T"," "));
        }catch(Exception e){ m.put("error",e.getMessage()); }
        return Result.success(m);
    }
}
