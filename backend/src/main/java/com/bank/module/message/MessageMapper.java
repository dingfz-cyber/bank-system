package com.bank.module.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT COUNT(*) FROM sys_message WHERE user_id=#{userId} AND is_read=0")
    int countUnread(Long userId);
}
