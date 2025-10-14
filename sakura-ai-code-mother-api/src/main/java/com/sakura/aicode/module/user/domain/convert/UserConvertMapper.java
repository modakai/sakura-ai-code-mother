package com.sakura.aicode.module.user.domain.convert;

import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConvertMapper {

    UserConvertMapper INSTANCE = Mappers.getMapper(UserConvertMapper.class);

    LoginUserVO toVo(User user);

}
