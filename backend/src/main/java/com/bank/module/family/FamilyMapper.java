package com.bank.module.family;

import org.apache.ibatis.annotations.*;
import java.util.*;

@Mapper
public interface FamilyMapper {
    @Select("SELECT * FROM family WHERE id=#{id}") Map<String,Object> findFamilyById(@Param("id") Long id);
    @Select("SELECT * FROM family_member WHERE user_id=#{uid} AND status=1 LIMIT 1") Map<String,Object> findMemberByUser(@Param("uid") Long uid);
    @Select("SELECT * FROM family_member WHERE id=#{id}") Map<String,Object> findMemberById(@Param("id") Long id);
    @Select("SELECT * FROM family_member WHERE family_id=#{fid} AND status=1") List<Map<String,Object>> findMembersByFamily(@Param("fid") Long fid);
    @Select("SELECT * FROM family WHERE status=0") List<Map<String,Object>> findPendingFamilies();
    @Select("SELECT * FROM utility_account WHERE family_id=#{fid}") List<Map<String,Object>> findAccountsByFamily(@Param("fid") Long fid);

    @Insert("INSERT INTO family(creator_id,address,phone,status) VALUES(#{uid},#{addr},#{phone},0)")
    void insertFamily(@Param("uid") Long uid, @Param("addr") String addr, @Param("phone") String phone);

    @Insert("INSERT INTO family_member(family_id,user_id,role,status) VALUES(#{fid},#{uid},#{role},0)")
    void insertMember(@Param("fid") Long fid, @Param("uid") Long uid, @Param("role") String role);

    @Insert("INSERT INTO family_member(family_id,user_id,role,status,creator_approved) VALUES(#{fid},#{uid},'户主',1,1)")
    void insertMemberAsCreator(@Param("fid") Long fid, @Param("uid") Long uid);

    @Select("SELECT id FROM sys_user WHERE phone=#{phone} LIMIT 1")
    Long findUserIdByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM family WHERE account_no=#{no} AND status=1 LIMIT 1")
    Map<String,Object> findFamilyByAccount(@Param("no") String no);

    @Select("SELECT * FROM family_member WHERE status=0 AND creator_approved=1")
    List<Map<String,Object>> findPendingMembers();

    @Update("UPDATE family_member SET creator_approved=#{approved} WHERE id=#{id}")
    void updateCreatorApproved(@Param("id") Long id, @Param("approved") int approved);

    @Update("UPDATE family_member SET status=#{status} WHERE id=#{id}")
    void updateMemberStatus(@Param("id") Long id, @Param("status") int status);

    @Delete("DELETE FROM family_member WHERE id=#{id}")
    void removeMember(@Param("id") Long id);

    @Update("UPDATE family SET account_no=#{acc} WHERE id=#{id}")
    void setAccountNo(@Param("id") Long id, @Param("acc") String acc);

    @Update("UPDATE family SET status=#{status} WHERE id=#{id}")
    void updateFamilyStatus(@Param("id") Long id, @Param("status") int status);

    @Insert("INSERT INTO utility_account(family_id,user_id,type,account_no,holder_name) VALUES(#{fid},#{uid},#{type},#{no},'户主')")
    void insertAccount(@Param("fid") Long fid, @Param("uid") Long uid, @Param("type") String type, @Param("no") String no);
}
