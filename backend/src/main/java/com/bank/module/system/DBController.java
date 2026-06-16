package com.bank.module.system;

import com.bank.common.*;
import com.bank.rbac.RequireRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/db")
@RequireRole("SYS_ADMIN")
public class DBController {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPass;

    private String getDbName(){ return dbUrl.replaceAll(".*/(\\w+)\\?.*","$1"); }
    private String getHost(){ return dbUrl.replaceAll(".*://(.*):(\\d+)/.*","$1"); }
    private String getPort(){ return dbUrl.replaceAll(".*://.*:(\\d+)/.*","$1"); }

    /** 列出备份文件 */
    @GetMapping("/backups")
    public Result<List<Map<String,String>>> listBackups(){
        List<Map<String,String>> list=new ArrayList<>();
        try{
            Path dir=Path.of("backups"); Files.createDirectories(dir);
            Files.list(dir).filter(f->f.toString().endsWith(".sql")).sorted((a,b)->b.compareTo(a)).forEach(f->{
                Map<String,String> m=new HashMap<>(); m.put("name",f.getFileName().toString());
                try{ m.put("size",Files.size(f)/1024+"KB"); }catch(Exception e){}
                list.add(m);
            });
        }catch(Exception ignored){}
        return Result.success(list);
    }

    private static final String MYSQL_BIN="C:/Program Files/MySQL/MySQL Server 9.5/bin/";

    /** 备份数据库 */
    @PostMapping("/backup")
    public Result<String> backup(){
        try{
            Path dir=Path.of("backups"); Files.createDirectories(dir);
            String fn="bank_demo_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))+".sql";
            ProcessBuilder pb=new ProcessBuilder(MYSQL_BIN+"mysqldump","-h"+getHost(),"-P"+getPort(),"-u"+dbUser,"--password="+dbPass,getDbName());
            pb.redirectErrorStream(true); pb.directory(new File("backups"));
            Process p=pb.start();
            String out=new String(p.getInputStream().readAllBytes());
            Files.writeString(dir.resolve(fn),out);
            p.waitFor();
            return Result.success(fn);
        }catch(Exception e){ return Result.error(500,"备份失败: "+e.getMessage()); }
    }

    /** 恢复数据库 */
    @PostMapping("/restore")
    public Result<Void> restore(@RequestBody Map<String,String> body){
        String fn=body.get("name");
        try{
            Path f=Path.of("backups",fn);
            if(!Files.exists(f)) return Result.error(404,"备份文件不存在");
            ProcessBuilder pb=new ProcessBuilder(MYSQL_BIN+"mysql","-h"+getHost(),"-P"+getPort(),"-u"+dbUser,"-p"+dbPass,"--force",getDbName());
            pb.redirectInput(f.toFile()); pb.redirectErrorStream(true);
            Process p=pb.start();
            String err=new String(p.getInputStream().readAllBytes());
            p.waitFor();
            if(p.exitValue()!=0) return Result.error(500,"恢复失败: "+err.substring(0,Math.min(200,err.length())));
            return Result.success();
        }catch(Exception e){ return Result.error(500,"恢复失败: "+e.getMessage()); }
    }
}
