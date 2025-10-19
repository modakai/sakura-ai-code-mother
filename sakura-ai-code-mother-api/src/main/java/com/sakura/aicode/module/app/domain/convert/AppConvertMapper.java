package com.sakura.aicode.module.app.domain.convert;

import com.sakura.aicode.module.app.domain.dto.AppAddRequest;
import com.sakura.aicode.module.app.domain.dto.AppUpdateRequest;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.domain.vo.AppVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppConvertMapper {

    AppConvertMapper INSTANCE = Mappers.getMapper(AppConvertMapper.class);

    App toEntity(AppAddRequest appAddRequest);

    App toEntity(AppUpdateRequest appUpdateRequest);

    AppVO toVo(App app);

    List<AppVO> toVoList(List<App> apps);
}
