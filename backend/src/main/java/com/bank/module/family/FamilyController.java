package com.bank.module.family;

import com.bank.common.*;
import com.bank.module.message.Message;
import com.bank.module.message.MessageMapper;
import com.bank.rbac.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyMapper mapper;
    private final MessageMapper messageMapper;

    private void sendMsg(Long userId, String title, String content){
        Message m=new Message(); m.setUserId(userId); m.setTitle(title); m.setContent(content);
        m.setType("family"); m.setIsRead(0); messageMapper.insert(m);
    }

    private Long getUserId(HttpServletRequest r){Object a=r.getAttribute("userId");if(a==null)throw new NotLoginException("未登录");return Long.parseLong(a.toString());}

    /** 我的家庭信息 */
    @GetMapping("/my")
    public Result<Map<String,Object>> my(HttpServletRequest r){
        Long uid=getUserId(r);
        Map<String,Object> member=mapper.findMemberByUser(uid);
        if(member==null) return Result.success(null);
        Map<String,Object> family=mapper.findFamilyById((Long)member.get("family_id"));
        if(family==null) return Result.success(null);
        family.put("myRole",member.get("role"));
        family.put("members",mapper.findMembersByFamily((Long)family.get("id")));
        family.put("accounts",mapper.findAccountsByFamily((Long)family.get("id")));
        return Result.success(family);
    }

    /** 创建家庭申请 */
    @PostMapping("/create")
    public Result<Void> create(@RequestBody Map<String,String> body, HttpServletRequest r){
        Long uid=getUserId(r);
        if(mapper.findMemberByUser(uid)!=null) throw new BusinessException("你已有所属家庭");
        mapper.insertFamily(uid,body.get("address"),body.get("phone"));
        sendMsg(uid,"家庭申请已提交","您的家庭创建申请已提交，请等待管理员审批。");
        return Result.success();
    }

    /** 户主主动添加成员 → 直接提交 operator */
    @PostMapping("/add-member")
    public Result<Void> addMember(@RequestBody Map<String,String> body, HttpServletRequest r){
        Long uid=getUserId(r);
        Map<String,Object> my=mapper.findMemberByUser(uid);
        if(my==null || !"户主".equals(my.get("role"))) throw new BusinessException("只有户主可以添加成员");
        Long targetId=mapper.findUserIdByPhone(body.get("phone"));
        if(targetId==null) throw new BusinessException("该手机号未注册");
        if(mapper.findMemberByUser(targetId)!=null) throw new BusinessException("该用户已有所属家庭");
        Long fid=(Long)my.get("family_id");
        mapper.insertMember(fid, targetId, body.getOrDefault("role","成员"));
        // 找到刚插入的成员ID，标记户主已通过
        Map<String,Object> newM=mapper.findMemberByUser(targetId);
        if(newM!=null) mapper.updateCreatorApproved((Long)newM.get("id"), 1);
        sendMsg(targetId,"户主邀请加入家庭","户主已将您添加为家庭成员，请等待管理员审批。");
        return Result.success();
    }

    /** 加入家庭申请（先到户主） */
    @PostMapping("/join")
    public Result<Void> join(@RequestBody Map<String,String> body, HttpServletRequest r){
        Long uid=getUserId(r);
        if(mapper.findMemberByUser(uid)!=null) throw new BusinessException("你已有所属家庭");
        Map<String,Object> fam=mapper.findFamilyByAccount(body.get("accountNo"));
        if(fam==null || (Integer)fam.get("status")!=1) throw new BusinessException("户号不存在或家庭未通过审核");
        mapper.insertMember((Long)fam.get("id"), uid, body.getOrDefault("role","成员"));
        sendMsg((Long)fam.get("creator_id"),"家庭成员申请","有用户申请加入您的家庭，请查看并审批。");
        return Result.success();
    }

    /** 户主一级审批 */
    @PostMapping("/creator-approve/{memberId}")
    public Result<Void> creatorApprove(@PathVariable Long memberId, @RequestParam int approved, HttpServletRequest r){
        Long uid=getUserId(r);
        Map<String,Object> m=mapper.findMemberById(memberId);
        if(m==null) throw new BusinessException("成员不存在");
        Map<String,Object> fam=mapper.findFamilyById((Long)m.get("family_id"));
        if(fam==null||!fam.get("creator_id").equals(uid)) throw new BusinessException("只有户主可以审批");
        mapper.updateCreatorApproved(memberId, approved);
        if(approved==1) sendMsg((Long)m.get("user_id"),"户主已通过申请","户主已批准您的加入申请，已提交管理员最终审批。");
        else sendMsg((Long)m.get("user_id"),"申请被拒绝","户主拒绝了您加入该家庭的申请。");
        return Result.success();
    }

    /** 管理员审批家庭创建 */
    @PostMapping("/admin/approve/{familyId}")
    @RequireRole("OPERATOR")
    public Result<Void> adminApprove(@PathVariable Long familyId, @RequestParam int status){
        mapper.updateFamilyStatus(familyId,status);
        Map<String,Object> fam=mapper.findFamilyById(familyId);
        Long uid=(Long)fam.get("creator_id");
        if(status==1){
            String acc="FAM"+String.format("%06d",familyId);
            mapper.setAccountNo(familyId,acc);
            String[][] types={{"water",acc},{"electric",acc},{"gas",acc},{"broadband",acc}};
            for(String[] t:types) mapper.insertAccount(familyId,uid,t[0],t[1]);
            mapper.insertMemberAsCreator(familyId,uid);
            sendMsg(uid,"家庭创建已通过","您的家庭已通过审批，户号为 "+acc+"。");
        } else sendMsg(uid,"家庭申请被驳回","您的家庭创建申请未通过审批。");
        return Result.success();
    }

    /** 管理员二级审批成员 */
    @PostMapping("/admin/approve-member/{memberId}")
    @RequireRole("OPERATOR")
    public Result<Void> adminApproveMember(@PathVariable Long memberId, @RequestParam int status){
        mapper.updateMemberStatus(memberId, status==1?1:2);
        Map<String,Object> m=mapper.findMemberById(memberId);
        if(m!=null){
            sendMsg((Long)m.get("user_id"),status==1?"加入家庭已通过":"加入家庭被驳回","您的家庭成员申请"+(status==1?"已通过":"被驳回")+"审批。");
        }
        return Result.success();
    }

    /** 管理员查待二审成员 */
    @GetMapping("/admin/pending-members")
    @RequireRole("OPERATOR")
    public Result<List<Map<String,Object>>> adminPendingMembers(){
        return Result.success(mapper.findPendingMembers());
    }

    /** 户主删除成员 */
    @PostMapping("/remove-member/{memberId}")
    public Result<Void> removeMember(@PathVariable Long memberId, HttpServletRequest r){
        Long uid=getUserId(r);
        Map<String,Object> m=mapper.findMemberById(memberId);
        if(m==null) throw new BusinessException("成员不存在");
        Map<String,Object> fam=mapper.findFamilyById((Long)m.get("family_id"));
        if(fam==null||!fam.get("creator_id").equals(uid)) throw new BusinessException("只有户主可以操作");
        mapper.removeMember(memberId);
        return Result.success();
    }

    /** 管理员查看待审批家庭 */
    @GetMapping("/admin/pending")
    @RequireRole("OPERATOR")
    public Result<List<Map<String,Object>>> adminPending(){
        return Result.success(mapper.findPendingFamilies());
    }
}
