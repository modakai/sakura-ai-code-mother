package com.sakura.aicode.module.user.domain.convert;

import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.domain.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConvertMapper {

    UserConvertMapper INSTANCE = Mappers.getMapper(UserConvertMapper.class);

    LoginUserVO toLoginVo(User user);

    UserVO toVo(User user);

    List<UserVO> toVoList(List<User> users);
}
